package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.type.Type;

public class LValue implements Visitable {
    private String identifier;
    private Type type;

    // E.g. a[i].y can be assigned to.
    private Expression expression;

    private CodeLocation location;

    public LValue(Expression e, CodeLocation location) {
        this.expression = e;
        this.location = location;
    }

    public String toString() {
        return "var " + identifier;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public CodeLocation getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }
}
