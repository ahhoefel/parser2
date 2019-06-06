package com.github.ahhoefel.parser;

public class Token {

  private final String value;
  private final Symbol symbol;

  public Token(Symbol symbol, String value) {
    this.symbol = symbol;
    this.value = value;
  }

  public String toString() {
    return String.format("%s(%s)", symbol, value);
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public String getValue() {
    return value;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Token)) {
      return false;
    }
    Token t = (Token) o;
    return value.equals(t.value) && symbol.equals(t.symbol);
  }
}
