/*
 * Copyright (c) 2015-2017 Dzikoysk
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

package org.panda_lang.framework.util;

public class BitwiseUtils {

    /**
     * Convert 2 ints to 1 long
     */
    public static long convert(int a, int b) {
        return a << 32 | b & 0xFFFFFFFFL;
    }

    /**
     * Extract 1st int from long
     */
    public static int extractLeft(long l) {
        return (int) (l >> 32);
    }

    /**
     * Extract 2nd int from long
     */
    public static int extractRight(long l) {
        int a = (int) (l >> 32);
        return (int) l;
    }

    /**
     * Extract 2 ints from long
     */
    public static int[] extract(long l) {
        return new int[]{ (int) l >> 32, (int) l };
    }

}
