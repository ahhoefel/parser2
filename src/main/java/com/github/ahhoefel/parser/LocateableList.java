package com.github.ahhoefel.parser;

import java.util.ArrayList;
import java.util.List;

import com.github.ahhoefel.lang.ast.CodeLocation;

public class LocateableList<T> implements Locateable {

    private CodeLocation location;
    private List<T> list;

    public LocateableList() {
        this.list = new ArrayList<T>();
    }

    public void add(T t) {
        this.list.add(t);
    }

    public List<T> getList() {
        return list;
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
