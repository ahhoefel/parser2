package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;

public class BooleanLiteralExpression extends Expression {

    private boolean value;

    public BooleanLiteralExpression(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Type getType() {
        return Type.BOOL;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
