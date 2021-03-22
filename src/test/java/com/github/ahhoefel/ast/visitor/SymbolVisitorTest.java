package com.github.ahhoefel.ast.visitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.rules.LanguageRules;
import org.junit.jupiter.api.Test;

public class SymbolVisitorTest {
        private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

        @Test
        public void testSymbols() throws IOException {
                String s = Files.readString(Path.of("/Users/hoefel/dev/parser2/src/tests/functions/call.ro"));
                try {
                        File f = (File) fileParser.parse(s);
                        SymbolVisitor v = new SymbolVisitor();
                        f.accept(v);
                        // Assert.assertEquals(s, v.toString());
                } catch (ParseException e) {
                        // System.out.println("Ignoring error.");
                }
        }
}
