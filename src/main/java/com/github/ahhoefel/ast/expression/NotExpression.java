package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.NegateOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public class NotExpression extends ExpressionAdapter {

  private Expression a;

  public NotExpression(Expression a) {
    super(1);
    this.a = a;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("!");
    a.toIndentedString(out);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    a.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    a.addToRepresentation(rep, liveRegisters);
    rep.add(new NegateOp(a.getRegister(), register));
    a.removeLiveRegisters(liveRegisters);
    addLiveRegisters(liveRegisters);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    Optional<Type> aType = a.checkType(log);
    if (!aType.isPresent()) {
      return Optional.empty();
    }
    if (aType.get() != Type.BOOL) {
      log.add(new ParseError(null, "Negation not defined for type: " + a.getType()));
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
