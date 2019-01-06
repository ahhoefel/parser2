package com.github.ahhoefel;

import java.util.List;

public class ExampleB {

  Grammar grammar;
  NonTerminalSymbol t;
  NonTerminalSymbol f;
  TerminalSymbol plus;
  TerminalSymbol times;
  TerminalSymbol n;
  TerminalSymbol lparen;
  TerminalSymbol rparen;
  SymbolTable symbols;
  NonTerminalSymbol startp;
  NonTerminalSymbol start;
  TerminalSymbol eof;

  public ExampleB() {
    symbols = new SymbolTable();
    t = symbols.newNonTerminal("T");
    f = symbols.newNonTerminal("F");
    plus = symbols.newTerminal("+");
    times = symbols.newTerminal("*");
    n = symbols.newTerminal("n");
    lparen = symbols.newTerminal("(");
    rparen = symbols.newTerminal(")");
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
