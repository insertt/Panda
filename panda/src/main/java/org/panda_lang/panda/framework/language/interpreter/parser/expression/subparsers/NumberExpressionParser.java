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

package org.panda_lang.panda.framework.language.interpreter.parser.expression.subparsers;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.value.Value;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.token.TokenRepresentation;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionParser;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionSubparser;
import org.panda_lang.panda.framework.language.interpreter.parser.general.number.NumberParser;
import org.panda_lang.panda.framework.language.interpreter.parser.general.number.NumberUtils;
import org.panda_lang.panda.framework.language.interpreter.token.PandaTokens;
import org.panda_lang.panda.framework.language.resource.syntax.separator.Separators;
import org.panda_lang.panda.framework.language.runtime.expression.PandaExpression;

public class NumberExpressionParser implements ExpressionSubparser {

    @Override
    public @Nullable Tokens read(ExpressionParser main, Tokens source) {
        Tokens tokens = new PandaTokens();
        TokenRepresentation period = null;

        for (TokenRepresentation representation : source.getTokensRepresentations()) {
            if (NumberUtils.isNumeric(tokens.asString() + (period != null ? "." : "") + representation.getTokenValue())) {
                if (period != null) {
                    tokens.addToken(period);
                }

                tokens.addToken(representation);
                period = null;
                continue;
            }

            if (representation.contentEquals(Separators.PERIOD)) {
                period = representation;
                continue;
            }

            break;
        }

        if (tokens.size() < 2) {
            return null;
        }

        return tokens;
    }

    @Override
    public Expression parse(ExpressionParser main, ParserData data, Tokens source) {
        NumberParser numberParser = new NumberParser();
        Value numericValue = numberParser.parse(data, source);

        if (numericValue != null) {
            return new PandaExpression(numericValue);
        }

        return null;
    }

    @Override
    public double getPriority() {
        return DefaultSubparsers.Priorities.SIMPLE_DYNAMIC;
    }

    @Override
    public String getName() {
        return DefaultSubparsers.Names.NUMBER;
    }

}
