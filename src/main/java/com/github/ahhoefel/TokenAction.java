package com.github.ahhoefel;

import java.util.function.Function;

public class TokenAction implements Function<Object[], Object> {

  private Symbol terminal;

  public TokenAction(Symbol terminal) {
    this.terminal = terminal;
  }

  @Override
  public Object apply(Object[] objects) {
    if (objects[0] instanceof String) {
      return new Token(terminal, (String) objects[0]);
    }
    return new Token(terminal, ((Token) objects[0]).getValue());
  }
}
