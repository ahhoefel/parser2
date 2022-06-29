package com.github.ahhoefel.lang.ast.type;

import java.util.Optional;

import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.parser.ErrorLog;

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

  public String getIdentifier() {
    return identifier;
  }

  public Optional<String> getPackage() {
    return packagePrefix;
  }

  public void linkTypes(SymbolCatalogOld symbols, ErrorLog log) {
    Type t = symbols.getType(packagePrefix, identifier, log);
    type = Optional.ofNullable(t);
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
