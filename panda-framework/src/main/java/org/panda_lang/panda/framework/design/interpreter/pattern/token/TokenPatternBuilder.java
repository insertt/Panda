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

package org.panda_lang.panda.framework.design.interpreter.pattern.token;

import org.panda_lang.panda.framework.PandaFrameworkException;
import org.panda_lang.panda.framework.design.interpreter.pattern.lexical.LexicalPatternCompiler;
import org.panda_lang.panda.framework.design.interpreter.pattern.lexical.elements.LexicalPatternElement;

public class TokenPatternBuilder {

    protected LexicalPatternElement patternContent;

    TokenPatternBuilder() { }

    public TokenPatternBuilder compile(String pattern) {
        LexicalPatternCompiler compiler = new LexicalPatternCompiler();
        compiler.enableSplittingByWhitespaces();
        compiler.setEscapeCharacter('`');

        this.patternContent = compiler.compile(pattern);
        return this;
    }

    public TokenPattern build() {
        if (patternContent == null) {
            throw new PandaFrameworkException("Pattern is not defined");
        }

        return new TokenPattern(this);
    }

}
