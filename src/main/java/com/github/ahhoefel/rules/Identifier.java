package com.github.ahhoefel.rules;

import com.github.ahhoefel.parser.*;

public class Identifier {
  public Symbol identifier;
  public Symbol identifierTail;
  public Symbol identifierHeadChar;
  public Symbol identifierTailChar;

  public Identifier(SymbolTable symbols, CharacterSet chars, Rule.Builder rules, ShiftReduceResolver resolver) {
    identifier = symbols.newSymbol("identifier");
    identifierTail = symbols.newSymbol("identifierTail");
    identifierHeadChar = symbols.newSymbol("identifierHeadChar");
    identifierTailChar = symbols.newSymbol("identifierTailChar");

    ConcatAction concat = ConcatAction.SINGLETON;
    Rule singleLetterIdentifier = rules.add(identifier, identifierHeadChar).setAction(concat);
    rules.add(identifier, identifierHeadChar, identifierTail).setAction(concat);
    rules.add(identifierTail, identifierTailChar, identifierTail).setAction(concat);
    Rule identifierTailToChar = rules.add(identifierTail, identifierTailChar).setAction(concat);
    rules.add(identifierHeadChar, chars.letter).setAction(concat);
    rules.add(identifierTailChar, chars.letter).setAction(concat);
    rules.add(identifierTailChar, chars.number).setAction(concat);
    rules.add(identifierTailChar, chars.underscore).setAction(concat);

    resolver.addShiftPreference(identifierTailToChar, chars.letter);
    resolver.addShiftPreference(identifierTailToChar, chars.number);
    resolver.addShiftPreference(singleLetterIdentifier, chars.letter);
    resolver.addShiftPreference(singleLetterIdentifier, chars.number);
  }
}
