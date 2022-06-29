package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.Token;
import java.util.List;
import java.util.Optional;

public class IntegerLiteralExpression extends ExpressionAdapter {

  private int value;

  public IntegerLiteralExpression(Token t) {
    super(64);
    String text = t.getValue();
    this.value = Integer.parseInt(text);
  }

  public int eval(Context context) {
    return value;
  }

  @Override
  public Register getRegister() {
    return register;
  }

  public int getValue() {
    return value;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalogOld symbols) {
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    rep.add(new LiteralOp(value, register));
    addLiveRegisters(liveRegisters);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
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
