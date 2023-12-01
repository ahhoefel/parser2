package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.ArrayType;
import com.github.ahhoefel.lang.ast.type.Type;

public class IndexAccessExpression extends Expression {

    private Expression subject;
    private Expression index;

    public IndexAccessExpression(Expression subject, Expression index, CodeLocation location) {
        this.subject = subject;
        this.index = index;
        this.setLocation(location);
    }

    public Expression getSubject() {
        return subject;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public void accept(Visitor v, Object... args) {
        v.visit(this, args);
    }

    @Override
    public boolean isLValue() {
        // TODO: depends on the subject.
        return true;
    }

    @Override
    public Expression getType() {
        Expression subjectType = subject.getType();
        if (subjectType.equals(Type.TYPE)) {
            return Type.TYPE;
        }
        return new ArrayType(subjectType);
    }

    public String toString() {
        return subject.toString() + "[" + index.toString() + "]";
    }
}
