package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.*;

public class Number {
  public Symbol number;

  public Number(SymbolTable symbols, CharacterSet chars, Rule.Builder rules, ShiftReduceResolver resolver) {
    number = symbols.newSymbol("number");
    rules.add(number, number, chars.number).setAction(ConcatAction.SINGLETON);
    rules.add(number, chars.number).setAction(ConcatAction.SINGLETON);
  }
}
