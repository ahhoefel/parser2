package com.github.ahhoefel.lang.ast.symbols;

import java.util.ArrayList;
import java.util.List;

public class StructLayout {

    public enum FieldType {
        INT,
        UNSIGNED_INT,
        FLOAT,
        POINTER
    }

    public static class Field {
        public String name;
        public int number;
        public int widthBits;
        public int offsetBits;
        public FieldType type;
    }

    private List<Field> fields;
    private int totalBits;

    public StructLayout() {
        fields = new ArrayList<>();
    }

    public Field add(String name, FieldType type, int widthBits) {
        Field field = new Field();
        field.name = name;
        field.type = type;
        field.widthBits = widthBits;
        field.number = fields.size();
        field.offsetBits = totalBits;
        fields.add(field);
        return field;
    }
}
