package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public class BooleanLiteralExpression extends ExpressionAdapter {

  private boolean value;
  private SymbolCatalog symbols;

  public BooleanLiteralExpression(boolean value) {
    super(1);
    this.value = value;
  }

  public void toIndentedString(IndentedString out) {
    out.add(Boolean.toString(value));
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    rep.add(new LiteralOp(value ? 1 : 0, register));
    addLiveRegisters(liveRegisters);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    return Optional.of(Type.BOOL);
  }

  @Override
  public Type getType() {
    return Type.BOOL;
  }

  @Override
  public boolean isLValue() {
    return false;
  }
}
