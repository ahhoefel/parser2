package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolTable {
  private List<Symbol> symbols;
  private List<Symbol> unmodifiableSymbols;

  public static class NonTerminalTable extends SymbolTable {
    private Symbol startp;
    private Symbol start;

    public NonTerminalTable() {
      super();
      startp = newSymbol("startp");
      start = newSymbol("start");
    }

    public Symbol getAugmentedStart() {
      return startp;
    }

    public Symbol getStart() {
      return start;
    }
  }

  public static class TerminalTable extends SymbolTable {
    private Symbol eof;

    public TerminalTable() {
      super();
      eof = newSymbol("eof");
    }

    public Symbol getEof() {
      return eof;
    }
  }

  public SymbolTable() {
    symbols = new ArrayList<>();
    unmodifiableSymbols = Collections.unmodifiableList(symbols);
  }

  public Symbol newSymbol(String label) {
    Symbol s = new Symbol(label, symbols.size());
    symbols.add(s);
    return s;
  }

  public List<Symbol> getSymbols() {
    return unmodifiableSymbols;
  }

  public int size() {
    return unmodifiableSymbols.size();
  }

  public boolean contains(Symbol s) {
    return s.getIndex() < symbols.size() && symbols.get(s.getIndex()) == s;
  }
}
