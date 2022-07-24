package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;

public class ReturnStatement implements Visitable {

    private Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public Expression getExpression() {
        return expression;
    }
}
