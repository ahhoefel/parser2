package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.Token;

public class IntegerLiteralExpression extends Expression {

    private long value;

    public IntegerLiteralExpression(Token t) {
        String text = t.getValue();
        this.value = Long.parseLong(text);
        this.setLocation(t.getLocation());
    }

    public long getValue() {
        return value;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Expression getType() {
        return Type.INT;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    public String toString() {
        return "IntegerLiteral(" + value + ")";
    }
}
