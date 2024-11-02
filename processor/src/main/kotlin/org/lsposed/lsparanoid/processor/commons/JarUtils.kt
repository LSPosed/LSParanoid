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

package org.lsposed.lsparanoid.processor.commons

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.ZipException

internal fun JarOutputStream.createFile(name: String, data: ByteArray) {
    try {
        putNextEntry(JarEntry(name.replace(File.separatorChar, '/')))
        write(data)
        closeEntry()
    } catch (e: ZipException) {
        // it's normal to have duplicated files in META-INF and duplicated module-info.class in root
        if (!name.startsWith("META-INF") && name != "module-info.class") throw e
    }
}

internal fun JarOutputStream.createDirectory(name: String) {
    try {
        putNextEntry(JarEntry(name.replace(File.separatorChar, '/')))
        closeEntry()
    } catch (ignored: ZipException) {
        // it's normal that the directory already exists
    }
}
