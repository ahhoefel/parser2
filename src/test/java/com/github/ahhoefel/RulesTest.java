package com.github.ahhoefel;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class RulesTest {

  @Test
  public void testIsEpsilon() {
    SymbolTable symbols = new SymbolTable();
    NonTerminalSymbol a = symbols.newNonTerminal("A");
    NonTerminalSymbol b = symbols.newNonTerminal("B");
    TerminalSymbol x = symbols.newTerminal("x");
    TerminalSymbol y = symbols.newTerminal("y");
    TerminalSymbol z = symbols.newTerminal("z");
    NonTerminalSymbol s = symbols.getStart();
    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Rules rules = new Rules(symbols, rs);
    Assert.assertFalse(rules.isEpsilon(s));
    Assert.assertTrue(rules.isEpsilon(a));
    Assert.assertFalse(rules.isEpsilon(b));
  }

  @Test
  public void testFirsts() {
    SymbolTable symbols = new SymbolTable();
    NonTerminalSymbol a = symbols.newNonTerminal("A");
    NonTerminalSymbol b = symbols.newNonTerminal("B");
    TerminalSymbol x = symbols.newTerminal("x");
    TerminalSymbol y = symbols.newTerminal("y");
    TerminalSymbol z = symbols.newTerminal("z");
    NonTerminalSymbol s = symbols.getStart();
    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Rules rules = new Rules(symbols, rs);

    Assert.assertEquals(Set.of(), rules.getSimpleFirstTerminals(s));
    Assert.assertEquals(Set.of(x), rules.getSimpleFirstTerminals(a));
    Assert.assertEquals(Set.of(y, z), rules.getSimpleFirstTerminals(b));

    Assert.assertEquals(Set.of(a, b), rules.getSimpleFirstNonTerminals(s));
    Assert.assertEquals(Set.of(), rules.getSimpleFirstNonTerminals(a));
    Assert.assertEquals(Set.of(), rules.getSimpleFirstNonTerminals(b));

    Assert.assertEquals(Set.of(s, a, b), rules.getFirstNonTerminals(s));
    Assert.assertEquals(Set.of(a), rules.getFirstNonTerminals(a));
    Assert.assertEquals(Set.of(b), rules.getFirstNonTerminals(b));

    Assert.assertEquals(Set.of(x, y, z), rules.getFirstTerminals(s));
    Assert.assertEquals(Set.of(x), rules.getFirstTerminals(a));
    Assert.assertEquals(Set.of(y, z), rules.getFirstTerminals(b));
  }
}