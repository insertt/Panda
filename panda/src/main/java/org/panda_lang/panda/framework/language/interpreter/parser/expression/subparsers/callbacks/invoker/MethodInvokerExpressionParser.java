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

package org.panda_lang.panda.framework.language.interpreter.parser.expression.subparsers.callbacks.invoker;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.module.ModuleLoader;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.prototype.method.PrototypeMethod;
import org.panda_lang.panda.framework.design.interpreter.parser.PandaComponents;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.pattern.abyss.AbyssPattern;
import org.panda_lang.panda.framework.design.interpreter.pattern.abyss.utils.AbyssPatternBuilder;
import org.panda_lang.panda.framework.design.interpreter.token.TokenType;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.language.architecture.PandaScript;
import org.panda_lang.panda.framework.language.architecture.prototype.clazz.method.MethodInvoker;
import org.panda_lang.panda.framework.language.interpreter.parser.PandaParserFailure;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionUtils;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.subparsers.callbacks.ExpressionCallbackParser;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.subparsers.callbacks.instance.ThisExpressionCallback;
import org.panda_lang.panda.framework.language.interpreter.parser.general.ArgumentParser;
import org.panda_lang.panda.framework.language.interpreter.parser.prototype.ClassPrototypeComponents;
import org.panda_lang.panda.framework.language.runtime.expression.PandaExpression;

public class MethodInvokerExpressionParser implements ExpressionCallbackParser<MethodInvokerExpressionCallback> {

    protected static final AbyssPattern PATTERN = new AbyssPatternBuilder()
            .simpleHollow()
            .unit(TokenType.SEPARATOR, "(")
            .hollow()
            .unit(TokenType.SEPARATOR, ")")
            .lastIndexAlgorithm(true)
            .build();

    protected static final AbyssPattern CALL_PATTERN = new AbyssPatternBuilder()
            .hollow()
            .unit(TokenType.SEPARATOR, ".")
            .simpleHollow()
            .lastIndexAlgorithm(true)
            .build();

    private final Tokens instanceSource;
    private final Tokens methodNameSource;
    private final Tokens argumentsSource;

    private MethodInvoker invoker;
    private boolean voids;

    public MethodInvokerExpressionParser(Tokens instanceSource, Tokens methodNameSource, Tokens argumentsSource) {
        this.instanceSource = instanceSource;
        this.methodNameSource = methodNameSource;
        this.argumentsSource = argumentsSource;
    }

    @Override
    public void parse(@Nullable Tokens source, ParserData data) {
        PandaScript script = data.getComponent(PandaComponents.PANDA_SCRIPT);
        ModuleLoader registry = script.getModuleLoader();

        Expression instance = null;
        ClassPrototype prototype;

        if (instanceSource != null) {
            String surmiseClassName = instanceSource.asString();
            prototype = registry.forClass(surmiseClassName);

            if (prototype == null) {
                instance = data.getComponent(PandaComponents.EXPRESSION).parse(data, instanceSource);
                prototype = instance.getReturnType();
            }
        }
        else {
            prototype = data.getComponent(ClassPrototypeComponents.CLASS_PROTOTYPE);
            instance = new PandaExpression(prototype, new ThisExpressionCallback());
        }

        ArgumentParser argumentParser = new ArgumentParser();
        Expression[] arguments = argumentParser.parse(data, argumentsSource);
        ClassPrototype[] parameterTypes = ExpressionUtils.toTypes(arguments);

        String methodName = methodNameSource.asString();
        PrototypeMethod prototypeMethod = prototype.getMethods().getMethod(methodName, parameterTypes);

        if (prototypeMethod == null) {
            throw new PandaParserFailure("Class " + prototype.getClassName() + " does not have method " + methodName, data, source);
        }

        if (!voids && prototypeMethod.isVoid()) {
            throw new PandaParserFailure("Method " + prototypeMethod.getMethodName() + " returns nothing", data, source);
        }

        this.invoker = new MethodInvoker(prototypeMethod, instance, arguments);
    }

    public void setVoids(boolean voids) {
        this.voids = voids;
    }

    public MethodInvoker getInvoker() {
        return invoker;
    }

    @Override
    public MethodInvokerExpressionCallback toCallback() {
        return new MethodInvokerExpressionCallback(invoker);
    }

}
