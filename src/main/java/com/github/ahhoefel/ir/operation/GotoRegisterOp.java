package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class GotoRegisterOp implements Operation {
  private Register destination;

  public GotoRegisterOp(Register destination) {
    this.destination = destination;
  }

  public String toString() {
    return String.format("GOTO %s", destination);
  }

  @Override
  public void run(Context context) {
    int dest = context.getRegister(destination);
    context.setIndex(dest);
  }
}
