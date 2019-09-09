package com.github.ahhoefel.ast;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class BooleanLiteralExpression implements Expression {

  private boolean value;
  private Register register;
  private SymbolCatalog symbols;

  public BooleanLiteralExpression(boolean value) {
    this.value = value;
    this.register = new Register();
  }

  public boolean eval(Context context) {
    return value;
  }

  @Override
  public Register getRegister() {
    return register;
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
    liveRegisters.add(register);
  }

  @Override
  public Type getType() {
    return Type.BOOL;
  }
}
