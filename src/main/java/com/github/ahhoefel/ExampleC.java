package com.github.ahhoefel;

import com.github.ahhoefel.parser.*;
import com.github.ahhoefel.rules.Identifier;
import com.github.ahhoefel.rules.Number;
import com.github.ahhoefel.rules.Whitespace;

public class ExampleC {

  SymbolTable.TerminalTable terminals;
  SymbolTable.NonTerminalTable nonTerminals;
  CharacterSet ch;
  Symbol start;
  Symbol statement;
  Number number;
  Identifier identifier;
  Whitespace whitespace;
  Grammar grammar;

  Rule statements;
  Rule noStatements;


  public ExampleC() {
    ch = new CharacterSet();
    terminals = ch.symbols;
    nonTerminals = new SymbolTable.NonTerminalTable();
    statement = nonTerminals.newSymbol("statement");
    Rule.Builder rules = new Rule.Builder();
    ShiftReduceResolver resolver = new ShiftReduceResolver();
    number = new Number(nonTerminals, ch, rules, resolver);
    identifier = new Identifier(nonTerminals, ch, rules, resolver);
    whitespace = new Whitespace(nonTerminals, ch, rules, resolver);

    start = nonTerminals.getStart();
    statements = rules.add(start, statement, start);
    noStatements = rules.add(start);

    rules.add(statement, number.number);
    rules.add(statement, identifier.identifier);
    rules.add(statement, whitespace.whitespace);

    grammar = new Grammar(terminals, nonTerminals, rules.build());
  }
}
