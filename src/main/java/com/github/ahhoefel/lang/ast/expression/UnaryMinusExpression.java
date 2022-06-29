package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.ir.operation.SubtractOp;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.List;
import java.util.Optional;

public class UnaryMinusExpression extends ExpressionAdapter {

  private Expression a;
  private Register zeroRegister;

  public UnaryMinusExpression(Expression a) {
    super(64);
    this.a = a;
    this.zeroRegister = new Register();
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
    a.addToRepresentation(rep, liveRegisters);
    rep.add(new LiteralOp(0, zeroRegister));
    rep.add(new SubtractOp(zeroRegister, a.getRegister(), register));
    a.removeLiveRegisters(liveRegisters);
    addLiveRegisters(liveRegisters);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    Optional<Type> aType = a.checkType(log);
    if (!aType.isPresent()) {
      return Optional.empty();
    }
    if (aType.get() != Type.INT) {
      throw new RuntimeException("Unary minus not defined for type: " + a.getType());
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
