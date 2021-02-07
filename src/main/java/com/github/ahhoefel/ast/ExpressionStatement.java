package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ir.Representation;
import java.util.ArrayList;
import java.util.Optional;

public class ExpressionStatement implements Statement {

  private final Expression expression;

  public ExpressionStatement(Expression expression) {
    this.expression = expression;
  }

  public Expression getExpression() {
    return expression;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public void addToSymbolCatalog(SymbolCatalog symbolCatalog) {
    expression.setSymbolCatalog(symbolCatalog);
  }

  @Override
  public void addToRepresentation(Representation rep) {
    expression.addToRepresentation(rep, new ArrayList<>());
  }

  @Override
  public void typeCheck(ErrorLog log) {
    Optional<Type> type = expression.checkType(log);
    if (type.isPresent() && type.get() != Type.VOID) {
      log.add(new ParseError(null, "Expression statement should have void type. Got: " + expression.getType()));
    }
  }
}
