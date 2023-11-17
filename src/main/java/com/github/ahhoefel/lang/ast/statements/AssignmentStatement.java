package com.github.ahhoefel.lang.ast.statements;

import java.util.Optional;

import com.github.ahhoefel.lang.ast.LValue;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols;

public class AssignmentStatement implements Visitable {

    private final Optional<LValue> lvalue;
    private final Optional<VariableDeclaration> declaration;
    private final Expression expression;
    private LocalSymbols.SymbolIndex symbolIndex;

    public AssignmentStatement(LValue lvalue, Expression expression) {
        this.lvalue = Optional.of(lvalue);
        this.declaration = Optional.empty();
        this.expression = expression;
    }

    public AssignmentStatement(VariableDeclaration declaration, Expression expression) {
        this.lvalue = Optional.empty();
        this.declaration = Optional.of(declaration);
        this.expression = expression;
    }

    public Optional<LValue> getLValue() {
        return lvalue;
    }

    public Optional<VariableDeclaration> getVariableDeclaration() {
        return declaration;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public void setLocalSymbolIndex(LocalSymbols.SymbolIndex symbolIndex) {
        this.symbolIndex = symbolIndex;
    }

    public LocalSymbols.SymbolIndex getLocalSymbolIndex() {
        return this.symbolIndex;
    }
}
