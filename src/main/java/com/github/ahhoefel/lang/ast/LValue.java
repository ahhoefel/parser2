package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.parser.Locateable;

public class LValue implements Visitable, Locateable {
    private String identifier;
    private CodeLocation location;
    // E.g. a[i].y can be assigned to.
    private Expression expression;

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

    public Expression getType() {
        return expression.getType();
    }

    @Override
    public CodeLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(CodeLocation location) {
        this.location = location;
    }
}
