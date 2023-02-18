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
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class ParanoidPlugin : Plugin<Project> {
    private lateinit var extension: ParanoidExtension

    override fun apply(project: Project) {
        extension = project.extensions.create("lsparanoid", ParanoidExtension::class.java)
        project.plugins.withType(AppPlugin::class.java) { _ ->
            project.extensions.configure("androidComponents") { it: AndroidComponentsExtension<*, *, *> ->
                it.onVariants { variant ->
                    if (!extension.enabled) return@onVariants
                    if (!extension.variantsFilter(variant)) return@onVariants
                    val task = project.tasks.register(
                        "lsparanoid${variant.name.replaceFirstChar { it.uppercase() }}",
                        ParanoidTask::class.java
                    ) {
                        it.bootClasspath.addAll(project.extensions.getByType(BaseExtension::class.java).bootClasspath)
                        it.seed.set(extension.seed)
                        it.global.set(extension.global)
                    }
                    variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                        .use(task).toTransform(
                            ScopedArtifact.CLASSES,
                            ParanoidTask::jars,
                            ParanoidTask::dirs,
                            ParanoidTask::output,
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
