package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols;

public class ReturnStatement implements Visitable {

    private Expression expression;
    private LocalSymbols.SymbolIndex symbolIndex;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public Expression getExpression() {
        return expression;
    }

   public void setLocalSymbolIndex(LocalSymbols.SymbolIndex symbolIndex) {
        this.symbolIndex = symbolIndex;
    }

    public LocalSymbols.SymbolIndex getLocalSymbolIndex() {
        return this.symbolIndex;
    }
}
