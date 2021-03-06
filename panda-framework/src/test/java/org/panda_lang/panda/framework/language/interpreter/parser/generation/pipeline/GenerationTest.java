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

package org.panda_lang.panda.framework.language.interpreter.parser.generation.pipeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.panda_lang.panda.framework.design.interpreter.parser.generation.pipeline.PipelineType;

import java.util.Arrays;

class GenerationTest {

    private static final PandaGeneration generation = new PandaGeneration();

    @BeforeAll
    public static void createPipelines() {
        generation.initialize(Arrays.asList(
                new PipelineType("b", 2.0),
                new PipelineType("a", 1.0),
                new PipelineType("c", 3.0))
        );
    }

    @Test
    public void testPipelineGeneration() throws Throwable {
        StringBuilder outputBuilder = new StringBuilder();

        generation.pipeline("b").nextLayer().delegate((pipeline, data) -> outputBuilder.append("b "), null);
        generation.pipeline("a").nextLayer().delegate((pipeline, data) -> outputBuilder.append("a "), null);
        generation.pipeline("c").nextLayer().delegate((pipeline, data) -> outputBuilder.append("c "), null);

        generation.pipeline("b").nextLayer().delegate((pipeline, delegatedData) -> {
            outputBuilder.append("b2 ");

            pipeline.nextLayer().delegate((pipeline1, delegatedData1) -> {
                pipeline1.generation().pipeline("a").nextLayer().delegate((pipeline2, delegatedData2) -> outputBuilder.append("a2 "), delegatedData1);
                outputBuilder.append("b3 ");
                return null;
            }, delegatedData);

            return null;
        }, null);

        generation.execute(null);
        Assertions.assertEquals("a b b2 b3 a2 c", outputBuilder.toString().trim());

        outputBuilder.setLength(0);
        generation.execute(null);
        Assertions.assertEquals("", outputBuilder.toString());
    }

}
