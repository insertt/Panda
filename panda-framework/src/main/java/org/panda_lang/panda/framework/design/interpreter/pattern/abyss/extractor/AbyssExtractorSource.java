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

package org.panda_lang.panda.framework.design.interpreter.pattern.abyss.extractor;

import org.panda_lang.panda.framework.design.interpreter.token.Token;
import org.panda_lang.panda.framework.design.interpreter.token.TokenRepresentation;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.language.runtime.PandaRuntimeException;

class AbyssExtractorSource {

    private final Tokens tokens;
    private final AbyssTokenRepresentation[] abyssRepresentations;

    protected AbyssExtractorSource(Tokens tokens) {
        this.tokens = tokens;
        this.abyssRepresentations = new AbyssTokenRepresentation[tokens.size()];
        this.prepare();
    }

    private void prepare() {
        AbyssExtractorOpposites opposites = new AbyssExtractorOpposites();

        for (int i = 0; i < tokens.size(); i++) {
            TokenRepresentation representation = tokens.get(i);

            if (representation == null) {
                throw new PandaRuntimeException("Representation is null");
            }

            Token token = representation.getToken();

            boolean levelUp = opposites.report(token);
            int nestingLevel = levelUp ? opposites.getNestingLevel() - 1 : opposites.getNestingLevel();

            AbyssTokenRepresentation abyssRepresentation = new AbyssTokenRepresentation(representation, nestingLevel);
            abyssRepresentations[i] = abyssRepresentation;
        }
    }

    protected AbyssTokenRepresentation[] getAbyssRepresentations() {
        return abyssRepresentations;
    }

}
