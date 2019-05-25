package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class Lexicon {

  private Symbol start;
  private Symbol word;

  private Identifier identifierGrammar;
  private Whitespace whitespaceGrammar;
  private Number numberGrammar;

  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private CharRange chars;
  private Grammar grammar;
  private LRTable table;

  private SymbolTable.TerminalTable resultSymbols;
  public Symbol identifier;
  public Symbol whitespace;
  public Symbol number;
  public Symbol period;
  public Symbol lParen;
  public Symbol rParen;
  public Symbol lBrace;
  public Symbol rBrace;
  public Symbol comma;
  public Symbol equals;
  public Symbol plus;
  public Symbol times;

  public Symbol forKeyword;
  public Symbol ifKeyword;
  public Symbol funcKeyword;
  public Symbol varKeyword;
  public Symbol intKeyword;
  public Symbol boolKeyword;
  public Symbol stringKeyword;

  public Lexicon() {
    terminals = new SymbolTable.TerminalTable();
    nonTerminals = new SymbolTable.NonTerminalTable();
    chars = new CharRange(terminals);

    resultSymbols = new SymbolTable.TerminalTable();
    identifier = resultSymbols.newSymbol("identifierGrammar");
    whitespace = resultSymbols.newSymbol("whitespaceGrammar");
    number = resultSymbols.newSymbol("numberGrammar");
    period = resultSymbols.newSymbol("period");
    lParen = resultSymbols.newSymbol("lparen");
    rParen = resultSymbols.newSymbol("rparen");
    lBrace = resultSymbols.newSymbol("lbrace");
    rBrace = resultSymbols.newSymbol("rbrace");
    comma = resultSymbols.newSymbol("comma");
    equals = resultSymbols.newSymbol("equals");
    plus = resultSymbols.newSymbol("plus");
    times = resultSymbols.newSymbol("times");

    forKeyword = resultSymbols.newSymbol("for");
    ifKeyword = resultSymbols.newSymbol("if");
    funcKeyword = resultSymbols.newSymbol("func");
    varKeyword = resultSymbols.newSymbol("var");
    intKeyword = resultSymbols.newSymbol("int");
    boolKeyword = resultSymbols.newSymbol("bool");
    stringKeyword = resultSymbols.newSymbol("string");
    List<Symbol> keywords = List.of(forKeyword, ifKeyword, funcKeyword, varKeyword, intKeyword, boolKeyword, stringKeyword);

    Rule.Builder rules = new Rule.Builder();
    ShiftReduceResolver resolver = new ShiftReduceResolver();
    this.identifierGrammar = new Identifier(nonTerminals, chars, rules, resolver);
    this.whitespaceGrammar = new Whitespace(nonTerminals, chars, rules, resolver);
    this.numberGrammar = new Number(nonTerminals, chars, rules, resolver);

    start = nonTerminals.getStart();
    word = nonTerminals.newSymbol("word");
    rules.add(start, word, start).setAction(PrependAction.SINGLETON);
    rules.add(start, word).setAction(PrependAction.SINGLETON);
    rules.add(word, identifierGrammar.identifier).setAction(new TokenAction(identifier, keywords));
    rules.add(word, whitespaceGrammar.whitespace).setAction(x -> null);
    Rule wordIsNumber = rules.add(word, numberGrammar.number).setAction(new TokenAction(number));
    rules.add(word, chars.period).setAction(new TokenAction(period));
    rules.add(word, chars.lparen).setAction(new TokenAction(lParen));
    rules.add(word, chars.rparen).setAction(new TokenAction(rParen));
    rules.add(word, chars.lbrace).setAction(new TokenAction(lBrace));
    rules.add(word, chars.rbrace).setAction(new TokenAction(rBrace));
    rules.add(word, chars.comma).setAction(new TokenAction(comma));
    rules.add(word, chars.eq).setAction(new TokenAction(equals));
    rules.add(word, chars.times).setAction(new TokenAction(times));
    rules.add(word, chars.plus).setAction(new TokenAction(plus));

    resolver.addShiftPreference(wordIsNumber, chars.number);

    grammar = new Grammar(terminals, nonTerminals, rules.build());
    table = LRParser.getCannonicalLRTable(grammar, resolver);
  }

  public List<Token> getTokens(Reader r) throws IOException {
    Tokenizer.TokenIterator iter = new Tokenizer.TokenIterator(new RangeTokenizer(chars, terminals.getEof()), r, terminals.getEof());
    return (List<Token>) Parser.parseTokens(table, iter, grammar.getAugmentedStartRule().getSource());
  }

  public SymbolTable.TerminalTable getTerminals() {
    return resultSymbols;
  }
}
