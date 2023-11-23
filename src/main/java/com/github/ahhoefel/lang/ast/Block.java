package com.github.ahhoefel.lang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.ahhoefel.parser.Locateable;

public class Block implements Visitable, Locateable {

    private List<Visitable> statements;
    private CodeLocation location;

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

    @Override
    public CodeLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(CodeLocation location) {
        this.location = location;
    }
}
