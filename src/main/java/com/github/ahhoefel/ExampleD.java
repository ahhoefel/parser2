package com.github.ahhoefel;

import java.util.List;

/**
 * Example D is an unambigous grammer that is not SLR(1).
 * See dragon book example 4.39.
 */
public class ExampleD {

  Grammar grammar;
  SymbolTable.TerminalTable terminals;
  Symbol equal;
  Symbol contents;
  Symbol identifier;
  SymbolTable.NonTerminalTable nonTerminals;
  Symbol start;
  Symbol left;
  Symbol right;

  Rule startToLeftEqualsRight;
  Rule startToRight;
  Rule leftToContentsRight;
  Rule leftToIdentifier;
  Rule rightToLeft;

  public ExampleD() {
    terminals = new SymbolTable.TerminalTable();
    equal = terminals.newSymbol("=");
    contents = terminals.newSymbol("*");
    identifier = terminals.newSymbol("id");
    nonTerminals = new SymbolTable.NonTerminalTable();
    start = nonTerminals.getStart();
    left = nonTerminals.newSymbol("L");
    right = nonTerminals.newSymbol("R");

    startToLeftEqualsRight = new Rule(start, List.of(left, equal, right));
    startToRight = new Rule(start, List.of(right));
    leftToContentsRight = new Rule(left, List.of(contents, right));
    leftToIdentifier = new Rule(left, List.of(identifier));
    rightToLeft = new Rule(right, List.of(left));

    grammar = new Grammar(terminals, nonTerminals, List.of(startToLeftEqualsRight, startToRight, leftToContentsRight, leftToIdentifier, rightToLeft));
  }
}
