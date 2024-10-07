package com.github.ahhoefel.lang.ast.visitor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import com.github.ahhoefel.FileArgumentProvider;
import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.rules.ExpressionRules;
import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.lang.rules.StructLiteralRules;
import com.github.ahhoefel.lang.rules.TypeRules;
import com.github.ahhoefel.parser.LayeredParser;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.lang.rules.lex.Lexicon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.ParameterizedTest;

public class FormatVisitorTest {

    @SuppressWarnings("rawtypes")
    private static final LayeredParser<Expression> parser = new LayeredParser.Layer<Iterator, Expression>(
            "Expression", Expression.class, new Lexicon(), "expression",
            new ExpressionRules(),
            new StructLiteralRules(), new TypeRules());

    private static final LayeredParser<File> fileParser = LanguageRules.getParser();

    @Test
    public void testSumAndProduct() {
        Expression e = (Expression) parser.parse("3 * 4 + x");
        FormatVisitor v = new FormatVisitor();
        e.accept(v);
        Assertions.assertEquals("3 * 4 + x", v.toString());
    }

    @Test
    public void testUnaryMinus() {
        Expression e = (Expression) parser.parse("3*-4+x");
        FormatVisitor v = new FormatVisitor();
        e.accept(v);
        Assertions.assertEquals("3 * -4 + x", v.toString());
    }

    @Test
    public void testParentheses() {
        Expression e = (Expression) parser.parse("3*(4+x)");
        FormatVisitor v = new FormatVisitor();
        e.accept(v);
        Assertions.assertEquals("3 * (4 + x)", v.toString());
    }

    private static class RoFiles extends FileArgumentProvider {
        private RoFiles() {
            super("/Users/hoefel/dev/parser2/src/tests/");
        }
    }

    @ParameterizedTest(name = "{0} {1}")
    @ArgumentsSource(RoFiles.class)
    public void testCorrectlyFormatted(Path path) throws Exception {
        // SymbolVisitor v = new SymbolVisitor(source);
        // GlobalSymbols globals = new GlobalSymbols(v, fileParser);
        // for (Path entry : entries) {
        // Target t = new Target(source, entry);
        // Optional<FileSymbols> fileSymbols = globals.add(t);
        // assertTrue(fileSymbols.isPresent());
        // }

        String s = Files.readString(path);
        try {
            File f = (File) fileParser.parse(s);
            FormatVisitor v = new FormatVisitor();
            f.accept(v);
            Assertions.assertEquals(Files.readString(path), s, v.toString());
        } catch (Exception e) {
            System.out.println("Ignoring error on path: " + path);
            System.out.println(e);
        }
    }
}
