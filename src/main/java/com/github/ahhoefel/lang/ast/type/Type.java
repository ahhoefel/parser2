package com.github.ahhoefel.lang.ast.type;

import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;

public interface Type extends Visitable {
    public static final Type INT = new IntType();
    public static final Type STRING = new StringType();
    public static final Type BOOL = new BooleanType();
    public static final Type VOID = new VoidType();

    int getWidthBits();

    class IntType implements Type {

        public String toString() {
            return "int";
        }

        public boolean equals(Object o) {
            return o == INT;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }
    }

    class BooleanType implements Type {
        public String toString() {
            return "bool";
        }

        public boolean equals(Object o) {
            return o == BOOL;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }
    }

    class StringType implements Type {

        public String toString() {
            return "string";
        }

        public boolean equals(Object o) {
            return o == STRING;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }
    }

    class VoidType implements Type {
        public String toString() {
            return "void";
        }

        public boolean equals(Object o) {
            return o == VOID;
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
