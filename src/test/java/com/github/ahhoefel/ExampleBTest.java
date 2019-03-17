package com.github.ahhoefel;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ExampleBTest {

  @Test
  public void testStuff() {
    ExampleB example = new ExampleB();
    Grammar g = example.grammar;
    Set<Symbol> epsilons = Grammar.epsilons(g);
    Grammar.NonTerminalMap<Set<Symbol>> firstNonTerminals = Grammar.firstNonTerminals(g, epsilons);
    Grammar.NonTerminalMap<Set<Symbol>> firstTerminals = Grammar.firstTerminals(g, epsilons, firstNonTerminals);
    Grammar.FollowingSymbols following = Grammar.following(g);

    Assert.assertEquals(epsilons, Set.of());

    Grammar.NonTerminalMap<Set<Symbol>> expectedFirstNonTerminals = new Grammar.NonTerminalMap<>(g);
    expectedFirstNonTerminals.set(example.startp, Set.of(example.start, example.t, example.f));
    expectedFirstNonTerminals.set(example.start, Set.of(example.start, example.t, example.f));
    expectedFirstNonTerminals.set(example.t, Set.of(example.t, example.f));
    expectedFirstNonTerminals.set(example.f, Set.of());
    Assert.assertEquals(firstNonTerminals, expectedFirstNonTerminals);

    Grammar.NonTerminalMap<Set<Symbol>> expectedFirstTerminals = new Grammar.NonTerminalMap<>(g);
    expectedFirstTerminals.set(example.startp, Set.of(example.n, example.lparen));
    expectedFirstTerminals.set(example.start, Set.of(example.n, example.lparen));
    expectedFirstTerminals.set(example.t, Set.of(example.n, example.lparen));
    expectedFirstTerminals.set(example.f, Set.of(example.n, example.lparen));
    Assert.assertEquals(firstTerminals, expectedFirstTerminals);

    Grammar.NonTerminalMap<Set<Symbol>> expectedFollowingNonTerminals = new Grammar.NonTerminalMap<>(g);
    expectedFollowingNonTerminals.set(example.startp, Set.of());
    expectedFollowingNonTerminals.set(example.start, Set.of());
    expectedFollowingNonTerminals.set(example.t, Set.of());
    expectedFollowingNonTerminals.set(example.f, Set.of());
    Grammar.NonTerminalMap<Set<Symbol>> expectedFollowingTerminals = new Grammar.NonTerminalMap<>(g);
    expectedFollowingTerminals.set(example.startp, Set.of());
    expectedFollowingTerminals.set(example.start, Set.of(example.eof, example.plus, example.rparen));
    expectedFollowingTerminals.set(example.t, Set.of(example.eof, example.plus, example.times, example.rparen));
    expectedFollowingTerminals.set(example.f, Set.of(example.eof, example.plus, example.times, example.rparen));
    Grammar.FollowingSymbols expectedFollowingSymbols = new Grammar.FollowingSymbols(expectedFollowingNonTerminals, expectedFollowingTerminals);
    Assert.assertEquals(expectedFollowingSymbols, following);

    // System.out.println(example.grammar);
  }
}
