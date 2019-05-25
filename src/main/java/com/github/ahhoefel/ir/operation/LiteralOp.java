package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class LiteralOp implements Operation {

  private int value;
  private Register destination;

  public LiteralOp(int value, Register destination) {
    this.value = value;
    this.destination = destination;
  }

  public String toString() {
    return String.format("LIT  %s -> %s", value, destination);
  }

  @Override
  public void run(Context context) {
    context.putRegister(destination, value);
  }
}
