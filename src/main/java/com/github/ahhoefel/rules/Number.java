package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

public class Number {
  public Symbol number;

  public Number(SymbolTable symbols, CharRange chars, Rule.Builder rules, ShiftReduceResolver resolver) {
    number = symbols.newSymbol("number");
    Symbol positiveNumber = symbols.newSymbol("positiveNumber");
    Rule numToNegative = rules.add(number, chars.hypen, positiveNumber).setAction(ConcatAction.SINGLETON);
    Rule numToPositive = rules.add(number, positiveNumber).setAction(ConcatAction.SINGLETON);
    rules.add(positiveNumber, positiveNumber, chars.number).setAction(ConcatAction.SINGLETON);
    rules.add(positiveNumber, chars.number).setAction(ConcatAction.SINGLETON);
    resolver.addShiftPreference(numToPositive, chars.number);
    resolver.addShiftPreference(numToNegative, chars.number);
  }
}
