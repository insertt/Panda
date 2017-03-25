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

package org.panda_lang.panda.language.structure.prototype.structure.constructor;

import org.panda_lang.panda.framework.interpreter.lexer.token.TokenType;
import org.panda_lang.panda.framework.interpreter.parser.ParserInfo;
import org.panda_lang.panda.framework.interpreter.parser.UnifiedParser;
import org.panda_lang.panda.framework.interpreter.parser.generation.ParserGeneration;
import org.panda_lang.panda.framework.interpreter.parser.generation.ParserGenerationCallback;
import org.panda_lang.panda.framework.interpreter.parser.generation.ParserGenerationLayer;
import org.panda_lang.panda.framework.interpreter.parser.generation.ParserGenerationType;
import org.panda_lang.panda.framework.interpreter.parser.generation.util.LocalCallback;
import org.panda_lang.panda.implementation.interpreter.lexer.token.pattern.TokenPattern;
import org.panda_lang.panda.implementation.interpreter.parser.pipeline.DefaultPipelines;
import org.panda_lang.panda.implementation.interpreter.parser.pipeline.registry.ParserRegistration;
import org.panda_lang.panda.implementation.interpreter.parser.util.Components;

@ParserRegistration(target = DefaultPipelines.PROTOTYPE, parserClass = ConstructorParser.class, handlerClass = ConstructorParserHandler.class)
public class ConstructorParser implements UnifiedParser {

    protected static final TokenPattern PATTERN = TokenPattern.builder()
            .unit(TokenType.KEYWORD, "constructor")
            .unit(TokenType.SEPARATOR, "(")
            .hollow()
            .unit(TokenType.SEPARATOR, ")")
            .unit(TokenType.SEPARATOR, "{")
            .hollow()
            .unit(TokenType.SEPARATOR, "}")
            .build();

    @Override
    public void parse(ParserInfo parserInfo) {
        ParserGeneration generation = parserInfo.getComponent(Components.GENERATION);

        generation.getLayer(ParserGenerationType.HIGHER)
                .delegateImmediately(new ConstructorExtractorCallback(), parserInfo.fork());
    }

    @LocalCallback
    private static class ConstructorExtractorCallback implements ParserGenerationCallback {

        @Override
        public void call(ParserInfo delegatedInfo, ParserGenerationLayer nextLayer) {

        }

    }

    @LocalCallback
    private static class ConstructorBodyCallback implements ParserGenerationCallback {

        @Override
        public void call(ParserInfo delegatedInfo, ParserGenerationLayer nextLayer) {

        }

    }

}