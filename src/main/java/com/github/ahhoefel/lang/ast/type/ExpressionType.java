package com.github.ahhoefel.lang.ast.type;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;

// A type based on a expression from the grammer.
public class ExpressionType extends Type {

    private Expression expression;

    public ExpressionType(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(Visitor v, Object... args) {
        v.visit(this, args);
    }

    @Override
    public CodeLocation getLocation() {
        return expression.getLocation();
    }

    @Override
    public void setLocation(CodeLocation location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLocation'");
    }

    @Override
    public int getWidthBits() {
        if (!expression.getType().equals(Type.TYPE)) {
            throw new RuntimeException(
                    "ExpressionTypes should have type Type.TYPE. Otherwise they do not evaluate to a type.");
        }
        // TODO: fix this. Either we need to evaluate the expression at compile time, or
        // at runtime.
        // If we have runtime based sizes for variables, that will require special
        // assembly to keep track of the
        // stack size.
        return 64;
    }

    @Override
    public int getEncoding() {
        return 0;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    @Override
    public Expression getType() {
        return Type.TYPE;
    }
}
