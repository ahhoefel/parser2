package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

public class MethodInvocationExpression implements Expression {
  private FunctionInvocationExpression functionInvocation;
  private Expression expression;
  private Register register;

  public MethodInvocationExpression(Expression expression, FunctionInvocationExpression functionInvocation) {
    this.functionInvocation = functionInvocation;
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
    functionInvocation.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    expression.setSymbolCatalog(symbols);
    functionInvocation.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep) {
    expression.addToRepresentation(rep);
    functionInvocation.addToRepresentation(rep);
    // We need to look up the type of the expression being called on.
    throw new RuntimeException("Not implemented");
  }
}
