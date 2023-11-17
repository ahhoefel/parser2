package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.type.Type;

import com.github.ahhoefel.parser.Token;

public class MemberAccessExpression extends Expression {

    private final Token member;
    private final Expression expression;
    private Type memberType;

    public MemberAccessExpression(Expression expression, Token member) {
        this.member = member;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public Token getMember() {
        return member;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Type getType() {
        return memberType;
    }

    @Override
    public boolean isLValue() {
        return true;
    }
}
