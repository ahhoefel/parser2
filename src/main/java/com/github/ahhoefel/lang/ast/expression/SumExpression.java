package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.AddOp;
import com.github.ahhoefel.lang.ast.ParseError;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.List;
import java.util.Optional;

public class SumExpression extends ExpressionAdapter {

  private Expression a;
  private Expression b;

  public SumExpression(Expression a, Expression b) {
    super(64);
    this.a = a;
    this.b = b;
  }

  public Expression getLeft() {
    return a;
  }

  public Expression getRight() {
    return b;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalogOld symbols) {
    a.setSymbolCatalog(symbols);
    b.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    a.addToRepresentation(rep, liveRegisters);
    b.addToRepresentation(rep, liveRegisters);
    rep.add(new AddOp(a.getRegister(), b.getRegister(), register));
    a.removeLiveRegisters(liveRegisters);
    b.removeLiveRegisters(liveRegisters);
    addLiveRegisters(liveRegisters);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    Optional<Type> aType = a.checkType(log);
    Optional<Type> bType = b.checkType(log);
    if (!aType.isPresent() || !bType.isPresent()) {
      return Optional.empty();
    }

    if (aType.get() != Type.INT || bType.get() != Type.INT) {
      log.add(new ParseError(null, "Addition not defined for types: " + a.getType() + " " + b.getType()));
      return Optional.empty();
    }
    return Optional.of(Type.INT);
  }

  @Override
  public Type getType() {
    return Type.INT;
  }

  @Override
  public boolean isLValue() {
    return false;
  }
}
