package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Operation;

public class GotoOp implements Operation {

  private Label label;

  public GotoOp(Label label) {
    this.label = label;
  }

  public String toString() {
    return String.format("GOTO %s", label);
  }

  @Override
  public void run(Context context) {
    context.setIndex(label.getIndex());
  }
}
