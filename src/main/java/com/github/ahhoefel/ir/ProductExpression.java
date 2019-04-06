package com.github.ahhoefel.ir;

public class ProductExpression implements Expression {

  private Expression a;
  private Expression b;

  public ProductExpression(Expression a, Expression b) {
    this.a = a;
    this.b = b;
  }

  public int eval() {
    return a.eval() * b.eval();
  }
}
