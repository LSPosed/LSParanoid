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
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import java.security.SecureRandom

class LSParanoidPlugin : Plugin<Project> {
    private lateinit var extension: LSParanoidExtension

    override fun apply(project: Project) {
        extension = project.extensions.create("lsparanoid", LSParanoidExtension::class.java)
        project.plugins.withType(AndroidBasePlugin::class.java) { _ ->
            project.extensions.configure("androidComponents") { it: AndroidComponentsExtension<*, *, *> ->
                it.onVariants { variant ->
                    if (!extension.variantFilter(variant)) return@onVariants
                    val task = project.tasks.register(
                        "lsparanoid${variant.name.replaceFirstChar { it.uppercase() }}",
                        LSParanoidTask::class.java
                    ) { task ->
                        task.bootClasspath.set(it.sdkComponents.bootClasspath)
                        task.classpath = variant.compileClasspath
                        task.seed.set(extension.seed ?: SecureRandom().nextInt())
                        task.global.set(extension.global)
                    }
                    variant.artifacts.forScope(if (extension.includeDependencies) ScopedArtifacts.Scope.ALL else ScopedArtifacts.Scope.PROJECT)
                        .use(task).toTransform(
                            ScopedArtifact.CLASSES,
                            LSParanoidTask::jars,
                            LSParanoidTask::dirs,
                            LSParanoidTask::output,
                        )
                }
            }
            project.addDependencies(getDefaultConfiguration())
        }
    }

    private fun getDefaultConfiguration(): String {
        return JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME
    }

    private fun Project.addDependencies(configurationName: String) {
        val version = Build.VERSION
        dependencies.add(configurationName, "org.lsposed.lsparanoid:core:$version")
    }
}
