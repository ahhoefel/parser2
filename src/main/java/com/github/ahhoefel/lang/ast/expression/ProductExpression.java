package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;

public class ProductExpression extends Expression {

    private Expression a;
    private Expression b;

    public ProductExpression(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    public Expression getLeft() {
        return a;
    }

    public Expression getRight() {
        return b;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Type getType() {
        return Type.INT;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
