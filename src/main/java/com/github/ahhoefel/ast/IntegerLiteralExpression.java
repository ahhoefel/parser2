package com.github.ahhoefel.ast;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.LiteralOp;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public class IntegerLiteralExpression implements Expression {

  private int value;
  private Register register;
  private SymbolCatalog symbols;

  public IntegerLiteralExpression(Token t) {
    String text = t.getValue();
    this.value = Integer.parseInt(text);
    this.register = new Register();
  }

  public int eval(Context context) {
    return value;
  }

  @Override
  public Register getRegister() {
    return register;
  }

  public void toIndentedString(IndentedString out) {
    out.add(Integer.toString(value));
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    rep.add(new LiteralOp(value, register));
    liveRegisters.add(register);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    return Optional.of(Type.INT);
  }

  @Override
  public Type getType() {
    return Type.INT;
  }
}
