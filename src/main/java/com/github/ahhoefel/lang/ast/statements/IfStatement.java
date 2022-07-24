package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;

public class IfStatement implements Visitable {

    private final Block block;
    private final Expression condition;

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
}
