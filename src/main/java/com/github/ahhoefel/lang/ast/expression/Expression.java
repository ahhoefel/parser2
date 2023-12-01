package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;

public abstract class Expression implements Visitable, Locateable {
    public abstract boolean isLValue();

    public abstract Expression getType();

    private CodeLocation location;

    public CodeLocation getLocation() {
        return location;
    }

    public void setLocation(CodeLocation location) {
        this.location = location;
    }

    private RegisterTracker register;

    public RegisterTracker getRegisterTracker() {
        return register;
    }

    public void setRegisterTracker(RegisterTracker register) {
        this.register = register;
    }
}
