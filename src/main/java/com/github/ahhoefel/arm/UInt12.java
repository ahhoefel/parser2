package com.github.ahhoefel.arm;

public class UInt12 implements Parameter {
    private int value;
    public UInt12(int value) {
        this.value = value;
         if (value < 0 || value > 4095) {
            throw new RuntimeException("UInt12 value out of range [0,4095]: " + value);
        }
    }

    public String toString() {
        return "#" + value;
    }
}
