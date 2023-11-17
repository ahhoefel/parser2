package com.github.ahhoefel;

import com.github.ahhoefel.parser.Grammar;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;

import java.util.List;

public class ExampleF {

  Grammar grammar;
  SymbolTable.TerminalTable terminals;
  Symbol digit;
  Symbol separator;
  SymbolTable.NonTerminalTable nonTerminals;
  Symbol start;
  Symbol number;

  public ExampleF() {
    terminals = new SymbolTable.TerminalTable();
    digit = terminals.newSymbol("digit");
    separator = terminals.newSymbol("separator");
    nonTerminals = new SymbolTable.NonTerminalTable();
    start = nonTerminals.getStart();
    number = nonTerminals.newSymbol("number");

    Rule r1 = new Rule(start, List.of(number, separator, start));
    Rule r2 = new Rule(start, List.of(number));
    Rule r3 = new Rule(number, List.of(digit, number));
    Rule r4 = new Rule(number, List.of(digit));

    grammar = new Grammar(terminals, nonTerminals, List.of(r1, r2, r3, r4));
  }
}
