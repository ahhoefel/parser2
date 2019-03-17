package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.util.List;

public class Identifier {
  public Symbol identifier;
  public Symbol identifierTail;
  public Symbol identifierHeadChar;
  public Symbol identifierTailChar;

  public Identifier(SymbolTable symbols, CharRange chars, List<Rule> rules) {
    identifier = symbols.newSymbol("identifier");
    identifierTail = symbols.newSymbol("identifierTail");
    identifierHeadChar = symbols.newSymbol("identifierHeadChar");
    identifierTailChar = symbols.newSymbol("identifierTailChar");

    ConcatAction concat = ConcatAction.SINGLETON;
    rules.add(new Rule(identifier, List.of(identifierHeadChar, identifierTail), concat));
    rules.add(new Rule(identifierTail, List.of(identifierTailChar, identifierTail), concat));
    rules.add(new Rule(identifierTail, List.of(identifierTailChar), concat));
    rules.add(new Rule(identifierHeadChar, List.of(chars.letter), concat));
    rules.add(new Rule(identifierTailChar, List.of(chars.letter), concat));
    rules.add(new Rule(identifierTailChar, List.of(chars.number), concat));
  }
}
