package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;

public class ExpressionStatement implements Visitable {

    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }
}
