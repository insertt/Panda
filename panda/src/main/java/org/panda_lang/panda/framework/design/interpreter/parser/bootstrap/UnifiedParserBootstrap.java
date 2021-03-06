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

package org.panda_lang.panda.framework.design.interpreter.parser.bootstrap;

import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.parser.UnifiedParser;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.ParserHandler;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.ParserRepresentation;
import org.panda_lang.panda.framework.design.interpreter.token.stream.SourceStream;

public abstract class UnifiedParserBootstrap<T> implements UnifiedParser<T>, ParserHandler {

    protected ParserRepresentation<UnifiedParser<T>> parser;

    protected abstract BootstrapParserBuilder<T> initialize(ParserData data, BootstrapParserBuilder<T> defaultBuilder);

    public boolean customHandle(ParserHandler handler, ParserData data, SourceStream source) {
        return handler.handle(data, source);
    }

    @Override
    public final boolean handle(ParserData data, SourceStream source) {
        return customHandle(get(data).getHandler(), data, source);
    }

    @Override
    public final T parse(ParserData data) throws Throwable {
        return get(data).getParser().parse(data);
    }

    protected ParserRepresentation<UnifiedParser<T>> get(ParserData data) {
        if (parser != null) {
            return parser;
        }

        this.parser = initialize(data, PandaParserBootstrap.<T> builder().instance(this)).build(data);
        return parser;
    }

}
