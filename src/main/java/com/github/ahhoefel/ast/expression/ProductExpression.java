package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.MultiplyOp;
import com.github.ahhoefel.ast.Visitor;

import java.util.List;
import java.util.Optional;

public class ProductExpression extends ExpressionAdapter {

  private Expression a;
  private Expression b;

  public ProductExpression(Expression a, Expression b) {
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

  @Override
  public Register getRegister() {
    return register;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
    b.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    a.addToRepresentation(rep, liveRegisters);
    b.addToRepresentation(rep, liveRegisters);
    rep.add(new MultiplyOp(a.getRegister(), b.getRegister(), register));
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
      log.add(new ParseError(null, "Product does not apply to types: " + a.getType() + " " + b.getType()));
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
