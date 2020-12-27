package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.parser.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lexicon {

  private Symbol start;
  private Symbol word;

  private Identifier identifierGrammar;
  private Whitespace whitespaceGrammar;
  private Number numberGrammar;

  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private CharacterSet chars;
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
  public Symbol lBracket;
  public Symbol rBracket;
  public Symbol comma;
  public Symbol colon;
  public Symbol equals;
  public Symbol plus;
  public Symbol times;
  public Symbol forwardSlash;
  public Symbol hyphen;
  public Symbol bang;
  public Symbol doubleAmpersand;
  public Symbol doublePipe;
  public Symbol greaterThan;
  public Symbol greaterThanOrEqual;
  public Symbol lessThan;
  public Symbol lessThanOrEqual;
  public Symbol doubleEquals;
  public Symbol notEqual;

  public Symbol forKeyword;
  public Symbol ifKeyword;
  public Symbol funcKeyword;
  public Symbol varKeyword;
  public Symbol intKeyword;
  public Symbol boolKeyword;
  public Symbol stringKeyword;
  public Symbol returnKeyword;
  public Symbol importKeyword;
  public Symbol typeKeyword;
  public Symbol structKeyword;
  public Symbol unionKeyword;
  public Symbol newKeyword;
  public Symbol trueKeyword;
  public Symbol falseKeyword;

  @SuppressWarnings("unchecked")
  public Lexicon() {
    chars = new CharacterSet();
    terminals = chars.symbols;
    nonTerminals = new SymbolTable.NonTerminalTable();

    resultSymbols = new SymbolTable.TerminalTable();
    identifier = resultSymbols.newSymbol("identifierGrammar");
    whitespace = resultSymbols.newSymbol("whitespaceGrammar");
    number = resultSymbols.newSymbol("numberGrammar");
    period = resultSymbols.newSymbol("period");
    lParen = resultSymbols.newSymbol("lparen");
    rParen = resultSymbols.newSymbol("rparen");
    lBrace = resultSymbols.newSymbol("lbrace");
    rBrace = resultSymbols.newSymbol("rbrace");
    rBracket = resultSymbols.newSymbol("rbracket");
    lBracket = resultSymbols.newSymbol("lbracket");
    comma = resultSymbols.newSymbol("comma");
    colon = resultSymbols.newSymbol("colon");
    equals = resultSymbols.newSymbol("equals");
    plus = resultSymbols.newSymbol("plus");
    times = resultSymbols.newSymbol("times");
    hyphen = resultSymbols.newSymbol("hyphen");
    forwardSlash = resultSymbols.newSymbol("forwardSlash");
    bang = resultSymbols.newSymbol("bang");
    doubleAmpersand = resultSymbols.newSymbol("doubleAmpersand");
    doublePipe = resultSymbols.newSymbol("doublePipe");
    greaterThan = resultSymbols.newSymbol("greaterThan");
    greaterThanOrEqual = resultSymbols.newSymbol("greaterThanOrEqual");
    lessThan = resultSymbols.newSymbol("lessThan");
    lessThanOrEqual = resultSymbols.newSymbol("lessThanOrEqual");
    doubleEquals = resultSymbols.newSymbol("doubleEquals");
    notEqual = resultSymbols.newSymbol("notEqual");

    forKeyword = resultSymbols.newSymbol("for");
    ifKeyword = resultSymbols.newSymbol("if");
    funcKeyword = resultSymbols.newSymbol("func");
    varKeyword = resultSymbols.newSymbol("var");
    intKeyword = resultSymbols.newSymbol("int");
    boolKeyword = resultSymbols.newSymbol("bool");
    stringKeyword = resultSymbols.newSymbol("string");
    returnKeyword = resultSymbols.newSymbol("return");
    importKeyword = resultSymbols.newSymbol("import");
    typeKeyword = resultSymbols.newSymbol("type");
    structKeyword = resultSymbols.newSymbol("struct");
    unionKeyword = resultSymbols.newSymbol("union");
    newKeyword = resultSymbols.newSymbol("new");
    trueKeyword = resultSymbols.newSymbol("true");
    falseKeyword = resultSymbols.newSymbol("false");
    List<Symbol> keywords = List.of(forKeyword, ifKeyword, funcKeyword, varKeyword, intKeyword, boolKeyword,
        stringKeyword, returnKeyword, importKeyword, typeKeyword, structKeyword, unionKeyword, newKeyword, trueKeyword,
        falseKeyword);

    Rule.Builder rules = new Rule.Builder();
    ShiftReduceResolver resolver = new ShiftReduceResolver();
    this.identifierGrammar = new Identifier(nonTerminals, chars, rules, resolver);
    this.whitespaceGrammar = new Whitespace(nonTerminals, chars, rules, resolver);
    this.numberGrammar = new Number(nonTerminals, chars, rules, resolver);

    start = nonTerminals.getStart();
    word = nonTerminals.newSymbol("word");
    rules.add(start).setAction(e -> new ArrayList<Token>());
    rules.add(start, start, word).setAction(e -> {
      List<Token> words = (List<Token>) e[0];
      if (e[1] != null) {
        words.add((Token) e[1]);
      }
      return words;
    });
    rules.add(word, identifierGrammar.identifier).setAction(new TokenAction(identifier, keywords));
    rules.add(word, whitespaceGrammar.whitespace).setAction(e -> null);
    Rule wordIsNumber = rules.add(word, numberGrammar.number).setAction(new TokenAction(number));
    rules.add(word, chars.period).setAction(new TokenAction(period));
    rules.add(word, chars.lparen).setAction(new TokenAction(lParen));
    rules.add(word, chars.rparen).setAction(new TokenAction(rParen));
    rules.add(word, chars.lbrace).setAction(new TokenAction(lBrace));
    rules.add(word, chars.rbrace).setAction(new TokenAction(rBrace));
    rules.add(word, chars.lbracket).setAction(new TokenAction(lBracket));
    rules.add(word, chars.rbracket).setAction(new TokenAction(rBracket));
    rules.add(word, chars.comma).setAction(new TokenAction(comma));
    rules.add(word, chars.colon).setAction(new TokenAction(colon));
    Rule wordIsSingleEqual = rules.add(word, chars.eq).setAction(new TokenAction(equals));
    rules.add(word, chars.times).setAction(new TokenAction(times));
    rules.add(word, chars.plus).setAction(new TokenAction(plus));
    Rule wordIsHyphen = rules.add(word, chars.hypen).setAction(new TokenAction(hyphen));
    rules.add(word, chars.forwardSlash).setAction(new TokenAction(forwardSlash));
    Rule wordIsBang = rules.add(word, chars.bang).setAction(new TokenAction(bang));
    rules.add(word, chars.ampersand, chars.ampersand).setAction(new TokenAction(doubleAmpersand));
    rules.add(word, chars.pipe, chars.pipe).setAction(new TokenAction(doublePipe));
    Rule wordIsGreaterThan = rules.add(word, chars.greaterThan).setAction(new TokenAction(greaterThan));
    rules.add(word, chars.greaterThan, chars.eq).setAction(new TokenAction(greaterThan));
    Rule wordIsLessThan = rules.add(word, chars.lessThan).setAction(new TokenAction(lessThan));
    rules.add(word, chars.lessThan, chars.eq).setAction(new TokenAction(lessThanOrEqual));
    rules.add(word, chars.eq, chars.eq).setAction(new TokenAction(doubleEquals));
    rules.add(word, chars.bang, chars.eq).setAction(new TokenAction(notEqual));

    resolver.addShiftPreference(wordIsNumber, chars.number);
    resolver.addShiftPreference(wordIsHyphen, chars.number);
    resolver.addShiftPreference(wordIsGreaterThan, chars.eq);
    resolver.addShiftPreference(wordIsLessThan, chars.eq);
    resolver.addShiftPreference(wordIsSingleEqual, chars.eq);
    resolver.addShiftPreference(wordIsBang, chars.eq);

    grammar = new Grammar(terminals, nonTerminals, rules.build());
    table = LRParser.getCanonicalLRTable(grammar, resolver);
  }

  @SuppressWarnings("unchecked")
  public List<Token> parse(Target target, ErrorLog log) throws IOException {
    Iterator<Token> tokens = chars.parse(target);
    System.out.print("Lexing... ");
    return (List<Token>) Parser.parseTokens(table, tokens, grammar.getAugmentedStartRule().getSource(), log);
  }

  public SymbolTable.TerminalTable getTerminals() {
    return resultSymbols;
  }
}
