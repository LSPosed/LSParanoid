/*
 * Copyright 2020 Michael Rozumyanskiy
 * Copyright 2023 LSPosed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lsposed.lsparanoid.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts.Scope
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import java.security.SecureRandom

class LSParanoidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("lsparanoid", LSParanoidExtension::class.java)
        project.plugins.withType(AndroidBasePlugin::class.java) { _ ->
            val components = project.extensions.getByType(AndroidComponentsExtension::class.java)
            components.onVariants { variant ->
                if (!extension.variantFilter(variant)) return@onVariants
                val task = project.tasks.register(
                    "lsparanoid${variant.name.replaceFirstChar { it.uppercase() }}", LSParanoidTask::class.java
                ) {
                    it.bootClasspath.set(components.sdkComponents.bootClasspath)
                    it.classpath = variant.compileClasspath
                    it.seed.set(extension.seed ?: SecureRandom().nextInt())
                    it.global.set(extension.global)
                    it.projectName.set("${project.rootProject.name}\$${project.name}")
                }
                variant.artifacts.forScope(if (extension.includeDependencies) Scope.ALL else Scope.PROJECT).use(task)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        LSParanoidTask::jars,
                        LSParanoidTask::dirs,
                        LSParanoidTask::output,
                    )
            }
            project.addDependencies()
        }
        project.tasks.withType(JavaCompile::class.java) {
            it.options.compilerArgs.add("-XDstringConcat=inline")
        }
        project.tasks.withType(KotlinCompile::class.java) {
            it.kotlinOptions.freeCompilerArgs += "-Xstring-concat=inline"
        }
    }

    private fun Project.addDependencies() {
        val version = Build.VERSION
        val configurationName = JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME
        dependencies.add(configurationName, "org.lsposed.lsparanoid:core:$version")
    }
}
