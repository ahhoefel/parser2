package com.github.ahhoefel;

public class CharRange {

  public final Symbol letter;
  public final Symbol number;
  public final Symbol hypen;
  public final Symbol space;
  public final Symbol tab;
  public final Symbol lparen;
  public final Symbol rparen;
  public final Symbol lbrace;
  public final Symbol rbrace;
  public final Symbol newline;
  public final Symbol period;
  public final Symbol comma;
  public final Symbol eq;
  public final Symbol plus;
  public final Symbol times;
  public final Symbol forwardSlash;
  public final Symbol unknown;

  public CharRange(SymbolTable symbols) {
    letter = symbols.newSymbol("letter");
    number = symbols.newSymbol("number");
    hypen = symbols.newSymbol("hypen");
    space = symbols.newSymbol("space");
    tab = symbols.newSymbol("tab");
    lparen = symbols.newSymbol("lparen");
    rparen = symbols.newSymbol("rparen");
    lbrace = symbols.newSymbol("lbrace");
    rbrace = symbols.newSymbol("rbrace");
    newline = symbols.newSymbol("newline");
    period = symbols.newSymbol("period");
    comma = symbols.newSymbol("comma");
    eq = symbols.newSymbol("equals");
    plus = symbols.newSymbol("plus");
    times = symbols.newSymbol("times");
    forwardSlash = symbols.newSymbol("forwardSlash");
    unknown = symbols.newSymbol("unknown");
  }
}
