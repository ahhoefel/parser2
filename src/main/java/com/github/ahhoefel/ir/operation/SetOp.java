package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class SetOp implements Operation {

  private Register a;
  private Register destination;

  public SetOp(Register a, Register destination) {
    this.a = a;
    this.destination = destination;
  }

  public String toString() {
    return String.format("SET  %s -> %s", a, destination);
  }

  @Override
  public void run(Context context) {
    context.putRegister(destination, context.getRegister(a));
  }
}
