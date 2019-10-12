package com.github.ahhoefel.parser;

import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.io.TokenIterator;

import java.io.IOException;
import java.util.Iterator;

public class CharacterSet {

  public final SymbolTable.TerminalTable symbols;
  public final Symbol letter;
  public final Symbol number;
  public final Symbol hypen;
  public final Symbol space;
  public final Symbol tab;
  public final Symbol lparen;
  public final Symbol rparen;
  public final Symbol lbrace;
  public final Symbol rbrace;
  public final Symbol lbracket;
  public final Symbol rbracket;
  public final Symbol newline;
  public final Symbol period;
  public final Symbol comma;
  public final Symbol colon;
  public final Symbol eq;
  public final Symbol plus;
  public final Symbol times;
  public final Symbol forwardSlash;
  public final Symbol bang;
  public final Symbol ampersand;
  public final Symbol pipe;
  public final Symbol unknown;
  public final Symbol greaterThan;
  public final Symbol lessThan;
  public final Symbol eof;

  public CharacterSet() {
    symbols = new SymbolTable.TerminalTable();
    eof = symbols.getEof();
    letter = symbols.newSymbol("letter");
    number = symbols.newSymbol("number");
    hypen = symbols.newSymbol("hypen");
    space = symbols.newSymbol("space");
    tab = symbols.newSymbol("tab");
    lparen = symbols.newSymbol("lparen");
    rparen = symbols.newSymbol("rparen");
    lbrace = symbols.newSymbol("lbrace");
    rbrace = symbols.newSymbol("rbrace");
    lbracket = symbols.newSymbol("lbracket");
    rbracket = symbols.newSymbol("rbracket");
    newline = symbols.newSymbol("newline");
    period = symbols.newSymbol("period");
    comma = symbols.newSymbol("comma");
    colon = symbols.newSymbol("colon");
    eq = symbols.newSymbol("equals");
    plus = symbols.newSymbol("plus");
    times = symbols.newSymbol("times");
    forwardSlash = symbols.newSymbol("forwardSlash");
    bang = symbols.newSymbol("bang");
    ampersand = symbols.newSymbol("ampersand");
    pipe = symbols.newSymbol("pipe");
    greaterThan = symbols.newSymbol("greaterThan");
    lessThan = symbols.newSymbol("lessThan");
    unknown = symbols.newSymbol("unknown");
  }

  public Iterator<Token> parse(Target target) throws IOException {
    System.out.print("Reading... ");
    RangeTokenizer rangeTokenizer = new RangeTokenizer(this);
    TokenIterator tokens = new TokenIterator(rangeTokenizer, target);
    return tokens;
  }
}
