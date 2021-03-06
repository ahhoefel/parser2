package com.github.ahhoefel.ast.statements;

import com.github.ahhoefel.ast.SymbolCatalogOld;
import com.github.ahhoefel.ast.Visitable;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.parser.ErrorLog;

public interface Statement extends Visitable {
  void addToSymbolCatalog(SymbolCatalogOld symbols);

  void addToRepresentation(Representation rep);

  void typeCheck(ErrorLog log);
}
