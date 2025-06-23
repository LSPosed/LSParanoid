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

import org.lsposed.lsparanoid.DeobfuscatorHelper
import org.lsposed.lsparanoid.RandomHelper
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.nio.charset.StandardCharsets

interface StringRegistry {
  fun registerString(string: String): Long
  fun getAllChunks(): List<String>
  fun cleanup()
}

class StringRegistryImpl(
  seed: Int
) : StringRegistry {

  private val seed = seed.toLong() and 0xffff_ffffL
  private val tempFile: File
  private var currentOffset: Long = 0L
  private val fileOutputStream: FileOutputStream

  init {
    tempFile = File.createTempFile("lsparanoid_strings", ".tmp")
    tempFile.deleteOnExit()
    fileOutputStream = FileOutputStream(tempFile, true) // Append mode
  }

  override fun registerString(string: String): Long {
    var mask = 0L
    var state = RandomHelper.seed(seed)
    state = RandomHelper.next(state)
    mask = mask or (state and 0xffff_0000_0000L)
    state = RandomHelper.next(state)
    mask = mask or ((state and 0xffff_0000_0000L) shl 16)

    val fileOffset = currentOffset
    val id = seed or ((fileOffset shl 32) xor mask)

    // Buffer for characters to be written for this string
    // Using a temporary StringBuilder for efficient char manipulation before writing
    val stringChars = StringBuilder()

    state = RandomHelper.next(state)
    stringChars.append((((state ushr 32) and 0xffffL) xor string.length.toLong()).toInt().toChar())

    for (char in string) {
      state = RandomHelper.next(state)
      stringChars.append((((state ushr 32) and 0xffffL) xor char.code.toLong()).toInt().toChar())
    }

    // Write the buffered characters to the file
    // Ensure we write characters, not bytes directly from string, to maintain char encoding consistency
    val charsToWrite = stringChars.toString().toCharArray()
    for (charToWrite in charsToWrite) {
        // Write char by char to handle potential multi-byte characters correctly if file encoding was different
        // However, since we control both ends and use it as a stream of chars, this should be fine.
        // A more robust way might involve explicit charset encoding/decoding if bytes were primary.
        // Here, we are essentially storing it as a sequence of obfuscated chars.
        fileOutputStream.write(charToWrite.code.shr(8) and 0xFF) // High byte
        fileOutputStream.write(charToWrite.code and 0xFF)        // Low byte
    }
    currentOffset += charsToWrite.size * 2 // Each char takes 2 bytes

    return id
  }

  override fun getAllChunks(): List<String> {
    fileOutputStream.flush() // Ensure all buffered output is written to the file
    // Closing the output stream now as we are done writing before reading for chunks
    // This is not strictly necessary if reading happens much later or by a different instance,
    // but good practice if getAllChunks is called after all registerString calls.
    // However, if registerString can be called after getAllChunks, this needs rethinking.
    // For now, assume all registrations happen before chunking.
    // fileOutputStream.close() // Re-evaluate if closing here is correct.

    val chunks = mutableListOf<String>()
    FileInputStream(tempFile).use { fis ->
      // Reading back char by char, assuming 2 bytes per char as written.
      // This is critical: the reading logic must exactly mirror the writing logic.
      val buffer = ByteArray(DeobfuscatorHelper.MAX_CHUNK_LENGTH * 2) // Each char is 2 bytes
      var bytesRead: Int
      while (fis.read(buffer).also { bytesRead = it } != -1) {
        val sb = StringBuilder(bytesRead / 2)
        for (i in 0 until bytesRead step 2) {
          if (i + 1 < bytesRead) { // Ensure there's a pair of bytes to form a char
            val highByte = buffer[i].toInt() and 0xFF
            val lowByte = buffer[i+1].toInt() and 0xFF
            sb.append((highByte shl 8 or lowByte).toChar())
          } else {
            // Handle trailing byte if any - this indicates an issue or incomplete char write
            // For now, we assume writes are always complete chars.
          }
        }
        chunks.add(sb.toString())
      }
    }
    return chunks
  }

  override fun cleanup() {
    try {
      fileOutputStream.close()
    } catch (e: Exception) {
      // Log or handle exception during close
    }
    if (tempFile.exists()) {
      tempFile.delete()
    }
  }
}
