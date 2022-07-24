package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.Token;

public class VariableExpression implements Expression {

    private final String identifier;
    private Type type;

    public VariableExpression(Token t) {
        this.identifier = t.getValue();
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isLValue() {
        return true;
    }
}
