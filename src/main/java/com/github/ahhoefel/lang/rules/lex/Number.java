package com.github.ahhoefel.lang.rules.lex;

import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class Number implements LanguageComponent {
  @Override
  public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
    Symbol number = provider.createAndExport("number");
    Symbol digit = provider.requireTerminal("number");
    rules.emit(number, number, digit).setAction(ConcatAction.SINGLETON);
    rules.emit(number, digit).setAction(ConcatAction.SINGLETON);
  }
}
