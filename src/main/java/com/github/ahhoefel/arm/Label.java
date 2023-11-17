package com.github.ahhoefel.arm;

public class Label implements Parameter {

    private String name;
    public Label(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
    
}
