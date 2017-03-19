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

package org.panda_lang.panda.implementation.interpreter.parser.defaults;

import org.panda_lang.framework.interpreter.lexer.token.TokenizedSource;
import org.panda_lang.framework.interpreter.parser.Parser;
import org.panda_lang.framework.interpreter.parser.ParserInfo;
import org.panda_lang.framework.interpreter.parser.generation.ParserGeneration;
import org.panda_lang.framework.interpreter.parser.pipeline.registry.PipelineRegistry;
import org.panda_lang.panda.implementation.interpreter.parser.util.Components;
import org.panda_lang.framework.interpreter.source.Source;
import org.panda_lang.framework.interpreter.source.SourceSet;
import org.panda_lang.panda.implementation.structure.Script;
import org.panda_lang.panda.Panda;
import org.panda_lang.panda.composition.PandaComposition;
import org.panda_lang.panda.implementation.interpreter.PandaInterpreter;
import org.panda_lang.panda.implementation.interpreter.lexer.PandaLexer;
import org.panda_lang.panda.implementation.interpreter.lexer.token.distributor.PandaSourceStream;
import org.panda_lang.panda.implementation.interpreter.parser.PandaParserInfo;
import org.panda_lang.panda.implementation.interpreter.parser.generation.PandaParserGeneration;
import org.panda_lang.panda.implementation.structure.PandaApplication;
import org.panda_lang.panda.implementation.structure.PandaScript;

public class SourceParser implements Parser {

    private final PandaInterpreter interpreter;
    private final PandaApplication application;

    public SourceParser(PandaInterpreter interpreter) {
        this.interpreter = interpreter;
        this.application = interpreter.getApplication();
    }

    public void parse(SourceSet sourceSet) {
        Panda panda = interpreter.getPanda();

        PandaComposition pandaComposition = panda.getPandaComposition();
        PipelineRegistry pipelineRegistry = pandaComposition.getPipelineRegistry();

        ParserGeneration generation = new PandaParserGeneration();

        ParserInfo parserInfo = new PandaParserInfo();
        parserInfo.setComponent(Components.INTERPRETER, interpreter);
        parserInfo.setComponent(Components.PIPELINE_REGISTRY, pipelineRegistry);
        parserInfo.setComponent(Components.GENERATION, generation);

        for (Source source : sourceSet.getSources()) {
            PandaScript pandaScript = new PandaScript(source.getTitle());

            PandaLexer lexer = new PandaLexer(pandaComposition.getSyntax(), source.getContent());
            TokenizedSource tokenizedSource = lexer.convert();
            PandaSourceStream sourceStream = new PandaSourceStream(tokenizedSource);

            parserInfo.setComponent(Components.SOURCE_STREAM, sourceStream);
            parserInfo.setComponent(Components.SCRIPT, pandaScript);

            OverallParser overallParser = new OverallParser(parserInfo);

            while(overallParser.hasNext()) {
                overallParser.next();
            }

            application.addScript(pandaScript);
        }

        generation.execute(parserInfo);

        for (Script script : application.getScripts()) {
            System.out.println(script.toString());
        }
    }

    public PandaInterpreter getInterpreter() {
        return interpreter;
    }

}
