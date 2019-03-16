package com.github.ahhoefel;

public class Token {

  private final String value;
  private final TerminalSymbol symbol;

  public Token(TerminalSymbol symbol, String value) {
    this.symbol = symbol;
    this.value = value;
  }

  public String toString() {
    return String.format("%s(%s)", symbol, value);
  }

  public TerminalSymbol getTerminal() {
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
