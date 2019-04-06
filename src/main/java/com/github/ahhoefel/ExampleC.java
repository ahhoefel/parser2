package com.github.ahhoefel;

import com.github.ahhoefel.rules.Identifier;
import com.github.ahhoefel.rules.Number;
import com.github.ahhoefel.rules.Whitespace;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
    List<Rule> rules = new ArrayList<>();
    number = new Number(nonTerminals, ch, rules);
    identifier = new Identifier(nonTerminals, ch, rules);
    whitespace = new Whitespace(nonTerminals, ch, rules);

    start = nonTerminals.getStart();
    statements = new Rule(start, List.<Symbol>of(statement, start));
    noStatements = new Rule(start, List.<Symbol>of());
    rules.add(statements);
    rules.add(noStatements);

    rules.add(new Rule(statement, List.<Symbol>of(number.number)));
    rules.add(new Rule(statement, List.<Symbol>of(identifier.identifier)));
    rules.add(new Rule(statement, List.<Symbol>of(whitespace.whitespace)));

    grammar = new Grammar(terminals, nonTerminals, rules);
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
