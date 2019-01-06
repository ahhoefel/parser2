package com.github.ahhoefel;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class LRItemTest {

  @Test
  public void testClosure() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol a = sb.newNonTerminal("A");
    NonTerminalSymbol b = sb.newNonTerminal("B");
    TerminalSymbol x = sb.newTerminal("x");
    TerminalSymbol y = sb.newTerminal("y");
    TerminalSymbol z = sb.newTerminal("z");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol start = symbols.getStart();
    Rule r0 = new Rule(start, List.of(a, b));
    Rule r1 = new Rule(a, List.of(x, a, b));
    Rule r2 = new Rule(a, List.of());
    Rule r3 = new Rule(b, List.of(y));
    Rule r4 = new Rule(b, List.of(z));
    Grammar grammar = new Grammar(symbols, List.of(r0, r1, r2, r3, r4));
    Set<MarkedRule> closure = LRItem.closure(new MarkedRule(r0, 0), grammar);
    Assert.assertEquals(closure, Set.of(
        new MarkedRule(r0, 0),
        new MarkedRule(r1, 0),
        new MarkedRule(r2, 0)
    ));
  }

  @Test
  public void testClosureSeed() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol t = sb.newNonTerminal("T");
    NonTerminalSymbol f = sb.newNonTerminal("F");
    TerminalSymbol plus = sb.newTerminal("+");
    TerminalSymbol times = sb.newTerminal("*");
    TerminalSymbol n = sb.newTerminal("n");
    TerminalSymbol lparen = sb.newTerminal("(");
    TerminalSymbol rparen = sb.newTerminal(")");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol start = symbols.getStart();
    Rule r1 = new Rule(start, List.of(t));
    Rule r2 = new Rule(start, List.of(start, plus, t));
    Rule r3 = new Rule(t, List.of(f));
    Rule r4 = new Rule(t, List.of(t, times, f));
    Rule r5 = new Rule(f, List.of(n));
    Rule r6 = new Rule(f, List.of(lparen, start, rparen));
    Grammar grammar = new Grammar(symbols, List.of(r1, r2, r3, r4, r5, r6));

    Set<MarkedRule> closure = LRItem.closure(new MarkedRule(r6, 1), grammar);
    Assert.assertEquals(Set.of(
        new MarkedRule(r6, 1),
        new MarkedRule(r1, 0),
        new MarkedRule(r2, 0),
        new MarkedRule(r3, 0),
        new MarkedRule(r4, 0),
        new MarkedRule(r5, 0),
        new MarkedRule(r6, 0)
    ), closure);

    closure = LRItem.closure(
        Set.of(
            new MarkedRule(r1, 1)
        ), grammar);
    Assert.assertEquals(Set.of(
        new MarkedRule(r1, 1)
    ), closure);
  }

  @Test
  public void testMakeItemGraph() {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol t = sb.newNonTerminal("T");
    NonTerminalSymbol f = sb.newNonTerminal("F");
    TerminalSymbol plus = sb.newTerminal("+");
    TerminalSymbol times = sb.newTerminal("*");
    TerminalSymbol n = sb.newTerminal("n");
    TerminalSymbol lparen = sb.newTerminal("(");
    TerminalSymbol rparen = sb.newTerminal(")");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol start = symbols.getStart();
    TerminalSymbol eof = symbols.getEof();
    Rule r1 = new Rule(start, List.of(t));
    Rule r2 = new Rule(start, List.of(start, plus, t));
    Rule r3 = new Rule(t, List.of(f));
    Rule r4 = new Rule(t, List.of(t, times, f));
    Rule r5 = new Rule(f, List.of(n));
    Rule r6 = new Rule(f, List.of(lparen, start, rparen));
    Grammar grammar = new Grammar(symbols, List.of(r1, r2, r3, r4, r5, r6));
    Rules rules = new Rules(symbols, List.of(r1, r2, r3, r4, r5, r6));
    LRParser parser = LRItem.makeItemGraph(grammar);
    LRParser parser2 = LRItem.makeItemGraph(rules);
    Assert.assertEquals(12, parser.items.size());

    // System.out.println(parser);
    // System.out.println(parser2);

    System.out.println(parser.getTable(grammar));
    System.out.println("Rules:");
    System.out.println(parser2.getTable(rules));


    List<TerminalSymbol> input = List.of(n, plus, n, eof);
    ParseTree tree = Parser.parseTerminals(parser.getTable(grammar), input.iterator(), grammar.getAugmentedStartRule().getSource());
    System.out.println(tree);
  }
/*
  @Test
  public void testMakeItemGraph2() {
    NonTerminalSymbol s = new NonTerminalSymbol("S");
    NonTerminalSymbol a = new NonTerminalSymbol("A");
    NonTerminalSymbol b = new NonTerminalSymbol("B");
    TerminalSymbol<String> x = new TerminalSymbol<>("x");
    TerminalSymbol<String> y = new TerminalSymbol<>("y");
    TerminalSymbol<String> z = new TerminalSymbol<>("z");
    TerminalSymbol<String> eof = new TerminalSymbol<>("eof");
    Rule r1 = new Rule(s, List.of(a, b));
    Rule r2 = new Rule(a, List.of(x, a, b));
    Rule r3 = new Rule(a, List.of());
    Rule r4 = new Rule(b, List.of(y));
    Rule r5 = new Rule(b, List.of(z));
    Rules rules = new Rules(List.of(r1, r2, r3, r4, r5), eof);
    LRParser parser = LRItem.makeItemGraph(rules);
    Assert.assertEquals(9, parser.items.size());
  }

  @Test
  public void testParser() {
    NonTerminalSymbol start = new NonTerminalSymbol("start");
    NonTerminalSymbol s = new NonTerminalSymbol("S");
    NonTerminalSymbol a = new NonTerminalSymbol("A");
    NonTerminalSymbol b = new NonTerminalSymbol("B");
    TerminalSymbol<String> x = new TerminalSymbol<>("x");
    TerminalSymbol<String> y = new TerminalSymbol<>("y");
    TerminalSymbol<String> z = new TerminalSymbol<>("z");
    TerminalSymbol<String> eof = new TerminalSymbol<>("eof");
    Rule r0 = new Rule(start, List.of(s));
    Rule r1 = new Rule(s, List.of(a, b));
    Rule r2 = new Rule(a, List.of(x, a, b));
    Rule r3 = new Rule(a, List.of());
    Rule r4 = new Rule(b, List.of(y));
    Rule r5 = new Rule(b, List.of(z));
    List<Rule> rs = List.of(r0, r1, r2, r3, r4, r5);
    Rules rules = new Rules(rs, eof);
    LRParser parser = LRItem.makeItemGraph(rules);
    LRTable table = parser.getTable(rules);
    //System.out.println(parser);
    // System.out.println(table);
    //List<String> input = List.of("x", "x", "y", "z");
    // ParseTree<String> tree = Parser.parseTerminals(table, input.iterator(), start);
    // System.out.println(tree);
  }
  */
}
