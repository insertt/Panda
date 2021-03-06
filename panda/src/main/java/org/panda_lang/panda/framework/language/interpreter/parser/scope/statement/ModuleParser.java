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

package org.panda_lang.panda.framework.language.interpreter.parser.scope.statement;

import org.panda_lang.panda.framework.design.architecture.module.Module;
import org.panda_lang.panda.framework.design.architecture.module.ModulePath;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.BootstrapParserBuilder;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.UnifiedParserBootstrap;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.annotations.Autowired;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.annotations.Component;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.annotations.Src;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.handlers.TokenHandler;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.ParserRegistration;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.UniversalPipelines;
import org.panda_lang.panda.framework.design.interpreter.token.TokenRepresentation;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.language.architecture.PandaScript;
import org.panda_lang.panda.framework.language.architecture.statement.ModuleStatement;
import org.panda_lang.panda.framework.language.interpreter.parser.PandaParserException;
import org.panda_lang.panda.framework.language.interpreter.parser.generation.pipeline.PandaTypes;
import org.panda_lang.panda.framework.language.resource.syntax.keyword.Keywords;

@ParserRegistration(target = UniversalPipelines.OVERALL_LABEL)
public class ModuleParser extends UnifiedParserBootstrap {

    @Override
    protected BootstrapParserBuilder initialize(ParserData data, BootstrapParserBuilder defaultBuilder) {
        return defaultBuilder
                .handler(new TokenHandler(Keywords.MODULE))
                .pattern("module <module:condition token {type:unknown}, token {value:-}>[;]");
    }

    @Autowired(type = PandaTypes.TYPES_LABEL)
    private void parse(ParserData data, @Component ModulePath modulePath, @Component PandaScript script, @Src("module") Tokens moduleSource) {
        StringBuilder moduleName = new StringBuilder();

        for (TokenRepresentation representation : moduleSource.getTokensRepresentations()) {
            moduleName.append(representation.getTokenValue());
        }

        String groupName = moduleName.toString();

        if (!modulePath.hasModule(groupName)) {
            modulePath.create(groupName);
        }

        if (script.select(ModuleStatement.class).size() > 0) {
            throw new PandaParserException("Script contains more than one declaration of the group");
        }

        Module module = modulePath.get(groupName);
        ModuleStatement moduleStatement = new ModuleStatement(module);

        script.setModule(module);
        script.getModuleLoader().include(module);
        script.getStatements().add(moduleStatement);
    }

}
