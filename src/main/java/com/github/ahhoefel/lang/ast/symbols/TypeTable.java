package com.github.ahhoefel.lang.ast.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.type.Type;

public class TypeTable {

    public static class TypeRecord extends Expression {
        private String name;
        private int widthBits;
        private int index;

        public TypeRecord setWidthBits(int bits) {
            this.widthBits = bits;
            return this;
        }

        public TypeRecord(String name) {
            this.name = name;
        }

        public int getWidthBits() {
            return widthBits;
        }

        public String getName() {
            return name;
        }

        @Override
        public void accept(Visitor v, Object... args) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }

        public String toString() {
            return name;
        }
    }

    private Map<String, TypeRecord> typesByName;
    private List<TypeRecord> typesByIndex;

    public TypeTable() {
        this.typesByName = new HashMap<>();
        this.typesByIndex = new ArrayList<>();
        add(new TypeRecord("Array").setWidthBits(64));
    }

    public void add(TypeRecord type) {
        int index = this.typesByIndex.size();
        type.index = index;
        this.typesByIndex.add(type);
        this.typesByName.put(type.name, type);
    }

    public Optional<TypeRecord> get(String name) {
        return Optional.ofNullable(typesByName.get(name));
    }
}
