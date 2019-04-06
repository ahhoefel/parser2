package com.github.ahhoefel.ir;

import com.github.ahhoefel.Token;

public class LiteralExpression implements Expression {

  private int value;

  public LiteralExpression(int value) {
    this.value = value;
  }

  public LiteralExpression(Object o) {
    String text = ((Token) o).getValue();
    this.value = Integer.parseInt(text);
  }

  public int eval() {
    return value;
  }
}
