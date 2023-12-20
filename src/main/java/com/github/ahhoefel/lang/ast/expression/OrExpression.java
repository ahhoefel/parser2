package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;

public class OrExpression extends Expression {

    private Expression a;
    private Expression b;

    public OrExpression(Expression a, Expression b, CodeLocation location) {
        this.a = a;
        this.b = b;
        this.setLocation(location);
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
    public Expression getType() {
        return Type.BOOL;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
