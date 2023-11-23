package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols;
import com.github.ahhoefel.parser.Locateable;

public class ExpressionStatement implements Visitable, Locateable {

    private final Expression expression;
    private LocalSymbols.SymbolIndex symbolIndex;
    private CodeLocation location;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public void setLocalSymbolIndex(LocalSymbols.SymbolIndex symbolIndex) {
        this.symbolIndex = symbolIndex;
    }

    public LocalSymbols.SymbolIndex getLocalSymbolIndex() {
        return this.symbolIndex;
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
