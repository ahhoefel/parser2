package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Lexicon {

  private NonTerminalSymbol start;
  private NonTerminalSymbol word;

  private Identifier identifier;
  private Whitespace whitespace;
  private Number number;

  private SymbolTable symbols;
  private CharRange chars;
  private Grammar grammar;
  private LRTable table;

  private SymbolTable resultSymbols;
  public TerminalSymbol identifierTerminal;
  public TerminalSymbol whitespaceTerminal;
  public TerminalSymbol numberTerminal;
  public TerminalSymbol periodTerminal;
  public TerminalSymbol lParenTerminal;
  public TerminalSymbol rParenTerminal;
  public TerminalSymbol commaTerminal;

  public Lexicon() {
    symbols = new SymbolTable();
    chars = new CharRange(symbols);

    resultSymbols = new SymbolTable();
    identifierTerminal = resultSymbols.newTerminal("identifier");
    whitespaceTerminal = resultSymbols.newTerminal("whitespace");
    numberTerminal = resultSymbols.newTerminal("number");
    periodTerminal = resultSymbols.newTerminal("period");
    lParenTerminal = resultSymbols.newTerminal("lparen");
    rParenTerminal = resultSymbols.newTerminal("rparen");
    commaTerminal = resultSymbols.newTerminal("comma");

    List<Rule> rules = new ArrayList<>();
    this.identifier = new Identifier(symbols, chars, rules);
    this.whitespace = new Whitespace(symbols, chars, rules);
    this.number = new Number(symbols, chars, rules);

    start = symbols.getStart();
    word = symbols.newNonTerminal("word");
    rules.add(new Rule(start, List.of(word, start), PrependAction.SINGLETON));
    rules.add(new Rule(start, List.of(word), PrependAction.SINGLETON));
    rules.add(new Rule(word, List.of(identifier.identifier), new TokenAction(identifierTerminal)));
    rules.add(new Rule(word, List.of(whitespace.whitespace), new TokenAction(whitespaceTerminal)));
    rules.add(new Rule(word, List.of(number.number), new TokenAction(numberTerminal)));
    rules.add(new Rule(word, List.of(chars.period), new TokenAction(periodTerminal)));
    rules.add(new Rule(word, List.of(chars.lparen), new TokenAction(lParenTerminal)));
    rules.add(new Rule(word, List.of(chars.rparen), new TokenAction(rParenTerminal)));
    rules.add(new Rule(word, List.of(chars.comma), new TokenAction(commaTerminal)));

    grammar = new Grammar(symbols, rules);
    LRParser parser = LRItem.makeItemGraph(grammar);
    table = parser.getTable(grammar);
  }

  public List<Token> getTokens(Reader r) throws IOException {
    Tokenizer.TokenIterator iter = new Tokenizer.TokenIterator(new RangeTokenizer(chars, symbols.getEof()), r, symbols.getEof());
    return (List<Token>) Parser.parseTokens(table, iter, grammar.getAugmentedStartRule().getSource());
  }

  public SymbolTable getSymbols() {
    return resultSymbols;
  }
}
