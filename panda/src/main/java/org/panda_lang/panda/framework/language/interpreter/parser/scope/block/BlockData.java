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

package org.panda_lang.panda.framework.language.interpreter.parser.scope.block;

import org.panda_lang.panda.framework.design.architecture.dynamic.Block;

public class BlockData {

    private final Block block;
    private final boolean unlisted;

    public BlockData(Block block, boolean unlisted) {
        this.block = block;
        this.unlisted = unlisted;
    }

    public BlockData(Block block) {
        this(block, false);
    }

    public boolean isUnlisted() {
        return unlisted;
    }

    public Block getBlock() {
        return block;
    }

}
