package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class EqualOp implements Operation {

  private Register a;
  private Register b;
  private Register destination;

  public EqualOp(Register a, Register b, Register destination) {
    this.a = a;
    this.b = b;
    this.destination = destination;
  }

  public String toString() {
    return String.format("EQU  %s %s -> %s", a, b, destination);
  }

  @Override
  public void run(Context context) {
    context.putRegister(destination, context.getRegister(a) == context.getRegister(b) ? 1 : 0);
  }
}
