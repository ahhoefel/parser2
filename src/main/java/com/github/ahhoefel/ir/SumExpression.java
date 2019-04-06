package com.github.ahhoefel.ir;

public class SumExpression implements Expression {

  private Expression a;
  private Expression b;

  public SumExpression(Expression a, Expression b) {
    this.a = a;
    this.b = b;
  }

  public int eval() {
    return a.eval() + b.eval();
  }
}
