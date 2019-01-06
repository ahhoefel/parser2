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

  public CharRange(SymbolTable symbols) {
    letter = symbols.newTerminal("letter");
    number = symbols.newTerminal("number");
    hypen = symbols.newTerminal("hypen");
    space = symbols.newTerminal("space");
    lparen = symbols.newTerminal("lparen");
    rparen = symbols.newTerminal("rparen");
    newline = symbols.newTerminal("newline");
    unknown = symbols.newTerminal("unknown");
  }
}
