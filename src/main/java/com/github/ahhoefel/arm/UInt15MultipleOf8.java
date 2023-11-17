package com.github.ahhoefel.arm;

public class UInt15MultipleOf8 implements Parameter {
    private int value;
    public UInt15MultipleOf8(int value) {
        this.value = value;
         if (value < 0 || value > 32760) {
            throw new RuntimeException("UInt15MultipleOf8 value out of range [0,32760]: " + value);
        }
        if (value % 8 != 0) {
            throw new RuntimeException("UInt15MultipleOf8 must be a multiple of 8: " + value);
        }
    }

    public String toString() {
        return "#" + value;
    }
}
