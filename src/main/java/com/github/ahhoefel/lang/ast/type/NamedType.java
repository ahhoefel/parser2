package com.github.ahhoefel.lang.ast.type;

import java.util.Optional;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Visitor;

public class NamedType implements Type {
    private String identifier;
    private Optional<String> packagePrefix;
    private Optional<Type> type;
    private CodeLocation location;

    public NamedType(String identifier, CodeLocation location) {
        this.identifier = identifier;
        packagePrefix = Optional.empty();
        type = Optional.empty();
        this.location = location;
    }

    @Override
    public CodeLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(CodeLocation location) {
        this.location = location;
    }

    public NamedType(String packagePrefix, String identifier, CodeLocation location) {
        this.identifier = identifier;
        this.packagePrefix = Optional.of(packagePrefix);
        type = Optional.empty();
        this.location = location;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Optional<String> getPackage() {
        return packagePrefix;
    }

    public Type getType() {
        if (!type.isPresent()) {
            throw new RuntimeException("Type not linked");
        }
        return type.get();
    }

    @Override
    public int getWidthBits() {
        return getType().getWidthBits();
    }

    public boolean equals(Object o) {
        // Order matters here so we recursively descend into named types, flipping
        // sides.
        return o.equals(type.get());
    }

    public String toString() {
        String out = "";
        if (packagePrefix.isPresent()) {
            out += packagePrefix.get() + ".";
        }
        out += identifier;
        if (type.isPresent()) {
            out += " " + type.get().toString();
        }
        return out;
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }
}
