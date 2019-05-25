package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;

public class CommentOp implements Operation {

  private String comment;

  public CommentOp(String comment) {
    this.comment = comment;
  }

  public String toString() {
    return String.format("COMM %s", comment);
  }

  @Override
  public void run(Context context) {

  }
}
