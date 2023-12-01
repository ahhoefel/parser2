package com.github.ahhoefel.lang.ast.type;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;

public abstract class Type extends Expression {

    // Singletons should not be used to explicitly written types in code, but rather
    // for the implicit types of expressions.
    // For explicitly written types in code, a new instance should be created so it
    // can have a code location.
    public static final Type INT = new IntType(null);
    public static final Type BOOL = new BooleanType(null);
    public static final Type STRING = new StringType(null);
    public static final Type VOID = new VoidType(null);
    // The type of types.
    public static final Type TYPE = new TypeType(null);

    public abstract int getWidthBits();

    public abstract int getEncoding();

    public static int getWidthBits(Expression type) {
        if (type instanceof Type) {
            return ((Type) type).getWidthBits();
        }
        // TODO: allow width calculations for type expressions.
        return 64;
    }

    public static Expression getMemberType(Expression type, String memberName) {
        // TODO: lookup members on a type expression.
        return INT;
    }

    public static class IntType extends Type {

        public IntType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "int";
        }

        public boolean equals(Object o) {
            return o instanceof IntType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 1;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class BooleanType extends Type {

        public BooleanType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "bool";
        }

        public boolean equals(Object o) {
            return o instanceof BooleanType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 2;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class StringType extends Type {

        public StringType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "string";
        }

        public boolean equals(Object o) {
            return o instanceof StringType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 3;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class VoidType extends Type {

        public VoidType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "void";
        }

        public boolean equals(Object o) {
            return o instanceof VoidType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 0;
        }

        public int getEncoding() {
            return 4;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class TypeType extends Type {

        public TypeType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "type";
        }

        public boolean equals(Object o) {
            return o instanceof TypeType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 5;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }
}
