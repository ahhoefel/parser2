package com.github.ahhoefel;

import java.util.List;

/**
 * Example 4.42
 */
public class ExampleE {

  Grammar grammar;
  SymbolTable.TerminalTable terminals;
  Symbol c;
  Symbol d;
  SymbolTable.NonTerminalTable nonTerminals;
  Symbol start;
  Symbol cee;


  public ExampleE() {
    terminals = new SymbolTable.TerminalTable();
    c = terminals.newSymbol("c");
    d = terminals.newSymbol("d");
    nonTerminals = new SymbolTable.NonTerminalTable();
    start = nonTerminals.getStart();
    cee = nonTerminals.newSymbol("C");

    Rule r1 = new Rule(start, List.of(cee, cee));
    Rule r2 = new Rule(cee, List.of(c, cee));
    Rule r3 = new Rule(cee, List.of(d));

    grammar = new Grammar(terminals, nonTerminals, List.of(r1, r2, r3));
  }
}
