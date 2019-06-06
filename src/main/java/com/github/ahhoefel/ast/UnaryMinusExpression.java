package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.ir.operation.SubtractOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class UnaryMinusExpression implements Expression {

  private Expression a;
  private Register register;
  private Register zeroRegister;

  public UnaryMinusExpression(Expression a) {
    this.a = a;
    this.register = new Register();
    this.zeroRegister = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("-");
    a.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    a.addToRepresentation(rep, liveRegisters);
    rep.add(new LiteralOp(0, zeroRegister));
    rep.add(new SubtractOp(zeroRegister, a.getRegister(), register));
    liveRegisters.remove(liveRegisters.size() - 1);
    liveRegisters.add(register);
  }
}
