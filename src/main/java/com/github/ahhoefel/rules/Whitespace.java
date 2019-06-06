package com.github.ahhoefel.rules;

import com.github.ahhoefel.parser.*;

public class Whitespace {

  public Symbol whitespace;
  public Symbol whitespaceChar;

  public Whitespace(SymbolTable symbols, CharRange chars, Rule.Builder rules, ShiftReduceResolver resolver) {
    whitespace = symbols.newSymbol("whitespace");
    whitespaceChar = symbols.newSymbol("whitespaceChar");
    ConcatAction concat = ConcatAction.SINGLETON;
    rules.add(whitespace, whitespaceChar, whitespace).setAction(concat);
    Rule lastSpace = rules.add(whitespace, whitespaceChar).setAction(concat);
    rules.add(whitespaceChar, chars.space).setAction(concat);
    rules.add(whitespaceChar, chars.tab).setAction(concat);
    rules.add(whitespaceChar, chars.newline).setAction(concat);
    resolver.addShiftPreference(lastSpace, chars.space);
    resolver.addShiftPreference(lastSpace, chars.tab);
    resolver.addShiftPreference(lastSpace, chars.newline);
  }
}
