package com.github.ahhoefel.lang.ast.type;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.parser.Locateable;

public interface Type extends Visitable, Locateable {

    // Singletons should not be used to explicitly written types in code, but rather
    // for the implicit types of expressions.
    // For explicitly written types in code, a new instance should be created so it
    // can have a code location.
    public static final Type INT = new IntType(null);
    public static final Type BOOL = new BooleanType(null);
    public static final Type STRING = new StringType(null);
    public static final Type VOID = new VoidType(null);

    int getWidthBits();

    static abstract class AbstractType implements Type {
        private CodeLocation location;

        @Override
        public CodeLocation getLocation() {
            return location;
        }

        @Override
        public void setLocation(CodeLocation location) {
            this.location = location;
        }
    }

    class IntType extends AbstractType {

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
    }

    class BooleanType extends AbstractType {

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
    }

    class StringType extends AbstractType {

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
    }

    class VoidType extends AbstractType {

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
    }
}
