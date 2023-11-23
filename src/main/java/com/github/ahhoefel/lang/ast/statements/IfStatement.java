package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols;
import com.github.ahhoefel.parser.Locateable;

public class IfStatement implements Visitable, Locateable {

    private final Block block;
    private final Expression condition;
    private LocalSymbols.SymbolIndex symbolIndex;
    private CodeLocation location;

    public IfStatement(Expression condition, Block block, CodeLocation location) {
        this.condition = condition;
        this.block = block;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
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
