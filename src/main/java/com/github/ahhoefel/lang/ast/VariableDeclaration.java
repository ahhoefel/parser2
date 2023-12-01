package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.Locateable;

public class VariableDeclaration implements Visitable, Locateable {

    private String name;
    private Expression type;
    private RegisterTracker registerTracker;
    private CodeLocation location;

    public VariableDeclaration(String name, Expression type, CodeLocation location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public void setRegisterTracker(RegisterTracker registerTracker) {
        this.registerTracker = registerTracker;
    }

    public RegisterTracker getRegisterTracker() {
        return registerTracker;
    }

    public String getName() {
        return name;
    }

    public Expression getType(ErrorLog log) {
        return type;
    }

    public Expression getType() {
        return type;
    }

    public String toString() {
        return String.format("var %s %s", name, type);
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
