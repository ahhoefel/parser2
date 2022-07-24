package com.github.ahhoefel.lang.ast;

import java.util.ArrayList;
import java.util.List;

public class Block implements Visitable {

    private List<Visitable> statements;

    public Block() {
        this.statements = new ArrayList<>();
    }

    public List<Visitable> getStatements() {
        return statements;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public void add(Visitable statement) {
        this.statements.add(statement);
    }
}
