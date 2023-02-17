/*
 * Copyright 2021 Michael Rozumyanskiy
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

package org.lsposed.lsparanoid.processor

import com.joom.grip.ClassRegistry
import com.joom.grip.io.FileSource
import com.joom.grip.mirrors.Type
import com.joom.grip.mirrors.getObjectTypeByInternalName
import org.lsposed.lsparanoid.processor.commons.createDirectory
import org.lsposed.lsparanoid.processor.commons.createFile
import org.lsposed.lsparanoid.processor.logging.getLogger
import org.lsposed.lsparanoid.processor.model.Deobfuscator
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.util.jar.JarOutputStream

class Patcher(
    private val deobfuscator: Deobfuscator,
    private val stringRegistry: StringRegistry,
    private val analysisResult: AnalysisResult,
    private val classRegistry: ClassRegistry,
    private val asmApi: Int,
) {

    private val logger = getLogger()

    fun copyAndPatchClasses(sources: Sequence<FileSource>, jar: JarOutputStream) {
        sources.forEach { source ->
            copyAndPatchClasses(source, jar)
        }
    }

    private fun copyAndPatchClasses(source: FileSource, jar: JarOutputStream) {
        logger.info("Patching...")
        logger.info("   Input: {}", source)

        source.listFiles { name, type ->
            when (type) {
                FileSource.EntryType.CLASS -> copyAndPatchClass(source, jar, name)
                FileSource.EntryType.FILE -> jar.createFile(name, source.readFile(name))
                FileSource.EntryType.DIRECTORY -> jar.createDirectory(name)
            }
        }
    }

    private fun copyAndPatchClass(source: FileSource, jar: JarOutputStream, name: String) {
        if (!maybePatchClass(source, jar, name)) {
            jar.createFile(name, source.readFile(name))
        }
    }

    private fun getObjectTypeFromFile(relativePath: String): Type.Object? {
        if (relativePath.endsWith(".class")) {
            val internalName = relativePath.substringBeforeLast(".class").replace('\\', '/')
            return getObjectTypeByInternalName(internalName)
        }
        return null
    }

    private fun maybePatchClass(source: FileSource, jar: JarOutputStream, name: String): Boolean {
        val type = getObjectTypeFromFile(name) ?: run {
            logger.error("Skip patching for {}", name)
            return false
        }

        val configuration = analysisResult.configurationsByType[type]
        val hasObfuscateAnnotation =
            OBFUSCATE_TYPE in classRegistry.getClassMirror(type).annotations
        if (configuration == null && !hasObfuscateAnnotation) {
            return false
        }

        logger.debug("Patching class {}", name)
        val reader = ClassReader(source.readFile(name))
        val writer = StandaloneClassWriter(
            ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES,
            classRegistry
        )
        val shouldObfuscateLiterals = reader.access and Opcodes.ACC_INTERFACE == 0
        val patcher =
            writer
                .wrapIf(hasObfuscateAnnotation) { RemoveObfuscateClassPatcher(asmApi, it) }
                .wrapIf(configuration != null) {
                    StringLiteralsClassPatcher(
                        deobfuscator,
                        stringRegistry,
                        asmApi,
                        it
                    )
                }
                .wrapIf(configuration != null && shouldObfuscateLiterals) {
                    StringConstantsClassPatcher(
                        configuration!!,
                        asmApi,
                        it
                    )
                }
        reader.accept(patcher, ClassReader.SKIP_FRAMES)
        jar.createFile(name, writer.toByteArray())
        return true
    }

    private inline fun ClassVisitor.wrapIf(
        condition: Boolean,
        wrapper: (ClassVisitor) -> ClassVisitor
    ): ClassVisitor {
        return if (condition) wrapper(this) else this
    }
}
