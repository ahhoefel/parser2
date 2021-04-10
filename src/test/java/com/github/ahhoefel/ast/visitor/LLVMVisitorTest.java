package com.github.ahhoefel.ast.visitor;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.rules.LanguageRules;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.ParameterizedTest;

public class LLVMVisitorTest {
        private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

        @ParameterizedTest(name = "{0} {1}")
        @ValueSource(strings = { "/Users/hoefel/dev/parser2/src/tests/expressions/condition.ro" })
        public void testCorrectlyFormatted(String pathString) throws Exception {
                Path path = Path.of(pathString);
                String s = Files.readString(path);
                System.out.println(s);
                try {
                        File f = (File) fileParser.parse(s);
                        LLVMVisitor v = new LLVMVisitor();
                        f.accept(v);
                } catch (ParseException e) {
                        System.out.println("Ignoring error. " + e);
                }

        }
}
