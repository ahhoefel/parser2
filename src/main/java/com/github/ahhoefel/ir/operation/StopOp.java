package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;

public class StopOp implements Operation {
  private String message;

  public StopOp(String message) {
    this.message = message;
  }

  public String toString() {
    return String.format("STOP %s", message);
  }

  @Override
  public void run(Context context) {
    context.stop(message);
  }
}
