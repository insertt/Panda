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

package org.panda_lang.panda.language.structure.imports;

import org.panda_lang.panda.framework.interpreter.lexer.token.TokenType;
import org.panda_lang.panda.framework.interpreter.lexer.token.TokenUtils;
import org.panda_lang.panda.framework.interpreter.lexer.token.reader.TokenReader;
import org.panda_lang.panda.framework.interpreter.parser.pipeline.ParserHandler;

public class ImportParserHandler implements ParserHandler {

    @Override
    public boolean handle(TokenReader tokenReader) {
        return TokenUtils.equals(tokenReader.read(), TokenType.KEYWORD, "import");
    }

}
