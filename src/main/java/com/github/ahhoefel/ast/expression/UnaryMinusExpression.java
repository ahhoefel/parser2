package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.ir.operation.SubtractOp;
import com.github.ahhoefel.util.IndentedString;

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

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("-");
    a.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
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