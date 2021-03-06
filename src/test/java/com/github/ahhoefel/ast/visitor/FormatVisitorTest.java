package com.github.ahhoefel.ast.visitor;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.ahhoefel.FileArgumentProvider;
import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.rules.ExpressionRules;
import com.github.ahhoefel.rules.LanguageRules;
import com.github.ahhoefel.rules.StructLiteralRules;
import com.github.ahhoefel.rules.TypeRules;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.ParameterizedTest;

public class FormatVisitorTest {

        private static final LRParser parser = new LRParser(LanguageBuilder.build("expression", new ExpressionRules(),
                        new StructLiteralRules(), new TypeRules()));

        private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

        @Test
        public void testSumAndProduct() {
                Expression e = (Expression) parser.parse("3 * 4 + x");
                FormatVisitor v = new FormatVisitor();
                e.accept(v);
                Assert.assertEquals("3 * 4 + x", v.toString());
        }

        @Test
        public void testUnaryMinus() {
                Expression e = (Expression) parser.parse("3*-4+x");
                FormatVisitor v = new FormatVisitor();
                e.accept(v);
                Assert.assertEquals("3 * -4 + x", v.toString());
        }

        @Test
        public void testParentheses() {
                Expression e = (Expression) parser.parse("3*(4+x)");
                FormatVisitor v = new FormatVisitor();
                e.accept(v);
                Assert.assertEquals("3 * (4 + x)", v.toString());
        }

        private class RoFiles extends FileArgumentProvider {
                private RoFiles() {
                        super("/Users/hoefel/dev/parser2/src/tests/");
                }
        }

        @ParameterizedTest(name = "{0} {1}")
        @ArgumentsSource(RoFiles.class)
        public void testCorrectlyFormatted(Path path) throws Exception {
                String s = Files.readString(path);
                try {
                        File f = (File) fileParser.parse(s);
                        FormatVisitor v = new FormatVisitor();
                        f.accept(v);
                        Assert.assertEquals(path.toString(), s, v.toString());
                } catch (ParseException e) {
                        System.out.println("Ignoring error on path: " + path);
                        System.out.println(e);
                }
        }
}
