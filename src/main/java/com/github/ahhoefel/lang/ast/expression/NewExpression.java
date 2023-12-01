package com.github.ahhoefel.lang.ast.expression;

import java.util.List;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;

public class NewExpression extends Expression {

    private Expression type;
    private List<Expression> args;

    public NewExpression(Expression type, List<Expression> args, CodeLocation location) {
        this.type = type;
        this.args = args;
        this.setLocation(location);
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Expression getType() {
        return type;
    }

    public List<Expression> getArgs() {
        return args;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
