package com.github.ahhoefel;

import java.util.List;

public class ExampleF {

  Grammar grammar;
  SymbolTable.TerminalTable terminals;
  Symbol digit;
  SymbolTable.NonTerminalTable nonTerminals;
  Symbol start;
  Symbol word;
  Symbol number;

  public ExampleF() {
    terminals = new SymbolTable.TerminalTable();
    digit = terminals.newSymbol("d");
    nonTerminals = new SymbolTable.NonTerminalTable();
    start = nonTerminals.getStart();
    word = nonTerminals.newSymbol("word");
    number = nonTerminals.newSymbol("number");

    Rule r1 = new Rule(start, List.of(number, start));
    Rule r2 = new Rule(start, List.of(number));
    Rule r3 = new Rule(number, List.of(digit, number));
    Rule r4 = new Rule(number, List.of(digit));

    grammar = new Grammar(terminals, nonTerminals, List.of(r1, r2, r3, r4));
  }
}
