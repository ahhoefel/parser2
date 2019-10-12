package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

public interface Statement {
  void addToSymbolCatalog(SymbolCatalog symbols);

  void toIndentedString(IndentedString out);

  void addToRepresentation(Representation rep);

  void typeCheck(ErrorLog log);
}
