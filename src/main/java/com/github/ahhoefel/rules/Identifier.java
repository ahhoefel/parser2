package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.util.List;

public class Identifier {
  public NonTerminalSymbol identifier;
  public NonTerminalSymbol identifierTail;
  public NonTerminalSymbol identifierHeadChar;
  public NonTerminalSymbol identifierTailChar;

  public Identifier(SymbolTable symbols, CharRange chars, List<Rule> rules) {
    identifier = symbols.newNonTerminal("identifier");
    identifierTail = symbols.newNonTerminal("identifierTail");
    identifierHeadChar = symbols.newNonTerminal("identifierHeadChar");
    identifierTailChar = symbols.newNonTerminal("identifierTailChar");

    ConcatAction concat = ConcatAction.SINGLETON;
    rules.add(new Rule(identifier, List.of(identifierHeadChar, identifierTail), concat));
    rules.add(new Rule(identifierTail, List.of(identifierTailChar, identifierTail), concat));
    rules.add(new Rule(identifierTail, List.of(identifierTailChar), concat));
    rules.add(new Rule(identifierHeadChar, List.of(chars.letter), concat));
    rules.add(new Rule(identifierTailChar, List.of(chars.letter), concat));
  }
}
