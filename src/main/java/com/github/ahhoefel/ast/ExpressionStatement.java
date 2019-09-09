package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;

public class ExpressionStatement implements Statement {

  private final Expression expression;

  public ExpressionStatement(Expression expression) {
    this.expression = expression;
  }

  public Expression getExpression() {
    return expression;
  }

  public void addToSymbolCatalog(SymbolCatalog symbolCatalog) {
    expression.setSymbolCatalog(symbolCatalog);
  }

  @Override
  public void toIndentedString(IndentedString out) {
    expression.toIndentedString(out);
    out.endLine();
  }

  @Override
  public void addToRepresentation(Representation rep) {
    expression.addToRepresentation(rep, new ArrayList<>());
  }

  @Override
  public void typeCheck() {
    if (expression.getType() != Type.VOID) {
      throw new RuntimeException("Expression statement should have void type. Got: " + expression.getType());
    }
  }


}
