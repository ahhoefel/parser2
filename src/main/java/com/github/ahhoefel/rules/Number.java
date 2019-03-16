package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.util.List;

public class Number {
  public NonTerminalSymbol number;

  public Number(SymbolTable symbols, CharRange chars, List<Rule> rules) {
    number = symbols.newNonTerminal("number");
    rules.add(new Rule(number, List.of(chars.number, number), ConcatAction.SINGLETON));
    rules.add(new Rule(number, List.of(chars.number), ConcatAction.SINGLETON));
  }
}
