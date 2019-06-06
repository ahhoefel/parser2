package com.github.ahhoefel;

import com.github.ahhoefel.parser.Grammar;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;

import java.util.List;

public class ExampleB {

  Grammar grammar;
  SymbolTable.TerminalTable terminals;
  Symbol plus;
  Symbol times;
  Symbol n;
  Symbol lparen;
  Symbol rparen;
  Symbol startp;
  Symbol eof;
  SymbolTable.NonTerminalTable nonTerminals;
  Symbol t;
  Symbol f;
  Symbol start;

  public ExampleB() {
    terminals = new SymbolTable.TerminalTable();
    plus = terminals.newSymbol("+");
    times = terminals.newSymbol("*");
    n = terminals.newSymbol("n");
    lparen = terminals.newSymbol("(");
    rparen = terminals.newSymbol(")");
    eof = terminals.getEof();

    nonTerminals = new SymbolTable.NonTerminalTable();
    startp = nonTerminals.getAugmentedStart();
    start = nonTerminals.getStart();
    t = nonTerminals.newSymbol("T");
    f = nonTerminals.newSymbol("F");

    Rule r1 = new Rule(start, List.of(t));
    Rule r2 = new Rule(start, List.of(start, plus, t));
    Rule r3 = new Rule(t, List.of(f));
    Rule r4 = new Rule(t, List.of(t, times, f));
    Rule r5 = new Rule(f, List.of(n));
    Rule r6 = new Rule(f, List.of(lparen, start, rparen));
    grammar = new Grammar(terminals, nonTerminals, List.of(r1, r2, r3, r4, r5, r6));
  }

}
