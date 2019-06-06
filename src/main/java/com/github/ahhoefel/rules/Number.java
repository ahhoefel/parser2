package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

public class Number {
  public Symbol number;

  public Number(SymbolTable symbols, CharRange chars, Rule.Builder rules, ShiftReduceResolver resolver) {
    number = symbols.newSymbol("number");
    rules.add(number, number, chars.number).setAction(ConcatAction.SINGLETON);
    rules.add(number, chars.number).setAction(ConcatAction.SINGLETON);
  }
}
