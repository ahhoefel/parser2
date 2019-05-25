package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Operation;

public class DestinationOp implements Operation {

  private Label label;

  public DestinationOp(Label label) {
    this.label = label;
  }

  public String toString() {
    return String.format("DEST %s", label);
  }

  public void setLocation(int index) {
    label.setIndex(index);
  }

  @Override
  public void run(Context context) {
  }
}
