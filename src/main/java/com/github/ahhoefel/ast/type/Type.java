package com.github.ahhoefel.ast.type;

import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Visitable;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.parser.ErrorLog;

public interface Type extends Visitable {

  class IntType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog, ErrorLog log) {

    }

    @Override
    public int width() {
      return 64;
    }

    public String toString() {
      return "int";
    }

    public boolean equals(Object o) {
      return o == INT;
    }

    @Override
    public void accept(Visitor v, Object... objs) {
      v.visit(this, objs);
    }
  }

  class BooleanType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog, ErrorLog log) {

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

    @Override
    public void accept(Visitor v, Object... objs) {
      v.visit(this, objs);
    }
  }

  class StringType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog, ErrorLog log) {
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

    @Override
    public void accept(Visitor v, Object... objs) {
      v.visit(this, objs);
    }
  }

  class VoidType implements Type {
    @Override
    public void linkTypes(SymbolCatalog catalog, ErrorLog log) {
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

    @Override
    public void accept(Visitor v, Object... objs) {
      v.visit(this, objs);
    }
  }

  void linkTypes(SymbolCatalog catalog, ErrorLog log);

  int width();

  Type INT = new IntType();
  Type STRING = new StringType();
  Type BOOL = new BooleanType();
  Type VOID = new VoidType();
}
