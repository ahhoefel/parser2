package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.NegateOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class NotExpression implements Expression {

  private Expression a;
  private Register register;

  public NotExpression(Expression a) {
    this.a = a;
    this.register = new Register(1);
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("!");
    a.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    a.addToRepresentation(rep, liveRegisters);
    rep.add(new NegateOp(a.getRegister(), register));
    liveRegisters.remove(liveRegisters.size() - 1);
    liveRegisters.add(register);
  }

  @Override
  public Type getType() {
    if (a.getType() != Type.BOOL) {
      throw new RuntimeException("Negation not defined for type: " + a.getType());
    }
    return Type.BOOL;
  }
}
