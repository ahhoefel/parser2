package com.github.ahhoefel;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class GrammarTest {
  @Test
  public void testIsEpsilon() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol a = sb.newNonTerminal("A");
    NonTerminalSymbol b = sb.newNonTerminal("B");
    TerminalSymbol x = sb.newTerminal("x");
    TerminalSymbol y = sb.newTerminal("y");
    TerminalSymbol z = sb.newTerminal("z");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol s = symbols.getStart();
    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);
    Grammar grammar = new Grammar(symbols, rs);
    Set<NonTerminalSymbol> epsilons = Grammar.epsilons(grammar);
    Set<NonTerminalSymbol> expectedEpsilons = Set.of(a);
    Assert.assertEquals(expectedEpsilons, epsilons);
  }

  @Test
  public void testFirsts() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol a = sb.newNonTerminal("A");
    NonTerminalSymbol b = sb.newNonTerminal("B");
    TerminalSymbol x = sb.newTerminal("x");
    TerminalSymbol y = sb.newTerminal("y");
    TerminalSymbol z = sb.newTerminal("z");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol s = symbols.getStart();
    Rule r0 = new Rule(s, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4);

    Grammar grammar = new Grammar(symbols, rs);
    Set<NonTerminalSymbol> epsilons = Grammar.epsilons(grammar);
    Grammar.NonTerminalMap<Set<NonTerminalSymbol>> firstNonTerminals = Grammar.firstNonTerminals(grammar, epsilons);
    Grammar.NonTerminalMap<Set<TerminalSymbol>> firstTerminals = Grammar.firstTerminals(grammar, epsilons, firstNonTerminals);

    Assert.assertEquals(Set.of(a, b), firstNonTerminals.get(s));
    Assert.assertEquals(Set.of(), firstNonTerminals.get(a));
    Assert.assertEquals(Set.of(), firstNonTerminals.get(b));

    Assert.assertEquals(Set.of(x, y, z), firstTerminals.get(s));
    Assert.assertEquals(Set.of(x), firstTerminals.get(a));
    Assert.assertEquals(Set.of(y, z), firstTerminals.get(b));
  }

  @Test
  public void testFirstsTwo() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol a = sb.newNonTerminal("A");
    NonTerminalSymbol b = sb.newNonTerminal("B");
    NonTerminalSymbol c = sb.newNonTerminal("C");
    NonTerminalSymbol d = sb.newNonTerminal("D");
    NonTerminalSymbol e = sb.newNonTerminal("E");
    TerminalSymbol x = sb.newTerminal("x");
    TerminalSymbol w = sb.newTerminal("w");
    TerminalSymbol y = sb.newTerminal("y");
    TerminalSymbol z = sb.newTerminal("z");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol s = symbols.getStart();
    Rule r0 = new Rule(s, List.of(a));
    Rule r1 = new Rule(a, List.of(b));
    Rule r2 = new Rule(a, List.of(a, c));
    Rule r3 = new Rule(a, List.of(x));
    Rule r4 = new Rule(b, List.of(d, e));
    Rule r5 = new Rule(c, List.of(w));
    Rule r6 = new Rule(d, List.of(y));
    Rule r7 = new Rule(d, List.of());
    Rule r8 = new Rule(e, List.of(z));

    Grammar grammar = new Grammar(symbols, List.of(r0, r1, r2, r3, r4, r5, r6, r7, r8));
    Set<NonTerminalSymbol> epsilons = Grammar.epsilons(grammar);
    Grammar.NonTerminalMap<Set<NonTerminalSymbol>> firstNonTerminals = Grammar.firstNonTerminals(grammar, epsilons);
    Grammar.NonTerminalMap<Set<TerminalSymbol>> firstTerminals = Grammar.firstTerminals(grammar, epsilons, firstNonTerminals);

    Assert.assertEquals(Set.of(d), epsilons);

    Assert.assertEquals(Set.of(a, b, d, e), firstNonTerminals.get(s));
    Assert.assertEquals(Set.of(a, b, d, e), firstNonTerminals.get(a));
    Assert.assertEquals(Set.of(d, e), firstNonTerminals.get(b));
    Assert.assertEquals(Set.of(), firstNonTerminals.get(c));
    Assert.assertEquals(Set.of(), firstNonTerminals.get(d));
    Assert.assertEquals(Set.of(), firstNonTerminals.get(e));

    Assert.assertEquals(Set.of(x, y, z), firstTerminals.get(s));
    Assert.assertEquals(Set.of(x, y, z), firstTerminals.get(a));
    Assert.assertEquals(Set.of(y, z), firstTerminals.get(b));
    Assert.assertEquals(Set.of(w), firstTerminals.get(c));
    Assert.assertEquals(Set.of(y), firstTerminals.get(d));
    Assert.assertEquals(Set.of(z), firstTerminals.get(e));
  }

  @Test
  public void testFollowing() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol a = sb.newNonTerminal("A");
    NonTerminalSymbol b = sb.newNonTerminal("B");
    NonTerminalSymbol c = sb.newNonTerminal("C");
    TerminalSymbol x = sb.newTerminal("x");
    TerminalSymbol y = sb.newTerminal("y");
    TerminalSymbol z = sb.newTerminal("z");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol s = symbols.getStart();

    Rule r0 = new Rule(s, List.of(a, b, c));
    Rule r1 = new Rule(a, List.of(x));
    Rule r2 = new Rule(b, List.of(y));
    Rule r3 = new Rule(b, List.of());
    Rule r4 = new Rule(c, List.of(z));

    Grammar grammar = new Grammar(symbols, List.of(r0, r1, r2, r3, r4));
    Grammar.FollowingSymbols follow = Grammar.following(grammar);

    Assert.assertEquals(Set.of(b, c), follow.getNonTerminals(a));
    Assert.assertEquals(Set.of(c), follow.getNonTerminals(b));
    Assert.assertEquals(Set.of(), follow.getNonTerminals(c));

    Assert.assertEquals(Set.of(y, z), follow.getTerminals(a));
    Assert.assertEquals(Set.of(z), follow.getTerminals(b));
    Assert.assertEquals(Set.of(symbols.getEof()), follow.getTerminals(c));
  }
}
