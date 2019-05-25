package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

public interface Expression {
  Register getRegister();

  void toIndentedString(IndentedString out);

  void setSymbolCatalog(SymbolCatalog symbols);

  void addToRepresentation(Representation rep);
}
