package com.github.ahhoefel;

public class TerminalSymbol<L> implements Symbol {

  private L label;

  public TerminalSymbol(L label) {
    this.label = label;
  }

  @Override
  public boolean isTerminal() {
    return true;
  }

  public String toString() {
    return label.toString();
  }

  public boolean equals(Object o) {
    if (o instanceof TerminalSymbol) {
      return ((TerminalSymbol<L>) o).label.equals(label);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return label.hashCode();
  }
}

