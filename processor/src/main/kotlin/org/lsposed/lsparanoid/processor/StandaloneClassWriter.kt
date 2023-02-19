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
import com.joom.grip.FileRegistry
import com.joom.grip.mirrors.ClassMirror
import com.joom.grip.mirrors.Type
import com.joom.grip.mirrors.getObjectType
import com.joom.grip.mirrors.getObjectTypeByInternalName
import org.lsposed.lsparanoid.processor.logging.getLogger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class StandaloneClassWriter : ClassWriter {
    private val logger = getLogger()
    private val classRegistry: ClassRegistry
    private val fileRegistry: FileRegistry

    constructor(
        flags: Int,
        classRegistry: ClassRegistry,
        fileRegistry: FileRegistry
    ) : super(flags) {
        this.classRegistry = classRegistry
        this.fileRegistry = fileRegistry
    }

    constructor(
        classReader: ClassReader,
        flags: Int,
        classRegistry: ClassRegistry,
        fileRegistry: FileRegistry
    ) : super(classReader, flags) {
        this.classRegistry = classRegistry
        this.fileRegistry = fileRegistry
    }

    override fun getCommonSuperClass(type1: String, type2: String): String {
        val hierarchy = HashSet<Type>()
        for (mirror in classRegistry.findClassHierarchy(getObjectTypeByInternalName(type1))) {
            hierarchy.add(mirror.type)
        }

        for (mirror in classRegistry.findClassHierarchy(getObjectTypeByInternalName(type2))) {
            if (mirror.type in hierarchy) {
                logger.debug("[getCommonSuperClass]: {} & {} = {}", type1, type2, mirror.access)
                return mirror.type.internalName
            }
        }

        logger.warn("[getCommonSuperClass]: {} & {} = NOT FOUND ", type1, type2)
        return OBJECT_INTERNAL_NAME
    }

    private fun ClassRegistry.findClassHierarchy(type: Type.Object): Sequence<ClassMirror> {
        return generateSequence(getClassMirrorOrObject(type)) {
            it.superType?.let { superType -> getClassMirrorOrObject(superType) }
        }
    }

    private fun ClassRegistry.getClassMirrorOrObject(type: Type.Object): ClassMirror? {
        return fileRegistry.findPathForType(type)?.let { _ ->
            getClassMirror(type)
        } ?: getClassMirror(OBJECT_TYPE)
    }

    companion object {
        private val OBJECT_INTERNAL_NAME = getObjectType<Any>().internalName
    }
}
