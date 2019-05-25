package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class PushOp implements Operation {

  private Register register;

  public PushOp(Register register) {
    this.register = register;
  }

  public String toString() {
    return String.format("PUSH %s", register);
  }

  @Override
  public void run(Context context) {
    context.push(register);
  }
}
