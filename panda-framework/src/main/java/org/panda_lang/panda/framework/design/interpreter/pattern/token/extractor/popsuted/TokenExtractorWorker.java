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

package org.panda_lang.panda.framework.design.interpreter.pattern.token.extractor.popsuted;

import org.panda_lang.panda.framework.design.interpreter.token.stream.SourceStream;
import org.panda_lang.panda.framework.design.interpreter.pattern.lexical.elements.LexicalPatternElement;
import org.panda_lang.panda.framework.design.interpreter.pattern.lexical.elements.LexicalPatternNode;
import org.panda_lang.panda.framework.language.interpreter.token.distributors.TokenDistributor;
import org.panda_lang.panda.framework.design.interpreter.pattern.token.TokenPattern;

class TokenExtractorWorker {

    protected final TokenPattern pattern;

    private final WorkerDynamicContent dynamicContent = new WorkerDynamicContent(this);
    private final WorkerWildcardContent wildcardContent = new WorkerWildcardContent(this);

    TokenExtractorWorker(TokenPattern pattern) {
        this.pattern = pattern;
    }

    TokenExtractorResult extract(SourceStream source) {
        TokenDistributor distributor = new TokenDistributor(source.toTokenizedSource());
        TokenExtractorResult result = extract(pattern.getPatternContent(), distributor);

        if (result.isMatched()) {
            source.read(distributor.getIndex());
        }

        return result;
    }

    protected TokenExtractorResult extract(LexicalPatternElement element, TokenDistributor distributor) {
        if (!distributor.hasNext()) {
            return new TokenExtractorResult("Distributor does not have content");
        }

        if (element.isUnit()) {
            return new TokenExtractorResult(element.toUnit().getValue().equals(distributor.next().getTokenValue())).identified(element.getIdentifier());
        }

        if (element.isWildcard()) {
            return wildcardContent.matchWildcard(element.toWildcard(), distributor).identified(element.getIdentifier());
        }

        LexicalPatternNode node = element.toNode();

        if (node.isVariant()) {
            return matchVariant(node, distributor).identified(node.getIdentifier());
        }

        return dynamicContent.matchDynamicContent(node, distributor).identified(node.getIdentifier());
    }


    private TokenExtractorResult matchVariant(LexicalPatternNode variantNode, TokenDistributor reader) {
        if (!variantNode.isVariant()) {
            throw new RuntimeException("The specified node is not marked as a variant node");
        }

        for (LexicalPatternElement variantElement : variantNode.getElements()) {
            TokenExtractorResult result = this.extract(variantElement, reader);

            if (result.isMatched()) {
                return result.identified(variantElement.getIdentifier());
            }
        }

        return new TokenExtractorResult("Variant does not matched");
    }

}
