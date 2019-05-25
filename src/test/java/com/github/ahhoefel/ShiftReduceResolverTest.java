package com.github.ahhoefel;

import org.junit.Assert;
import org.junit.Test;

public class ShiftReduceResolverTest {

  @Test
  public void basicTest() {
    SymbolTable.TerminalTable terminals = new SymbolTable.TerminalTable();
    SymbolTable.NonTerminalTable nonTerminals = new SymbolTable.NonTerminalTable();
    Symbol t = terminals.newSymbol("t");
    Symbol nt = nonTerminals.newSymbol("T");
    Rule.Builder rules = new Rule.Builder();
    Rule r = rules.add(nt, t);
    ShiftReduceResolver resolver = new ShiftReduceResolver();
    resolver.addShiftPreference(r, t);

    Assert.assertEquals(resolver.getPreference(r, t).get(), ShiftReduceResolver.Preference.SHIFT);

  }


}
