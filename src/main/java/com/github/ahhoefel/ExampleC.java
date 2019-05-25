package com.github.ahhoefel;

import com.github.ahhoefel.rules.Identifier;
import com.github.ahhoefel.rules.Number;
import com.github.ahhoefel.rules.Whitespace;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

public class ExampleC {

  SymbolTable.TerminalTable terminals;
  SymbolTable.NonTerminalTable nonTerminals;
  CharRange ch;
  Symbol start;
  Symbol statement;
  Number number;
  Identifier identifier;
  Whitespace whitespace;
  Grammar grammar;

  Rule statements;
  Rule noStatements;


  public ExampleC() {
    terminals = new SymbolTable.TerminalTable();
    nonTerminals = new SymbolTable.NonTerminalTable();
    ch = new CharRange(terminals);
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

  public static void main(String[] args) throws IOException {
    ExampleC c = new ExampleC();
    LRTable table = LRParser.getSLRTable(c.grammar);
    Reader r = new CharArrayReader("foc 123 d12".toCharArray());

    Tokenizer.TokenIterator tokens = new Tokenizer.TokenIterator(new RangeTokenizer(c.ch, c.terminals.getEof()), r, c.terminals.getEof());

    System.out.println(table);
    Object tree = Parser.parseTokens(table, tokens, c.grammar.getAugmentedStartRule().getSource());
    System.out.println(tree);
  }
}
