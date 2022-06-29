package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

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
  public void setSymbolCatalog(SymbolCatalogOld symbols) {
    a.setSymbolCatalog(symbols);
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
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
