package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.MultiplyOp;
import com.github.ahhoefel.util.IndentedString;

public class ProductExpression implements Expression {

  private Expression a;
  private Expression b;
  private Register register;

  public ProductExpression(Expression a, Expression b) {
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
    out.add(" * ");
    b.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
    b.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep) {
    a.addToRepresentation(rep);
    b.addToRepresentation(rep);
    rep.add(new MultiplyOp(a.getRegister(), b.getRegister(), register));
  }
}
