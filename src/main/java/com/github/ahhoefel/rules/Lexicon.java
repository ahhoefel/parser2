package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Lexicon {

  private Symbol start;
  private Symbol word;

  private Identifier identifier;
  private Whitespace whitespace;
  private Number number;

  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private CharRange chars;
  private Grammar grammar;
  private LRTable table;

  private SymbolTable.TerminalTable resultSymbols;
  public Symbol identifierTerminal;
  public Symbol whitespaceTerminal;
  public Symbol numberTerminal;
  public Symbol periodTerminal;
  public Symbol lParenTerminal;
  public Symbol rParenTerminal;
  public Symbol commaTerminal;

  public Lexicon() {
    terminals = new SymbolTable.TerminalTable();
    nonTerminals = new SymbolTable.NonTerminalTable();
    chars = new CharRange(terminals);

    resultSymbols = new SymbolTable.TerminalTable();
    identifierTerminal = resultSymbols.newSymbol("identifier");
    whitespaceTerminal = resultSymbols.newSymbol("whitespace");
    numberTerminal = resultSymbols.newSymbol("number");
    periodTerminal = resultSymbols.newSymbol("period");
    lParenTerminal = resultSymbols.newSymbol("lparen");
    rParenTerminal = resultSymbols.newSymbol("rparen");
    commaTerminal = resultSymbols.newSymbol("comma");

    List<Rule> rules = new ArrayList<>();
    this.identifier = new Identifier(nonTerminals, chars, rules);
    this.whitespace = new Whitespace(nonTerminals, chars, rules);
    this.number = new Number(nonTerminals, chars, rules);

    start = nonTerminals.getStart();
    word = nonTerminals.newSymbol("word");
    rules.add(new Rule(start, List.of(word, start), PrependAction.SINGLETON));
    rules.add(new Rule(start, List.of(word), PrependAction.SINGLETON));
    rules.add(new Rule(word, List.of(identifier.identifier), new TokenAction(identifierTerminal)));
    rules.add(new Rule(word, List.of(whitespace.whitespace), new TokenAction(whitespaceTerminal)));
    rules.add(new Rule(word, List.of(number.number), new TokenAction(numberTerminal)));
    rules.add(new Rule(word, List.of(chars.period), new TokenAction(periodTerminal)));
    rules.add(new Rule(word, List.of(chars.lparen), new TokenAction(lParenTerminal)));
    rules.add(new Rule(word, List.of(chars.rparen), new TokenAction(rParenTerminal)));
    rules.add(new Rule(word, List.of(chars.comma), new TokenAction(commaTerminal)));

    grammar = new Grammar(terminals, nonTerminals, rules);
    LRParser parser = LRItem.makeItemGraph(grammar);
    table = parser.getTable(grammar);
  }

  public List<Token> getTokens(Reader r) throws IOException {
    Tokenizer.TokenIterator iter = new Tokenizer.TokenIterator(new RangeTokenizer(chars, terminals.getEof()), r, terminals.getEof());
    return (List<Token>) Parser.parseTokens(table, iter, grammar.getAugmentedStartRule().getSource());
  }

  public SymbolTable.TerminalTable getTerminals() {
    return resultSymbols;
  }
}
