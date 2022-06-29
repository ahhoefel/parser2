package com.github.ahhoefel.lang.ast;

public interface Visitable {
    public void accept(Visitor v, Object... args);
}
