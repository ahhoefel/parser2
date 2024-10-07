package com.github.ahhoefel.lang.ast.symbols;

import com.github.ahhoefel.lang.ast.symbols.StructLayout.Field;
import com.github.ahhoefel.lang.ast.symbols.StructLayout.FieldType;

public class ArrayStruct implements Struct {

    private StructLayout layout;
    private Field pointer;
    private Field length;
    private Field elementWidthBits;

    public ArrayStruct() {
        layout = new StructLayout();
        pointer = layout.add("pointer", FieldType.POINTER, 64);
        length = layout.add("length", FieldType.INT, 32);
        elementWidthBits = layout.add("elementWidthBits", FieldType.INT, 32);
    }

    @Override
    public StructLayout getLayout() {
        return layout;
    }

    public Field getPointer() {
        return pointer;
    }

    public Field getLength() {
        return length;
    }

    public Field getElementWidthBits() {
        return elementWidthBits;
    }
}
