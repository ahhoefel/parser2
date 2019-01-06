package com.github.ahhoefel;

public class TerminalSymbol extends Symbol {
  public TerminalSymbol(String label, int index) {
    super(label, index);
  }

  public boolean isTerminal() {
    return true;
  }
}
