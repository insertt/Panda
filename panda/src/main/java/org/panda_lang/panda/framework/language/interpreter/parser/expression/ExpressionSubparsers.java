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

package org.panda_lang.panda.framework.language.interpreter.parser.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExpressionSubparsers {

    private final List<ExpressionSubparser> subparsers = new ArrayList<>();

    public ExpressionSubparsers(Collection<ExpressionSubparser> subparsers) {
        this.subparsers.addAll(subparsers);
        Collections.sort(this.subparsers);
    }

    public ExpressionSubparsers fork() {
        return new ExpressionSubparsers(subparsers);
    }

    public void merge(ExpressionSubparsers subparsers) {
        this.subparsers.addAll(subparsers.getSubparsers());
    }

    public ExpressionSubparsers select(Collection<String> names) {
        List<ExpressionSubparser> selected = new ArrayList<>();

        for (ExpressionSubparser subparser : subparsers) {
            if (names.contains(subparser.getName().trim())) {
                selected.add(subparser);
            }
        }

        return new ExpressionSubparsers(selected);
    }

    private void sortSubparsers() {
        Collections.sort(subparsers);
    }

    public void addSubparser(ExpressionSubparser subparser) {
        this.subparsers.add(subparser);
        this.sortSubparsers();
    }

    public void removeSubparsers(Collection<String> names) {
        for (String name : names) {
            removeSubparser(name.trim());
        }
    }

    public void removeSubparser(String name) {
        subparsers.removeIf(expressionSubparser -> expressionSubparser.getName().equals(name));
    }

    public boolean isEmpty() {
        return subparsers.isEmpty();
    }

    public List<? extends ExpressionSubparser> getSubparsers() {
        return subparsers;
    }

}
