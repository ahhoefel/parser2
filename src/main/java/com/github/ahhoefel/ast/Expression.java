package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public interface Expression {
  Register getRegister();

  void toIndentedString(IndentedString out);

  void setSymbolCatalog(SymbolCatalog symbols);

  void addToRepresentation(Representation rep, List<Register> liveRegisters);

  Optional<Type> checkType(ErrorLog log);
  Type getType();
}
