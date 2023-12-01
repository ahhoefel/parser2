package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.symbols.SymbolReference;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.Token;

public class VariableExpression extends Expression {

    private final String identifier;
    private SymbolReference symbol;
    private Type type;

    public VariableExpression(Token t) {
        this.identifier = t.getValue();
        this.setLocation(t.getLocation());
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Expression getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setSymbolReference(SymbolReference symbol) {
        this.symbol = symbol;
    }

    public SymbolReference getSymbolReference() {
        return this.symbol;
    }

    @Override
    public void setRegisterTracker(RegisterTracker register) {
        throw new UnsupportedOperationException(
                "Registers should not be set on variable expressions, but rather on variable declarations");
    }

    @Override
    public RegisterTracker getRegisterTracker() {
        return symbol.getRegisterTracker();
    }

    @Override
    public boolean isLValue() {
        return true;
    }

    public String toString() {
        return identifier;
    }
}
