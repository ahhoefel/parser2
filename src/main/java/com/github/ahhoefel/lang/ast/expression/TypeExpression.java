package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;

public class TypeExpression extends Expression {

    private Type type;

    public TypeExpression(Type type) {
        this.type = type;
        this.setLocation(type.getLocation());
    }

    public Type getFunctionInvocation() {
        return type;
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public Type getStoredType() {
        return type;
    }

    @Override
    public Expression getType() {
        return Type.TYPE;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
