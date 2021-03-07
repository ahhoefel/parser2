package com.github.ahhoefel.ast.visitor;

import java.util.List;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.expression.IntegerLiteralExpression;
import com.github.ahhoefel.ast.expression.ProductExpression;
import com.github.ahhoefel.ast.expression.SumExpression;
import com.github.ahhoefel.ast.expression.VariableExpression;
import com.github.ahhoefel.parser.Grammar;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.LRTable;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.Parser;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.rules.ExpressionRules;
import com.github.ahhoefel.rules.StructLiteralRules;
import com.github.ahhoefel.rules.TypeRules;

import org.junit.Assert;
import org.junit.Test;

public class FormatVisitorTest {

        private static final LRParser parser = new LRParser(LanguageBuilder.build("expression", new ExpressionRules(),
                        new StructLiteralRules(), new TypeRules()));

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
                // This is wrong. Parentheses should be included
                // in the output, even if they aren't needed in the AST.
                Assert.assertEquals("3 * 4 + x", v.toString());
        }
}
