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

package org.panda_lang.panda.language.structure.prototype;

import org.panda_lang.panda.implementation.structure.value.Value;

public class ClassInstance  {

    private final Object object;
    private final ClassPrototype prototype;
    private final Value[] fieldValues;

    public ClassInstance(ClassPrototype classPrototype) {
        this.object = this;
        this.prototype = classPrototype;
        this.fieldValues = new Value[classPrototype.getFields().size()];
    }

    public ClassPrototype getClassPrototype() {
        return prototype;
    }

}
