package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.type.Type;

import java.util.List;
import java.util.Optional;

public class ParenthesesExpression extends ExpressionAdapter {

  private Expression a;

  public ParenthesesExpression(Expression a) {
    super(1);
    this.a = a;
  }

  public Expression getExpression() {
    return a;
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    return a.checkType(log);
  }

  @Override
  public Type getType() {
    return a.getType();
  }

  @Override
  public boolean isLValue() {
    return false;
  }
}
