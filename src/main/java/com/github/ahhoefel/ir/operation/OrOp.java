package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class OrOp implements Operation {

  private Register a;
  private Register b;
  private Register destination;

  public OrOp(Register a, Register b, Register destination) {
    this.a = a;
    this.b = b;
    this.destination = destination;
  }

  public String toString() {
    return String.format("OR   %s %s -> %s", a, b, destination);
  }

  @Override
  public void run(Context context) {
    context.copyToRegister(destination, context.getRegister(a).or(context.getRegister(b)));
  }
}
