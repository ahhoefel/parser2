package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.parser.Token;

public class LValue {
  private String identifier;
  private Type type;
  private boolean declaration;
  private SymbolCatalog symbols;

  public LValue(Token token) {
    this.identifier = token.getValue();
    declaration = false;
  }

  public String getIdentifier() {
    return identifier;
  }

  public boolean isDeclaration() {
    return declaration;
  }

  public Type getType() {
    return type;
  }

  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
    if (declaration) {
      symbols.addVariable(new VariableDeclaration(this));
    }
  }

  public Register getRegister() {
    return symbols.getVariable(identifier).getRegister();
  }

  public static LValue withDeclaration(Token identifer, Type type) {
    LValue v = new LValue(identifer);
    v.declaration = true;
    v.type = type;
    return v;
  }

  public String toString() {
    if (isDeclaration()) {
      return String.format("var %s %s", identifier, type.toString());
    }
    return identifier;
  }
}
