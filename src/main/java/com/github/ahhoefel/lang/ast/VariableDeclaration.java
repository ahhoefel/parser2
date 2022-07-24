package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

public class VariableDeclaration implements Visitable {

    private String name;
    private Type type;

    public VariableDeclaration(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public String getName() {
        return name;
    }

    public Type getType(ErrorLog log) {
        return type;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return String.format("var %s %s", name, type);
    }
}
