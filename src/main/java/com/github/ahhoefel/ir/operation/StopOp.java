package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class StopOp implements Operation {
  private String message;
  private Register register;
  private Type returnType;

  public StopOp(String message) {
    this.message = message;
  }

  public StopOp(Register r, Type returnType) {
    this.register = r;
    this.returnType = returnType;
  }

  public String toString() {
    if (message != null) {
      return String.format("STOP %s", message);
    }
    return String.format("STOP %s type %s", register, returnType);
  }

  @Override
  public void run(Context context) {
    if (message != null) {
      context.stop(message);
    }
    context.stop(register, returnType);
  }
}
