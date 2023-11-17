package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;

public abstract class Expression implements Visitable {
    public abstract boolean isLValue();
    public abstract Type getType();
    
    private RegisterTracker register;
    public RegisterTracker getRegisterTracker() {
        return register;
    }

    public void setRegisterTracker(RegisterTracker register) {
        this.register = register;
    }
}
