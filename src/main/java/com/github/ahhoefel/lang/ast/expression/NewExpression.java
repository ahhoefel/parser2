package com.github.ahhoefel.lang.ast.expression;

import java.util.List;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;

public class NewExpression extends Expression {

    private Expression type;
    private List<Expression> args;
    private RegisterTracker widthRegisterTracker;
    private RegisterTracker arrayLengthRegisterTracker;
    private RegisterTracker arrayItemWidthRegisterTracker;

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

    public void setWidthRegisterTracker(RegisterTracker widthRegisterTracker) {
        this.widthRegisterTracker = widthRegisterTracker;
    }

    public RegisterTracker getWidthRegisterTracker() {
        return widthRegisterTracker;
    }

    public void setArrayLengthRegisterTracker(RegisterTracker arrayLengthRegisterTracker) {
        this.arrayLengthRegisterTracker = arrayLengthRegisterTracker;
    }

    public RegisterTracker getArrayLengthRegisterTracker() {
        return arrayLengthRegisterTracker;
    }

    public void setArrayItemWidthRegisterTracker(RegisterTracker arrayItemWidthRegisterTracker) {
        this.arrayItemWidthRegisterTracker = arrayItemWidthRegisterTracker;
    }

    public RegisterTracker getArrayItemWidthRegisterTracker() {
        return arrayItemWidthRegisterTracker;
    }
}
