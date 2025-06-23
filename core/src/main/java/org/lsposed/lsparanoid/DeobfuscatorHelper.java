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

package org.lsposed.lsparanoid;

/**
 * The type Deobfuscator helper.
 */
public class DeobfuscatorHelper {
  /**
   * The constant MAX_CHUNK_LENGTH.
   */
  public static final int MAX_CHUNK_LENGTH = 0x1fff;

  private DeobfuscatorHelper() {
    // Cannot be instantiated.
  }

  /**
   * Gets string.
   *
   * @param id     the id
   * @param chunks the chunks
   * @return the string
   */
  public static String getString(final long id, final String[] chunks) {
    long state = RandomHelper.seed(id & 0xffffffffL);
    state = RandomHelper.next(state);
    final long low = (state >>> 32) & 0xffff;
    state = RandomHelper.next(state);
    final long high = (state >>> 16) & 0xffff0000;
    // The 'id >>> 32' part is the obfuscated byte offset from StringRegistryImpl
    // We need to divide by 2 to get the character offset, as each char was written as 2 bytes
    final int byteOffset = (int) ((id >>> 32) ^ low ^ high);
    final int charOffset = byteOffset / 2; // Convert byte offset to character offset

    state = getCharAt(charOffset, chunks, state); // Use charOffset for the first lookup (length char)
    final int length = (int) ((state >>> 32) & 0xffffL);
    final char[] chars = new char[length];

    for (int i = 0; i < length; ++i) {
      // Subsequent characters are sequentially located after the first one
      state = getCharAt(charOffset + i + 1, chunks, state);
      chars[i] = (char) ((state >>> 32) & 0xffffL);
    }

    return new String(chars);
  }

  private static long getCharAt(final int charIndex, final String[] chunks, final long state) {
    // charIndex is now correctly a character index within the conceptual full string
    final long nextState = RandomHelper.next(state);
    final String chunk = chunks[charIndex / MAX_CHUNK_LENGTH];
    return nextState ^ ((long) chunk.charAt(charIndex % MAX_CHUNK_LENGTH) << 32);
  }
}
