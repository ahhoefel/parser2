package com.github.ahhoefel.ast.statements;

import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.parser.ErrorLog;

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

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
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
