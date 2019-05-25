package com.github.ahhoefel.ast;

import com.github.ahhoefel.Token;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

public class MemberAccessExpression implements Expression {

  private final Token member;
  private final Expression expression;
  private SymbolCatalog symbols;
  private Register register;

  public MemberAccessExpression(Expression expression, Token member) {
    this.member = member;
    this.expression = expression;
    this.register = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    expression.toIndentedString(out);
    out.add(".");
    out.add(member.getValue());
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
    expression.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep) {
    expression.addToRepresentation(rep);
    throw new RuntimeException("not implemented");
  }
}
