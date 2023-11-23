package com.github.ahhoefel.parser;

import com.github.ahhoefel.lang.ast.CodeLocation;

public class LocateableContainer<T> implements Locateable {
    private T t;
    private CodeLocation location;

    public LocateableContainer(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    @Override
    public CodeLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(CodeLocation location) {
        this.location = location;
    }
}
