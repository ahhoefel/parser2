package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class ConditionalGotoOp implements Operation {

  private Register register;
  private Label label;

  public ConditionalGotoOp(Register register, Label label) {
    this.register = register;
    this.label = label;
  }

  public String toString() {
    return String.format("COND %s, %s", register, label);
  }

  @Override
  public void run(Context context) {
    if (context.getRegister(register) != 0) {
      context.setIndex(label.getIndex());
    }
  }
}