/*
 * Copyright (c) 2015-2018 Dzikoysk
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

package org.panda_lang.panda.language.structure.general.expression.callbacks.memory;

import org.panda_lang.panda.core.structure.value.Value;
import org.panda_lang.panda.language.runtime.ExecutableBranch;
import org.panda_lang.panda.language.runtime.PandaRuntimeException;
import org.panda_lang.panda.language.structure.general.expression.Expression;
import org.panda_lang.panda.language.structure.general.expression.ExpressionCallback;
import org.panda_lang.panda.language.structure.prototype.scope.ClassScopeInstance;
import org.panda_lang.panda.language.structure.prototype.structure.field.Field;

public class FieldExpressionCallback implements ExpressionCallback {

    private final Field field;
    private final int fieldIndex;

    public FieldExpressionCallback(Field field, int fieldIndex) {
        this.field = field;
        this.fieldIndex = fieldIndex;
    }

    @Override
    public Value call(Expression expression, ExecutableBranch bridge) {
        Object instance = bridge.getInstance();

        if (instance == null) {
            throw new PandaRuntimeException("Instance is not defined");
        }

        if (!(instance instanceof ClassScopeInstance)) {
            throw new PandaRuntimeException("Cannot get field value of external object");
        }

        ClassScopeInstance pandaInstance = (ClassScopeInstance) instance;
        Value value = pandaInstance.getFieldValues()[fieldIndex];

        if (value == null) {
            throw new PandaRuntimeException("Field '" + field.getName() + "' have not been initialized");
        }

        return value;
    }

}