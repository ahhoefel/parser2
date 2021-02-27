package com.github.ahhoefel.ast.visitor;

import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.expression.IntegerLiteralExpression;
import com.github.ahhoefel.ast.expression.ProductExpression;
import com.github.ahhoefel.ast.expression.SumExpression;
import com.github.ahhoefel.ast.expression.VariableExpression;
import com.github.ahhoefel.parser.Token;

import org.junit.Assert;
import org.junit.Test;

public class FormatVisitorTest {
    @Test
    public void testSumAndProductExpressions() {
        Expression e = new SumExpression(
                new ProductExpression(new IntegerLiteralExpression(new Token(null, "3", null)),
                        new IntegerLiteralExpression(new Token(null, "4", null))),
                new VariableExpression(new Token(null, "x", null)));

        FormatVisitor v = new FormatVisitor();
        e.accept(v);

        Assert.assertEquals("3 * 4 + x", v.toString());
    }
}
