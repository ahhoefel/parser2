package com.github.ahhoefel.ast;

public interface Visitable {
    public void accept(Visitor v, Object... args);
}
