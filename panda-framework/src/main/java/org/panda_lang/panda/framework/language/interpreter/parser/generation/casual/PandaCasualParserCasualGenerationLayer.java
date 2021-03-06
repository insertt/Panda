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

package org.panda_lang.panda.framework.language.interpreter.parser.generation.casual;

import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.parser.generation.casual.CasualParserGenerationCallback;
import org.panda_lang.panda.framework.design.interpreter.parser.generation.casual.CasualParserGenerationUnit;
import org.panda_lang.panda.framework.design.interpreter.parser.generation.casual.CasualGenerationLayer;

import java.util.ArrayList;
import java.util.List;

class PandaCasualParserCasualGenerationLayer implements CasualGenerationLayer {

    private final List<CasualParserGenerationUnit> before;
    private final List<CasualParserGenerationUnit> delegates;
    private final List<CasualParserGenerationUnit> after;

    public PandaCasualParserCasualGenerationLayer() {
        this.before = new ArrayList<>(1);
        this.delegates = new ArrayList<>();
        this.after = new ArrayList<>(1);
    }

    @Override
    public void call(ParserData currentData, CasualGenerationLayer nextLayer) throws Exception {
        call(before, currentData, nextLayer);
        call(delegates, currentData, nextLayer);
        call(after, currentData, nextLayer);
    }

    private void call(List<CasualParserGenerationUnit> units, ParserData currentInfo, CasualGenerationLayer nextLayer) throws Exception {
        List<CasualParserGenerationUnit> unitList = new ArrayList<>(units);
        units.clear();

        for (CasualParserGenerationUnit unit : unitList) {
            CasualParserGenerationCallback callback = unit.getCallback();
            ParserData delegatedInfo = unit.getDelegated();
            callback.call(delegatedInfo, nextLayer);
        }
    }

    @Override
    public CasualGenerationLayer delegateBefore(CasualParserGenerationCallback callback, ParserData delegated) {
        return delegate(before, callback, delegated);
    }

    @Override
    public CasualGenerationLayer delegate(CasualParserGenerationCallback callback, ParserData delegated) {
        return delegate(delegates, callback, delegated);
    }

    @Override
    public CasualGenerationLayer delegateAfter(CasualParserGenerationCallback callback, ParserData delegated) {
        return delegate(after, callback, delegated);
    }

    public CasualGenerationLayer delegate(List<CasualParserGenerationUnit> units, CasualParserGenerationCallback callback, ParserData delegated) {
        CasualParserGenerationUnit unit = new PandaCasualParserGenerationUnit(callback, delegated);
        units.add(unit);
        return this;
    }

    @Override
    public int countDelegates() {
        return before.size() + delegates.size() + after.size();
    }

}
