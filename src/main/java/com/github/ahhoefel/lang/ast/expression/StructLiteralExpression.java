package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.type.Type;

import java.util.HashMap;
import java.util.Map;

public class StructLiteralExpression extends Expression {

    private Type type;
    private Map<String, Expression> values;

    public StructLiteralExpression(Type type) {

        this.type = type;
        this.values = new HashMap<>();
    }

    public Map<String, Expression> getValues() {
        return values;
    }

    public void add(String identifier, Expression value) {
        assert !values.containsKey(identifier);
        values.put(identifier, value);
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

}
