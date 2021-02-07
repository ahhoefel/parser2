package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Representation;

import java.util.ArrayList;
import java.util.List;

public class Block implements Visitable {

  public List<Statement> statements;
  public SymbolCatalog symbols;

  public Block() {
    this.statements = new ArrayList<>();
  }

  public Statement get(int i) {
    return statements.get(i);
  }

  public int size() {
    return statements.size();
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public void add(Statement statement) {
    this.statements.add(statement);
  }

  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
    for (Statement statement : statements) {
      statement.addToSymbolCatalog(symbols);
    }
  }

  public void addToRepresentation(Representation rep) {
    for (Statement statement : statements) {
      statement.addToRepresentation(rep);
    }
  }

  public void addToSymbolCatalog(SymbolCatalog symbols) {
    for (Statement statement : statements) {
      statement.addToSymbolCatalog(symbols);
    }
  }

  public void typeCheck(ErrorLog log) {
    for (Statement statement : statements) {
      statement.typeCheck(log);
    }
  }
}
