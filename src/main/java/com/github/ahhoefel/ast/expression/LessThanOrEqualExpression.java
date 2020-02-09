package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LessThanOrEqualOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public class LessThanOrEqualExpression extends ExpressionAdapter {

  private Expression a;
  private Expression b;

  public LessThanOrEqualExpression(Expression a, Expression b) {
    super(1);
    this.a = a;
    this.b = b;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    a.toIndentedString(out);
    out.add(" <= ");
    b.toIndentedString(out);
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
    rep.add(new LessThanOrEqualOp(a.getRegister(), b.getRegister(), register));
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
      log.add(new ParseError(null, "Inequality does not apply to types: " + a.getType() + " " + b.getType()));
    }
    return Optional.of(Type.BOOL);
  }

  @Override
  public Type getType() {
    return Type.BOOL;
  }

  @Override
  public boolean isLValue() {
    return false;
  }
}