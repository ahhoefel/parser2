package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;

public class ParenthesesExpression extends Expression {

    private Expression a;

    public ParenthesesExpression(Expression a, CodeLocation location) {
        this.a = a;
        this.setLocation(location);
    }

    public Expression getExpression() {
        return a;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Expression getType() {
        return a.getType();
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
