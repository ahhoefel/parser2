package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.List;

public class SymbolFactory {
  private List<NonTerminalSymbol> nonTerminals;
  private List<TerminalSymbol> terminals;
  private NonTerminalSymbol startp;
  private NonTerminalSymbol start;
  private TerminalSymbol eof;

  public SymbolFactory(Builder builder) {
    nonTerminals = new ArrayList<>();
    terminals = new ArrayList<>();
    startp = new NonTerminalSymbol("startp", 0);
    start = new NonTerminalSymbol("start", 1);
    eof = new TerminalSymbol("eof", 0);
    nonTerminals.add(startp);
    nonTerminals.add(start);
    nonTerminals.addAll(builder.nonTerminals);
    terminals.add(eof);
    terminals.addAll(builder.terminals);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private List<NonTerminalSymbol> nonTerminals;
    private List<TerminalSymbol> terminals;

    private Builder() {
      this.nonTerminals = new ArrayList<>();
      this.terminals = new ArrayList<>();
    }

    public NonTerminalSymbol newNonTerminal(String label) {
      NonTerminalSymbol s = new NonTerminalSymbol(label, nonTerminals.size() + 2);
      nonTerminals.add(s);
      return s;
    }

    public TerminalSymbol newTerminal(String label) {
      TerminalSymbol s = new TerminalSymbol(label, terminals.size() + 1);
      terminals.add(s);
      return s;
    }

    public SymbolFactory build() {
      return new SymbolFactory(this);
    }
  }

  public List<NonTerminalSymbol> getNonTerminals() {
    return nonTerminals;
  }

  public List<TerminalSymbol> getTerminals() {
    return terminals;
  }

  public NonTerminalSymbol getAugmentedStart() {
    return startp;
  }

  public NonTerminalSymbol getStart() {
    return start;
  }

  public TerminalSymbol getEof() {
    return eof;
  }
}
