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

package org.panda_lang.panda.framework.design.architecture.prototype.generator;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.module.ModulePath;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;

public class ClassPrototypeGeneratorManager {

    protected static final ClassPrototypeGenerator generator = new ClassPrototypeGenerator();
    protected static long reflectionsTime;

    public ClassPrototype generate(@Nullable ModulePath modulePath, Class<?> clazz) {
        return generator.generate(modulePath, clazz);
    }

    public static long getReflectionsTime() {
        return reflectionsTime;
    }

    public static long getTotalLoadTime() {
        return ClassPrototypeGenerator.totalLoadTime;
    }

    public static void resetReflectionsTime() {
        reflectionsTime = 0;
    }

}