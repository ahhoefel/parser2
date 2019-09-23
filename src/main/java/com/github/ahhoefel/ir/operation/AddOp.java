package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class AddOp implements Operation {

  private Register a;
  private Register b;
  private Register destination;

  public AddOp(Register a, Register b, Register destination) {
    this.a = a;
    this.b = b;
    this.destination = destination;
  }

  public String toString() {
    return String.format("ADD  %s %s -> %s", a, b, destination);
  }

  @Override
  public void run(Context context) {
    context.copyToRegister(destination, context.getRegister(a).add(context.getRegister(b)));
  }
}
