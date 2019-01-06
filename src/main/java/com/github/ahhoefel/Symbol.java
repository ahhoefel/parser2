package com.github.ahhoefel;

public abstract class Symbol {
  private String label;
  private int index;

  public Symbol(String label, int index) {
    this.label = label;
    this.index = index;
  }

  public abstract boolean isTerminal();

  public NonTerminalSymbol getNonTerminal() {
    return (NonTerminalSymbol) this;
  }

  public TerminalSymbol getTerminal() {
    return (TerminalSymbol) this;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public String toString() {
    return label;
  }

  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public int hashCode() {
    return index;
  }
}
