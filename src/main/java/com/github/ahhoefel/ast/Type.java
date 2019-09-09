package com.github.ahhoefel.ast;

public interface Type {

  class IntType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog) {

    }

    @Override
    public int width() {
      return 1;
    }

    public String toString() {
      return "int";
    }

    public boolean equals(Object o) {
      return o == INT;
    }
  }

  class BooleanType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog) {

    }

    @Override
    public int width() {
      return 1;
    }

    public String toString() {
      return "bool";
    }

    public boolean equals(Object o) {
      return o == BOOL;
    }
  }

  class StringType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog) {
    }

    @Override
    public int width() {
      return 1;
    }

    public String toString() {
      return "string";
    }

    public boolean equals(Object o) {
      return o == STRING;
    }
  }

  class VoidType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog) {
    }

    @Override
    public int width() {
      return 0;
    }

    public String toString() {
      return "void";
    }

    public boolean equals(Object o) {
      return o == VOID;
    }
  }

  void linkTypes(SymbolCatalog catalog);

  int width();

  Type INT = new IntType();
  Type STRING = new StringType();
  Type BOOL = new BooleanType();
  Type VOID = new VoidType();
}
