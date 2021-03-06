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

package org.panda_lang.panda.framework.language.interpreter.parser.prototype;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.module.Module;
import org.panda_lang.panda.framework.design.architecture.module.ModulePath;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.prototype.constructor.PrototypeConstructor;
import org.panda_lang.panda.framework.design.architecture.prototype.field.PrototypeField;
import org.panda_lang.panda.framework.design.architecture.value.Value;
import org.panda_lang.panda.framework.design.interpreter.parser.PandaComponents;
import org.panda_lang.panda.framework.design.interpreter.parser.PandaPipelines;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.parser.UnifiedParser;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.BootstrapParserBuilder;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.UnifiedParserBootstrap;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.annotations.Autowired;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.annotations.Src;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.handlers.TokenHandler;
import org.panda_lang.panda.framework.design.interpreter.parser.bootstrap.layer.Delegation;
import org.panda_lang.panda.framework.design.interpreter.parser.component.UniversalComponents;
import org.panda_lang.panda.framework.design.interpreter.parser.generation.pipeline.Generation;
import org.panda_lang.panda.framework.design.interpreter.parser.linker.ScopeLinker;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.ParserPipeline;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.ParserRegistration;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.PipelineRegistry;
import org.panda_lang.panda.framework.design.interpreter.parser.pipeline.UniversalPipelines;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.design.interpreter.token.TokensUtils;
import org.panda_lang.panda.framework.design.interpreter.token.stream.SourceStream;
import org.panda_lang.panda.framework.design.runtime.ExecutableBranch;
import org.panda_lang.panda.framework.language.architecture.PandaScript;
import org.panda_lang.panda.framework.language.architecture.module.PrimitivePrototypeLiquid;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.ClassReference;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.ClassScope;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.ClassScopeInstance;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.PandaClassPrototype;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.constructor.ConstructorUtils;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.generator.ClassPrototypeTypeGenerator;
import org.panda_lang.panda.framework.language.interpreter.parser.PandaParserException;
import org.panda_lang.panda.framework.language.interpreter.parser.PandaParserFailure;
import org.panda_lang.panda.framework.language.interpreter.parser.generation.pipeline.PandaTypes;
import org.panda_lang.panda.framework.language.interpreter.parser.linker.PandaScopeLinker;
import org.panda_lang.panda.framework.language.interpreter.token.stream.PandaSourceStream;
import org.panda_lang.panda.framework.language.resource.syntax.keyword.Keywords;

@ParserRegistration(target = UniversalPipelines.OVERALL_LABEL)
public class ClassPrototypeParser extends UnifiedParserBootstrap {

    private static final ClassPrototypeTypeGenerator GENERATOR = new ClassPrototypeTypeGenerator();

    @Override
    protected BootstrapParserBuilder initialize(ParserData data, BootstrapParserBuilder defaultBuilder) {
        return defaultBuilder
                .handler(new TokenHandler(Keywords.CLASS))
                .pattern("class <name> [extends <inherited>] `{ [<*body>] `}");
    }

    @Autowired(type = PandaTypes.TYPES_LABEL)
    public void parse(ParserData data, Generation generation, @Src("name") String className) throws Exception {
        PandaScript script = data.getComponent(PandaComponents.PANDA_SCRIPT);
        Module module = script.getModule();

        if (className == null) {
            throw new PandaParserException("Class name cannot be null");
        }

        ClassPrototype classPrototype = PandaClassPrototype.builder()
                .module(module)
                .associated(GENERATOR.generateType(className))
                .name(className)
                .build();

        data.setComponent(ClassPrototypeComponents.CLASS_PROTOTYPE, classPrototype);
        module.add(classPrototype);

        ModulePath registry = data.getComponent(PandaComponents.MODULE_REGISTRY);
        classPrototype.getExtended().add(PrimitivePrototypeLiquid.OBJECT);

        data.setComponent(ClassPrototypeComponents.CLASS_PROTOTYPE, classPrototype);

        ClassScope classScope = new ClassScope(classPrototype);
        data.setComponent(ClassPrototypeComponents.CLASS_SCOPE, classScope);

        ClassReference classReference = new ClassReference(classPrototype, classScope);
        script.getStatements().add(classReference);

        ScopeLinker classScopeLinker = new PandaScopeLinker(classScope);
        data.setComponent(PandaComponents.SCOPE_LINKER, classScopeLinker);
    }

    @Autowired(type = PandaTypes.TYPES_LABEL, delegation = Delegation.CURRENT_AFTER)
    public void parseDeclaration(ParserData data, @Src("declaration") Tokens declaration) {
        if (declaration != null) {
            ClassPrototypeParserUtils.readDeclaration(data, declaration);
        }
    }

    @Autowired(type = PandaTypes.TYPES_LABEL, delegation = Delegation.NEXT_AFTER)
    public void parseBody(ParserData data, Generation generation, @Nullable @Src("*body") Tokens body) throws Throwable {
        if (TokensUtils.isEmpty(body)) {
            return;
        }

        PipelineRegistry pipelineRegistry = data.getComponent(UniversalComponents.PIPELINE);
        ParserPipeline<UnifiedParser> pipeline = pipelineRegistry.getPipeline(PandaPipelines.PROTOTYPE);

        ParserData bodyInfo = data.fork();
        SourceStream stream = new PandaSourceStream(body);
        bodyInfo.setComponent(UniversalComponents.SOURCE_STREAM, stream);

        while (stream.hasUnreadSource()) {
            UnifiedParser parser = pipeline.handle(bodyInfo, stream.toTokenizedSource());

            if (parser == null) {
                throw new PandaParserFailure("Cannot parse the element of the prototype", data.setComponent(UniversalComponents.SOURCE_STREAM, stream.updateCachedSource()));
            }

            parser.parse(bodyInfo);
        }
    }

    @Autowired(order = 1, type = PandaTypes.TYPES_LABEL)
    public void parseAfter(ParserData data) {
        ClassPrototype prototype = data.getComponent(ClassPrototypeComponents.CLASS_PROTOTYPE);
        ClassScope scope = data.getComponent(ClassPrototypeComponents.CLASS_SCOPE);

        if (prototype.getConstructors().getAmountOfConstructors() > 0) {
            return;
        }

        for (PrototypeField field : prototype.getFields().getListOfFields()) {
            if (!field.hasDefaultValue()) {
                // assign
            }
        }

        PrototypeConstructor defaultConstructor = new PrototypeConstructor() {
            @Override
            public ClassScopeInstance createInstance(ExecutableBranch branch, Value... values) {
                return scope.createInstance(branch);
            }

            @Override
            public ClassPrototype[] getParameterTypes() {
                return ConstructorUtils.PARAMETERLESS;
            }
        };

        prototype.getConstructors().addConstructor(defaultConstructor);
    }

}
