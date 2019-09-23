package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Alloc;
import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class NegateOp implements Operation {

  private Register a;
  private Register destination;

  public NegateOp(Register a, Register destination) {
    this.a = a;
    this.destination = destination;
  }

  public String toString() {
    return String.format("EQZ  %s -> %s", a, destination);
  }

  @Override
  public void run(Context context) {
    Alloc v = new Alloc(1);
    if (context.getRegister(a).equalsZero()) {
      v.setWord(0, 1);
    }
    context.copyToRegister(destination, v);
  }
}
