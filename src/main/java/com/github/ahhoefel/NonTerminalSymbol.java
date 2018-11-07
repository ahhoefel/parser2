package com.github.ahhoefel;

public class NonTerminalSymbol implements Symbol {
  private String label;

  public NonTerminalSymbol(String label) {
    this.label = label;
  }

  @Override
  public boolean isTerminal() {
    return false;
  }

  public String toString() {
    return label;
  }

  public boolean equals(Object o) {
    if (o instanceof NonTerminalSymbol) {
      return ((NonTerminalSymbol) o).label.equals(label);
    }
    return false;
  }
}
