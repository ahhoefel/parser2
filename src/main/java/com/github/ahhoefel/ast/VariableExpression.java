package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

public class VariableExpression implements Expression {

  private final String identifier;
  private SymbolCatalog symbols;
  private Type type;
  private CodeLocation location;

  private Register register;

  public VariableExpression(Token t) {
    this.location = t.getLocation();
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
    VariableDeclaration variable = symbols.getVariable(identifier).get();
    rep.add(new SetOp(variable.getRegister(), register, 0, 0, variable.getType().width()));
    liveRegisters.add(register);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    if (type != null) {
      return Optional.of(type);
    }
    Optional<VariableDeclaration> var = symbols.getVariable(identifier);
    if (!var.isPresent()) {
      log.add(new ParseError(location, "Variable not declared: " + identifier));
      return Optional.empty();
    }
    type = var.get().getType(log);
    register.setWidth(type.width());
    //System.out.println(register + " " + type.width());
    return Optional.of(type);
  }

  @Override
  public Type getType() {
    return type;
  }

  public String getIdentifier() {
    return identifier;
  }
}
