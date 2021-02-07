package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Representation;

public interface Statement extends Visitable {
  void addToSymbolCatalog(SymbolCatalog symbols);

  void addToRepresentation(Representation rep);

  void typeCheck(ErrorLog log);
}
