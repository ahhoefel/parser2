package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.symbols.SymbolReference;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.Token;

import java.util.List;
import java.util.Optional;

public class FunctionInvocationExpression extends Expression {

    private Optional<Expression> implicitArg;
    private List<Expression> args;
    private String identifier;
    private Type type;
    private SymbolReference symbol;

    public FunctionInvocationExpression(Token identifier, List<Expression> args, CodeLocation location) {
        this.identifier = identifier.getValue();
        this.args = args;
        this.implicitArg = Optional.empty();
        this.setLocation(location);
    }

    public void setImplicitArg(Expression implicitArg) {
        this.implicitArg = Optional.of(implicitArg);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public Optional<Expression> getImplicitArg() {
        return implicitArg;
    }

    public List<Expression> getArgs() {
        return args;
    }

    @Override
    public Expression getType() {
        return type;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    public void setSymbolReference(SymbolReference symbol) {
        this.symbol = symbol;
    }

    public SymbolReference getSymbolReference() {
        return this.symbol;
    }

}
