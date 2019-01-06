package com.github.ahhoefel;

import java.util.List;

public class ExampleB {

  Grammar grammar;
  SymbolFactory.Builder sb;
  NonTerminalSymbol t;
  NonTerminalSymbol f;
  TerminalSymbol plus;
  TerminalSymbol times;
  TerminalSymbol n;
  TerminalSymbol lparen;
  TerminalSymbol rparen;
  SymbolFactory symbols;
  NonTerminalSymbol startp;
  NonTerminalSymbol start;
  TerminalSymbol eof;

  public ExampleB() {
    sb = SymbolFactory.newBuilder();
    t = sb.newNonTerminal("T");
    f = sb.newNonTerminal("F");
    plus = sb.newTerminal("+");
    times = sb.newTerminal("*");
    n = sb.newTerminal("n");
    lparen = sb.newTerminal("(");
    rparen = sb.newTerminal(")");
    symbols = sb.build();
    startp = symbols.getAugmentedStart();
    start = symbols.getStart();
    eof = symbols.getEof();
    Rule r1 = new Rule(start, List.of(t));
    Rule r2 = new Rule(start, List.of(start, plus, t));
    Rule r3 = new Rule(t, List.of(f));
    Rule r4 = new Rule(t, List.of(t, times, f));
    Rule r5 = new Rule(f, List.of(n));
    Rule r6 = new Rule(f, List.of(lparen, start, rparen));
    grammar = new Grammar(symbols, List.of(r1, r2, r3, r4, r5, r6));
  }

}
