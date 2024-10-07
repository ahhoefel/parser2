package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;

public class BooleanLiteralExpression extends Expression {

    private boolean value;

    public BooleanLiteralExpression(boolean value, CodeLocation location) {
        this.value = value;
        this.setLocation(location);
    }

    public boolean getValue() {
        return value;
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
