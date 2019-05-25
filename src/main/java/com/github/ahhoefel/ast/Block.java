package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;
import java.util.List;

public class Block {

  public List<Statement> statements;
  public SymbolCatalog symbols;

  public Block() {
    this.statements = new ArrayList<>();
    this.symbols = new SymbolCatalog();
  }

  public Statement get(int i) {
    return statements.get(i);
  }

  public int size() {
    return statements.size();
  }

  public void add(Statement statement) {
    this.statements.add(statement);
    statement.addToSymbolCatalog(symbols);
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

  public void toIndentedString(IndentedString out) {
    for (Statement statement : statements) {
      statement.toIndentedString(out);
    }
  }
}
