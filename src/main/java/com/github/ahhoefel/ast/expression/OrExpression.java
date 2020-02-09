package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.OrOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public class OrExpression implements Expression {

  private Expression a;
  private Expression b;
  private Register register;

  public OrExpression(Expression a, Expression b) {
    this.a = a;
    this.b = b;
    this.register = new Register(1);
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    a.toIndentedString(out);
    out.add(" || ");
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
    rep.add(new OrOp(a.getRegister(), b.getRegister(), register));
    a.removeLiveRegisters(liveRegisters);
    b.removeLiveRegisters(liveRegisters);
    addLiveRegisters(liveRegisters);
  }

  @Override
  public void addLiveRegisters(List<Register> stack) {
    stack.add(register);
  }

  @Override
  public void removeLiveRegisters(List<Register> stack) {
    stack.remove(stack.size() - 1);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    Optional<Type> aType = a.checkType(log);
    Optional<Type> bType = b.checkType(log);
    if (!aType.isPresent() || !bType.isPresent()) {
      return Optional.empty();
    }

    if (aType.get() != Type.BOOL || bType.get() != Type.BOOL) {
      log.add(new ParseError(null, "Logical or not defined for types: " + a.getType() + " " + b.getType()));
      return Optional.empty();
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