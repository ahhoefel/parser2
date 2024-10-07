package com.github.ahhoefel.lang.rules.lex;

import com.github.ahhoefel.parser.*;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class Whitespace implements LanguageComponent {
  @Override
  public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
    Symbol whitespace = provider.createAndExport("whitespace");
    Symbol whitespaceChar = provider.create("whitespaceChar");
    Symbol space = provider.requireTerminal("space");
    Symbol tab = provider.requireTerminal("tab");
    Symbol newline = provider.requireTerminal("newline");

    ConcatAction concat = ConcatAction.SINGLETON;
    rules.emit(whitespace, whitespaceChar, whitespace).setAction(concat);
    Rule lastSpace = rules.emit(whitespace, whitespaceChar).setAction(concat);
    rules.emit(whitespaceChar, space).setAction(concat);
    rules.emit(whitespaceChar, tab).setAction(concat);
    rules.emit(whitespaceChar, newline).setAction(concat);
    resolver.addShiftPreference(lastSpace, space);
    resolver.addShiftPreference(lastSpace, tab);
    resolver.addShiftPreference(lastSpace, newline);
  }
}
