package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.ast.Expression;
import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;

public class DebugFunctionReturnOp implements Operation {

  private Expression expression;

  public DebugFunctionReturnOp(Expression expression) {
    this.expression = expression;
  }

  @Override
  public void run(Context context) {
    System.out.println(context.getStackIndent() + "return " + context.getRegister(expression.getRegister()));
    context.decrementStackDepth();
  }

  public String toString() {
    return "DBG Function return: " + expression.getRegister();
  }
}
