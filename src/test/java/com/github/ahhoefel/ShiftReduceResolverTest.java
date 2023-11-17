package com.github.ahhoefel;

import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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

    Assertions.assertEquals(resolver.getPreference(r, t).get(), ShiftReduceResolver.Preference.SHIFT);

  }


}
