package com.github.ahhoefel.lang.rules.lex;

import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class Identifier implements LanguageComponent {
  @Override
  public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
    Symbol identifier = provider.createAndExport("identifier");
    Symbol identifierTail = provider.create("identifierTail");
    Symbol identifierHeadChar = provider.create("identifierHeadChar");
    Symbol identifierTailChar = provider.create("identifierTailChar");

    Symbol letter = provider.requireTerminal("letter");
    Symbol number = provider.requireTerminal("number");
    Symbol underscore = provider.requireTerminal("underscore");

    ConcatAction concat = ConcatAction.SINGLETON;
    Rule singleLetterIdentifier = rules.emit(identifier, identifierHeadChar).setAction(concat);
    rules.emit(identifier, identifierHeadChar, identifierTail).setAction(concat);
    rules.emit(identifierTail, identifierTailChar, identifierTail).setAction(concat);
    Rule identifierTailToChar = rules.emit(identifierTail, identifierTailChar).setAction(concat);
    rules.emit(identifierHeadChar, letter).setAction(concat);
    rules.emit(identifierTailChar, letter).setAction(concat);
    rules.emit(identifierTailChar, number).setAction(concat);
    rules.emit(identifierTailChar, underscore).setAction(concat);

    resolver.addShiftPreference(identifierTailToChar, letter);
    resolver.addShiftPreference(identifierTailToChar, number);
    resolver.addShiftPreference(singleLetterIdentifier, letter);
    resolver.addShiftPreference(singleLetterIdentifier, number);
  }
}
