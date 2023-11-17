package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;

public class ParenthesesExpression extends Expression {

    private Expression a;

    public ParenthesesExpression(Expression a) {
        this.a = a;
    }

    public Expression getExpression() {
        return a;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Type getType() {
        return a.getType();
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
