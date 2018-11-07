package com.github.ahhoefel;

import java.util.List;

public class Rule {
  private NonTerminalSymbol source;
  private List<Symbol> symbols;

  public Rule(NonTerminalSymbol source, List<Symbol> symbols) {
    this.source = source;
    this.symbols = symbols;
  }

  public NonTerminalSymbol getSource() {return source;}
  public List<Symbol> getSymbols() {
    return symbols;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append(source.toString());
    buf.append(" ->");
    for (Symbol symbol : symbols) {
      buf.append(" ").append(symbol.toString());
    }
    return buf.toString();
  }
}