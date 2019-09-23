package com.github.ahhoefel.ast;

import java.util.Optional;

public class NamedType implements Type {
  private String identifier;
  private Optional<String> packagePrefix;
  private Optional<Type> type;

  public NamedType(String identifier) {
    this.identifier = identifier;
    packagePrefix = Optional.empty();
    type = Optional.empty();
  }

  public NamedType(String packagePrefix, String identifier) {
    this.identifier = identifier;
    this.packagePrefix = Optional.of(packagePrefix);
    type = Optional.empty();
  }

  public Optional<String> getPackage() {
    return packagePrefix;
  }

  public void linkTypes(SymbolCatalog symbols) {
    type = Optional.of(symbols.getType(packagePrefix, identifier));
  }

  public Type getType() {
    if (!type.isPresent()) {
      throw new RuntimeException("Type not linked");
    }
    return type.get();
  }

  @Override
  public int width() {
    if (type.isPresent()) {
      return type.get().width();
    }
    return 0;
  }

  public boolean equals(Object o) {
    // Order matters here so we recursively descend into named types, flipping sides.
    return o.equals(type.get());
  }

  public String toString() {
    String out = "type ";
    if (packagePrefix.isPresent()) {
      out += packagePrefix.get() + ".";
    }
    out += identifier + " ";
    if (type.isPresent()) {
      out += type.get().toString();
      //out += "size:" + width();
    } else {
      out += "unresolved";
    }
    return out;
  }
}
