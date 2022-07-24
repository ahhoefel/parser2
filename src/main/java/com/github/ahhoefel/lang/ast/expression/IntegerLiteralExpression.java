package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.Token;

public class IntegerLiteralExpression implements Expression {

    private int value;

    public IntegerLiteralExpression(Token t) {
        String text = t.getValue();
        this.value = Integer.parseInt(text);
    }

    public int getValue() {
        return value;
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
