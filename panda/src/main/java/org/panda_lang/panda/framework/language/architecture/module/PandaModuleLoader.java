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

package org.panda_lang.panda.framework.language.architecture.module;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.module.Module;
import org.panda_lang.panda.framework.design.architecture.module.ModuleLoader;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.language.architecture.prototype.array.ArrayClassPrototypeUtils;
import org.panda_lang.panda.framework.language.architecture.prototype.array.PandaArray;
import org.panda_lang.panda.framework.language.runtime.PandaRuntimeException;
import org.panda_lang.panda.utilities.commons.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PandaModuleLoader implements ModuleLoader {

    private final Map<String, Module> importedModules;

    public PandaModuleLoader() {
        this.importedModules = new HashMap<>(2);
    }

    @Override
    public void include(Module module) {
        this.importedModules.put(module.getName(), module);
    }

    @Override
    public @Nullable ClassPrototype forClass(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        if (name.contains(":")) {
            throw new PandaRuntimeException("Not implemented");
        }

        if (name.endsWith(PandaArray.IDENTIFIER)) {
            return ArrayClassPrototypeUtils.obtain(this, name);
        }

        for (Module module : importedModules.values()) {
            ClassPrototype prototype = module.get(name);

            if (prototype != null) {
                return prototype;
            }
        }

        return null;
    }

    @Override
    public Module get(String name) {
        return importedModules.get(name);
    }

}
