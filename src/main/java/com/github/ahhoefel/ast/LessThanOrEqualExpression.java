package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LessThanOrEqualOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class LessThanOrEqualExpression implements Expression {

  private Expression a;
  private Expression b;
  private Register register;

  public LessThanOrEqualExpression(Expression a, Expression b) {
    this.a = a;
    this.b = b;
    this.register = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    a.toIndentedString(out);
    out.add(" <= ");
    b.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
    b.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    a.addToRepresentation(rep, liveRegisters);
    b.addToRepresentation(rep, liveRegisters);
    rep.add(new LessThanOrEqualOp(a.getRegister(), b.getRegister(), register));
    liveRegisters.remove(liveRegisters.size() - 1);
    liveRegisters.remove(liveRegisters.size() - 1);
    liveRegisters.add(register);
  }

  @Override
  public Type getType() {
    if (a.getType() != Type.INT || b.getType() != Type.INT) {
      throw new RuntimeException("Inequality does not apply to types: " + a.getType() + " " + b.getType());
    }
    return Type.BOOL;
  }
}
