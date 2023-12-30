package com.github.ahhoefel.arm;

public class Comment implements Parameter {

    private String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    public String toString() {
        return comment;
    }

}
