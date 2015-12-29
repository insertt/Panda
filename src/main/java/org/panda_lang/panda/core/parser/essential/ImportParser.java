package org.panda_lang.panda.core.parser.essential;

import org.panda_lang.panda.core.parser.*;
import org.panda_lang.panda.core.parser.essential.util.EssentialPriority;
import org.panda_lang.panda.core.syntax.Import;
import org.panda_lang.panda.core.syntax.NamedExecutable;

public class ImportParser implements Parser {

    static {
        ParserLayout parserLayout = new ParserLayout(new ImportParser());
        parserLayout.pattern("import *;", EssentialPriority.IMPORT.getPriority(), EssentialPriority.IMPORT.getPriority() * 10, PatternExtractor.FULL);
        ParserCenter.registerParser(parserLayout);
    }

    @Override
    public NamedExecutable parse(Atom atom) {
        final String source = atom.getSourceCode();
        final StringBuilder importBuilder = new StringBuilder();

        int stage = 0;
        Import importElement = null;

        for (char c : source.toCharArray()) {
            if (Character.isWhitespace(c) || c == ';') {
                switch (stage) {
                    case 0:
                        stage = 1;
                        break;
                    case 1:
                        importElement = new Import(importBuilder.toString());
                        importBuilder.setLength(0);
                        stage = 2;
                        break;
                    case 2:
                        String operator = importBuilder.toString();
                        if (operator.equals(">")) {

                        } else if (operator.equals("as")) {

                        }
                }
            } else if (stage == 1) {
                importBuilder.append(c);
            }
        }

        return importElement;
    }

}
