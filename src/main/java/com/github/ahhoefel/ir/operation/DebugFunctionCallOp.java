package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Alloc;
import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class DebugFunctionCallOp implements Operation {

  private FunctionDeclaration function;
  private List<Expression> args;

  public DebugFunctionCallOp(FunctionDeclaration function, List<Expression> args) {
    this.function = function;
    this.args = args;
  }

  @Override
  public void run(Context context) {
    List<Alloc> argValues = new ArrayList<>();
    for (Expression arg : args) {
      argValues.add(context.getRegister(arg.getRegister()));
    }
    context.incrementStackDepth();
    System.out.print(context.getStackIndent());
    System.out.print("func " + function.getName() + "(");
    System.out.print(function.formatParametersWithValues(argValues));
    System.out.println(")");
  }

  public String toString() {
    return "DBG function call " + function.getName();
  }
}
