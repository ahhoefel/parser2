package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.List;
import java.util.Optional;

public interface Expression extends Visitable {
  Register getRegister();

  void setSymbolCatalog(SymbolCatalogOld symbols);

  void addToRepresentation(Representation rep, List<Register> liveRegisters);

  void addLiveRegisters(List<Register> stack);

  void removeLiveRegisters(List<Register> stack);

  boolean isLValue();

  Optional<Type> checkType(ErrorLog log);

  Type getType();
}
