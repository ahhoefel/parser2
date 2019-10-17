package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
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

  void addLiveRegisters(List<Register> stack);

  void removeLiveRegisters(List<Register> stack);

  boolean isLValue();

  Optional<Type> checkType(ErrorLog log);
  Type getType();
}
