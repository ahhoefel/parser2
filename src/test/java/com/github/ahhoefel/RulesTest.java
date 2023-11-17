package com.github.ahhoefel;

import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Rules;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


import java.util.List;
import java.util.Set;

public class RulesTest {

  @Test
  public void testIsEpsilon() {
    SymbolTable.NonTerminalTable nonTerminals = new SymbolTable.NonTerminalTable();
    Symbol s = nonTerminals.getStart();
    Symbol a = nonTerminals.newSymbol("A");
    Symbol b = nonTerminals.newSymbol("B");

    SymbolTable.TerminalTable terminals = new SymbolTable.TerminalTable();
    Symbol x = terminals.newSymbol("x");
    Symbol y = terminals.newSymbol("y");
    Symbol z = terminals.newSymbol("z");

    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Rules rules = new Rules(terminals, nonTerminals, rs);
    Assertions.assertFalse(rules.isEpsilon(s));
    Assertions.assertTrue(rules.isEpsilon(a));
    Assertions.assertFalse(rules.isEpsilon(b));
  }

  @Test
  public void testFirsts() {
    SymbolTable.NonTerminalTable nonTerminals = new SymbolTable.NonTerminalTable();
    Symbol s = nonTerminals.getStart();
    Symbol a = nonTerminals.newSymbol("A");
    Symbol b = nonTerminals.newSymbol("B");

    SymbolTable.TerminalTable terminals = new SymbolTable.TerminalTable();
    Symbol x = terminals.newSymbol("x");
    Symbol y = terminals.newSymbol("y");
    Symbol z = terminals.newSymbol("z");
    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Rules rules = new Rules(terminals, nonTerminals, rs);

    Assertions.assertEquals(Set.of(), rules.getSimpleFirstTerminals(s));
    Assertions.assertEquals(Set.of(x), rules.getSimpleFirstTerminals(a));
    Assertions.assertEquals(Set.of(y, z), rules.getSimpleFirstTerminals(b));

    Assertions.assertEquals(Set.of(a, b), rules.getSimpleFirstNonTerminals(s));
    Assertions.assertEquals(Set.of(), rules.getSimpleFirstNonTerminals(a));
    Assertions.assertEquals(Set.of(), rules.getSimpleFirstNonTerminals(b));

    Assertions.assertEquals(Set.of(s, a, b), rules.getFirstNonTerminals(s));
    Assertions.assertEquals(Set.of(a), rules.getFirstNonTerminals(a));
    Assertions.assertEquals(Set.of(b), rules.getFirstNonTerminals(b));

    Assertions.assertEquals(Set.of(x, y, z), rules.getFirstTerminals(s));
    Assertions.assertEquals(Set.of(x), rules.getFirstTerminals(a));
    Assertions.assertEquals(Set.of(y, z), rules.getFirstTerminals(b));
  }
}