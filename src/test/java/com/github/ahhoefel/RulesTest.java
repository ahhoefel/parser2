package com.github.ahhoefel;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class RulesTest {

  @Test
  public void testIsEpsilon() {
    NonTerminalSymbol s = new NonTerminalSymbol("S");
    NonTerminalSymbol a = new NonTerminalSymbol("A");
    NonTerminalSymbol b = new NonTerminalSymbol("B");
    TerminalSymbol<String> x = new TerminalSymbol<>("x");
    TerminalSymbol<String> y = new TerminalSymbol<>("y");
    TerminalSymbol<String> z = new TerminalSymbol<>("z");
    TerminalSymbol<String> eof = new TerminalSymbol<>("eof");
    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Rules rules = new Rules(rs, eof);
    Assert.assertFalse(rules.isEpsilon(s));
    Assert.assertTrue(rules.isEpsilon(a));
    Assert.assertFalse(rules.isEpsilon(b));
  }

  @Test
  public void testFirsts() {
    NonTerminalSymbol start = new NonTerminalSymbol("start");
    NonTerminalSymbol a = new NonTerminalSymbol("A");
    NonTerminalSymbol b = new NonTerminalSymbol("B");
    TerminalSymbol<String> x = new TerminalSymbol<>("x");
    TerminalSymbol<String> y = new TerminalSymbol<>("y");
    TerminalSymbol<String> z = new TerminalSymbol<>("z");
    TerminalSymbol<String> eof = new TerminalSymbol<>("eof");
    Rule r0 = new Rule(start, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Rules rules = new Rules(rs, eof);

    Assert.assertEquals(Set.of(), rules.getSimpleFirstTerminals(start));
    Assert.assertEquals(Set.of(x), rules.getSimpleFirstTerminals(a));
    Assert.assertEquals(Set.of(y, z), rules.getSimpleFirstTerminals(b));

    Assert.assertEquals(Set.of(a, b), rules.getSimpleFirstNonTerminals(start));
    Assert.assertEquals(Set.of(), rules.getSimpleFirstNonTerminals(a));
    Assert.assertEquals(Set.of(), rules.getSimpleFirstNonTerminals(b));

    Assert.assertEquals(Set.of(start, a, b), rules.getFirstNonTerminals(start));
    Assert.assertEquals(Set.of(a), rules.getFirstNonTerminals(a));
    Assert.assertEquals(Set.of(b), rules.getFirstNonTerminals(b));

    Assert.assertEquals(Set.of(x, y, z), rules.getFirstTerminals(start));
    Assert.assertEquals(Set.of(x), rules.getFirstTerminals(a));
    Assert.assertEquals(Set.of(y, z), rules.getFirstTerminals(b));
  }
}