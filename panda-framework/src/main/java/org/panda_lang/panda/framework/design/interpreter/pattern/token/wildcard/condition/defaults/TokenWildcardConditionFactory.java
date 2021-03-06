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

package org.panda_lang.panda.framework.design.interpreter.pattern.token.wildcard.condition.defaults;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.panda_lang.panda.framework.PandaFrameworkException;
import org.panda_lang.panda.framework.design.interpreter.token.TokenRepresentation;
import org.panda_lang.panda.framework.design.interpreter.pattern.token.wildcard.condition.WildcardCondition;
import org.panda_lang.panda.framework.design.interpreter.pattern.token.wildcard.condition.WildcardConditionFactory;
import org.panda_lang.panda.framework.design.interpreter.pattern.token.wildcard.condition.WildcardConditionResult;
import org.panda_lang.panda.utilities.commons.StringUtils;

class TokenWildcardConditionFactory implements WildcardConditionFactory {

    private static final String TOKEN = "token";
    private static final String NOT_TOKEN = "not token";

    @Override
    public boolean handle(String condition) {
        return condition.startsWith(TOKEN) || condition.startsWith(NOT_TOKEN);
    }

    @Override
    public WildcardCondition create(String condition) {
        boolean renamed = false;

        if (condition.startsWith(NOT_TOKEN)) {
            condition = StringUtils.replaceFirst(condition, NOT_TOKEN, TOKEN);
            renamed = true;
        }

        String[] elements =  StringUtils.splitFirst(condition, " ");
        boolean not = renamed;

        if (elements.length < 2) {
            throw new PandaFrameworkException("Token condition does renamed contain specification");
        }

        String source = elements[1]
                .replace("{", "{" + System.lineSeparator())
                .replace("}", System.lineSeparator() + "}");

        JsonObject conditions = JsonValue
                .readHjson(source)
                .asObject();

        return representation -> check(conditions, representation).negate(not);
    }

    private WildcardConditionResult check(JsonObject conditions, TokenRepresentation representation) {
        if (!equals("type", conditions, representation.getTokenType())) {
            return WildcardConditionResult.NEUTRAL;
        }

        if (!equals("value", conditions, representation.getTokenValue())) {
            return WildcardConditionResult.NEUTRAL;
        }

        return WildcardConditionResult.ALLOWED;
    }

    private boolean equals(String key, JsonObject conditions, String value) {
        return !conditions.names().contains(key) || value.equalsIgnoreCase(conditions.get(key).asString());
    }

}
