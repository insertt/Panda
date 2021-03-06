/*
 * Copyright (c) 2015-2019 Dzikoysk
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

package org.panda_lang.panda.framework.design.interpreter.token.stream;

import org.panda_lang.panda.framework.design.interpreter.token.TokenRepresentation;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;

public interface SourceStream {

    TokenRepresentation read();

    Tokens read(int length);

    Tokens readLineResidue();

    SourceStream restoreCachedSource();

    SourceStream updateCachedSource();

    SourceStream update(Tokens source);

    TokenReader toTokenReader();

    Tokens toTokenizedSource();

    default Tokens readDifference(Tokens source) {
        return read(source.size());
    }

    default Tokens readDifference(TokenReader reader) {
        return read(reader.getIndex() + 1);
    }

    default boolean hasUnreadSource() {
        return toTokenizedSource().size() > 0;
    }

    default int getUnreadLength() {
        return toTokenizedSource().size();
    }

    default int getCurrentLine() {
        return hasUnreadSource() ? toTokenizedSource().getFirst().getLine() : -2;
    }

}
