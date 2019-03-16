package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.util.List;

public class Whitespace {

  public NonTerminalSymbol whitespace;
  public NonTerminalSymbol whitespaceChar;

  public Whitespace(SymbolTable symbols, CharRange chars, List<Rule> rules) {
    whitespace = symbols.newNonTerminal("whitespace");
    whitespaceChar = symbols.newNonTerminal("whitespaceChar");
    ConcatAction concat = ConcatAction.SINGLETON;
    rules.add(new Rule(whitespace, List.of(whitespaceChar, whitespace), concat));
    rules.add(new Rule(whitespace, List.of(whitespaceChar), concat));
    rules.add(new Rule(whitespaceChar, List.of(chars.space), concat));
    rules.add(new Rule(whitespaceChar, List.of(chars.tab), concat));
  }
}
