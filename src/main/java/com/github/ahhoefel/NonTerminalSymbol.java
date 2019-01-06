package com.github.ahhoefel;


public class NonTerminalSymbol extends Symbol {
  public NonTerminalSymbol(String label, int index) {
    super(label, index);
  }

  public boolean isTerminal() {
    return false;
  }
}
