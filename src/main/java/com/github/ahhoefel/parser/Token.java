package com.github.ahhoefel.parser;

import com.github.ahhoefel.lang.ast.CodeLocation;

public class Token {

  private final String value;
  private final Symbol symbol;
  private CodeLocation location;

  public Token(Symbol symbol, String value, CodeLocation location) {
    this.symbol = symbol;
    this.value = value;
    this.location = location;
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

  public CodeLocation getLocation() {
    return location;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Token)) {
      return false;
    }
    Token t = (Token) o;
    return value.equals(t.value) && symbol.equals(t.symbol);
  }
}
