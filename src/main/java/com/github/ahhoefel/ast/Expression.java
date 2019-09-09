package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public interface Expression {
  Register getRegister();

  void toIndentedString(IndentedString out);

  void setSymbolCatalog(SymbolCatalog symbols);

  void addToRepresentation(Representation rep, List<Register> liveRegisters);

  Type getType();
}
