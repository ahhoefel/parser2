package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;
import com.github.ahhoefel.lang.ast.type.Type;

public class IndexAccessExpression extends Expression {

    private Expression subject;
    private Expression index;
    private RegisterTracker widthRegisterTracker; // Single element width

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
        try {
            v.visit(this, args);
        } catch (Exception e) {
            throw new RuntimeException("Exception in " + v.getClass().toString() + " in AST at " + this.getLocation(),
                    e);
        }
    }

    @Override
    public boolean isLValue() {
        // TODO: depends on the subject.
        return true;
    }

    @Override
    public Expression getType() {
        // There are two cases:
        // 1) Expressions like Array[int] are IndexAccessExpressions with a subject that
        // is Array and index of type TYPE. The resulting type of such and expression is
        // a TYPE.
        // 2) Expresssions like a[5] are IndexAccessExpressions. The type of their
        // subjects "a" is also an IndexAccessExpression like "Array[int]". The
        // resulting type is "int", the type of the index of the subject type.

        if (subject instanceof VariableExpression &&
                ((VariableExpression) subject).getIdentifier().equals("Array")) {
            return Type.TYPE;
        }
        Expression subjectType = subject.getType();
        if (subjectType instanceof IndexAccessExpression) {
            // E.g. Array[int]
            IndexAccessExpression arrayType = (IndexAccessExpression) subjectType;
            return arrayType.getIndex();
        }
        throw new RuntimeException(
                "IndexAccessExpressions should be either Array types or have a subject of array type");
    }

    public String toString() {
        return subject.toString() + "[" + index.toString() + "]";
    }

    public boolean equals(Object o) {
        // throw new RuntimeException("Foo");
        if (!(o instanceof IndexAccessExpression)) {
            return false;
        }
        IndexAccessExpression e = (IndexAccessExpression) o;
        return e.subject.equals(subject) && e.index.equals(index);
    }

    public RegisterTracker getWidthRegisterTracker() {
        return widthRegisterTracker;
    }

    public void setWidthRegisterTracker(RegisterTracker widthRegisterTracker) {
        this.widthRegisterTracker = widthRegisterTracker;
    }
}
