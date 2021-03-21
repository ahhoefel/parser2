package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.parser.ErrorLog;

public class VariableDeclaration {

  private String name;
  private Type type;
  private Register register;

  public VariableDeclaration(String name, Type type) {
    this.name = name;
    this.type = type;
    this.register = new Register();
  }

  public VariableDeclaration(LValue value) {
    if (!value.isDeclaration()) {
      throw new RuntimeException("Variable declarations can only be made from LValues that are declarations.");
    }
    this.name = value.getIdentifier();
    this.type = value.getType();
    this.register = new Register();
  }

  public String getName() {
    return name;
  }

  public Register getRegister() {
    return register;
  }

  public Type getType(ErrorLog log) {
    register.setWidth(type.width());
    return type;
  }

  public Type getType() {
    return type;
  }

  public String toString() {
    return String.format("var %s %s: %s", name, type, register);
  }
}
