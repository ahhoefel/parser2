package com.github.ahhoefel;

public class CharRange {

  public final TerminalSymbol letter;
  public final TerminalSymbol number;
  public final TerminalSymbol hypen;
  public final TerminalSymbol space;
  public final TerminalSymbol lparen;
  public final TerminalSymbol rparen;
  public final TerminalSymbol newline;
  public final TerminalSymbol unknown;

  public CharRange(SymbolFactory.Builder sf) {
    letter = sf.newTerminal("letter");
    number = sf.newTerminal("number");
    hypen = sf.newTerminal("hypen");
    space = sf.newTerminal("space");
    lparen = sf.newTerminal("lparen");
    rparen = sf.newTerminal("rparen");
    newline = sf.newTerminal("newline");
    unknown = sf.newTerminal("unknown");
  }
}
