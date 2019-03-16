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

  SymbolTable symbols;
  CharRange ch;
  NonTerminalSymbol start;
  NonTerminalSymbol statement;
  Number number;
  Identifier identifier;
  Whitespace whitespace;
  Grammar grammar;

  Rule statements;
  Rule noStatements;


  public ExampleC() {
    symbols = new SymbolTable();
    ch = new CharRange(symbols);
    statement = symbols.newNonTerminal("statement");
    List<Rule> rules = new ArrayList<>();
    number = new Number(symbols, ch, rules);
    identifier = new Identifier(symbols, ch, rules);
    whitespace = new Whitespace(symbols, ch, rules);

    start = symbols.getStart();
    statements = new Rule(start, List.<Symbol>of(statement, start));
    noStatements = new Rule(start, List.<Symbol>of());
    rules.add(statements);
    rules.add(noStatements);

    rules.add(new Rule(statement, List.<Symbol>of(number.number)));
    rules.add(new Rule(statement, List.<Symbol>of(identifier.identifier)));
    rules.add(new Rule(statement, List.<Symbol>of(whitespace.whitespace)));

    grammar = new Grammar(symbols, rules);
  }

  public static void main(String[] args) throws IOException {
    ExampleC c = new ExampleC();
    LRParser parser = LRItem.makeItemGraph(c.grammar);
    Reader r = new CharArrayReader("foc 123 d12".toCharArray());

    Tokenizer.TokenIterator tokens = new Tokenizer.TokenIterator(new RangeTokenizer(c.ch, c.symbols.getEof()), r, c.symbols.getEof());

    LRTable table = parser.getTable(c.grammar);
    System.out.println(table);
    Object tree = Parser.parseTokens(parser.getTable(c.grammar), tokens, c.grammar.getAugmentedStartRule().getSource());
    System.out.println(tree);
  }
}
