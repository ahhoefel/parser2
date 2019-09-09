package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class VariableExpression implements Expression {

  private final String identifier;
  private SymbolCatalog symbols;

  private Register register;

  public VariableExpression(Token t) {
    this.identifier = t.getValue();
    this.register = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add(identifier);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    VariableDeclaration variable = symbols.getVariable(identifier);
    rep.add(new SetOp(variable.getRegister(), register));
    liveRegisters.add(register);
  }

  @Override
  public Type getType() {
    VariableDeclaration var = symbols.getVariable(identifier);
    return var.getType();
  }

  public String getIdentifier() {
    return identifier;
  }


}
