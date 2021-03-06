package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Alloc;
import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class LiteralLabelOp implements Operation {

  private Label label;
  private Register destination;

  public LiteralLabelOp(Label label, Register destination) {
    this.label = label;
    this.destination = destination;
  }

  public String toString() {
    return String.format("ADDR %s -> %s", label, destination);
  }

  @Override
  public void run(Context context) {
    Alloc v = new Alloc(64);
    v.setWord(0, label.getIndex());
    context.copyToRegister(destination, v);
  }
}
