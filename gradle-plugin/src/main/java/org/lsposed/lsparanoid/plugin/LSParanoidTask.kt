/*
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

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.lsposed.lsparanoid.processor.ParanoidProcessor
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.jar.JarOutputStream

@CacheableTask
abstract class LSParanoidTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jars: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val dirs: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Classpath
    abstract val bootClasspath: ListProperty<RegularFile>

    @get:CompileClasspath
    abstract var classpath: FileCollection

    @get:Input
    abstract val seed: Property<Int>

    @get:Input
    @get:Optional
    abstract var classFilter: ((className: String) -> Boolean)?

    @get:Input
    abstract val projectName: Property<String>

    @TaskAction
    fun taskAction() {
        val inputs = jars.get() + dirs.get()
        JarOutputStream(
            BufferedOutputStream(
                FileOutputStream(
                    output.get().asFile
                )
            )
        ).use { jarOutput ->
            ParanoidProcessor(
                seed = seed.get(),
                inputs = inputs.map { it.asFile.toPath() },
                classpath = bootClasspath.get().map { it.asFile.toPath() }
                    .toSet() + classpath.files.map { it.toPath() },
                output = jarOutput,
                projectName = projectName.get(),
                classFilter = classFilter
            ).process()
        }
    }
}
