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
}
