package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class PopOp implements Operation {

  private Register register;

  public PopOp(Register register) {
    this.register = register;
  }

  public String toString() {
    return String.format("POP %s", register);
  }

  @Override
  public void run(Context context) {
    context.pop(register);
  }
}
