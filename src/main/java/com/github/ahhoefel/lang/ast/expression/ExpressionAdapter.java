package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.ir.Register;

import java.util.List;

public abstract class ExpressionAdapter implements Expression {

  protected Register register;

  public ExpressionAdapter(int width) {
    register = new Register(width);
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void addLiveRegisters(List<Register> stack) {
    stack.add(register);
  }

  @Override
  public void removeLiveRegisters(List<Register> stack) {
    stack.remove(stack.size() - 1);
  }
}
