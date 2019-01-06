package com.github.ahhoefel;

import java.util.*;

public class Grammar {

  private SymbolTable symbols;
  private List<Rule> rules;
  private List<List<Rule>> rulesBySymbolIndex;
  private Rule augmentedStart;

  public Grammar(SymbolTable symbols, List<Rule> rules) {
    this.symbols = symbols;
    this.augmentedStart = new Rule(symbols.getAugmentedStart(), List.of(symbols.getStart()));
    this.rules = new ArrayList<>();
    this.rules.add(augmentedStart);
    this.rules.addAll(rules);
    rulesBySymbolIndex = new ArrayList<>(symbols.getNonTerminals().size());
    for (int i = 0; i < symbols.getNonTerminals().size(); i++) {
      rulesBySymbolIndex.add(new ArrayList<>());
    }
    for (Rule rule : rules) {
      rulesBySymbolIndex.get(rule.getSource().getIndex()).add(rule);
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    for (Rule rule : rules)
      out.append(rule).append("\n");
    return out.toString();
  }

  public SymbolTable getSymbols() {
    return symbols;
  }

  public Rule getAugmentedStartRule() {
    return augmentedStart;
  }

  public List<Rule> get(NonTerminalSymbol symbol) {
    return rulesBySymbolIndex.get(symbol.getIndex());
  }

  public static Set<NonTerminalSymbol> epsilons(Grammar g) {
    Set<NonTerminalSymbol> epsilonRules = new HashSet<>();
    Set<NonTerminalSymbol> visited = new HashSet<>();
    for (NonTerminalSymbol symbol : g.symbols.getNonTerminals()) {
      isEpsilonNonTerminal(g, epsilonRules, visited, symbol);
    }
    return epsilonRules;
  }

  private static boolean isEpsilonNonTerminal(Grammar g, Set<NonTerminalSymbol> epsilonRules, Set<NonTerminalSymbol> visited, NonTerminalSymbol source) {
    if (visited.contains(source)) {
      return epsilonRules.contains(source);
    }
    visited.add(source);
    for (Rule rule : g.get(source)) {
      if (isEpsilonRule(g, epsilonRules, visited, rule)) {
        epsilonRules.add(source);
        return true;
      }
    }
    return false;
  }

  private static boolean isEpsilonRule(Grammar g, Set<NonTerminalSymbol> epsilonRules, Set<NonTerminalSymbol> visited, Rule rule) {
    for (Symbol symbol : rule.getSymbols()) {
      if (symbol.isTerminal()) {
        return false;
      }
    }
    for (Symbol symbol : rule.getSymbols()) {
      if (!isEpsilonNonTerminal(g, epsilonRules, visited, symbol.getNonTerminal())) {
        return false;
      }
    }
    return true;
  }

  public static NonTerminalMap<Set<NonTerminalSymbol>> firstNonTerminals(Grammar g, Set<NonTerminalSymbol> epsilons) {
    NonTerminalMap<Set<NonTerminalSymbol>> firsts = new NonTerminalMap<>(g);
    for (NonTerminalSymbol symbol : g.symbols.getNonTerminals()) {
      firsts.set(symbol, new HashSet<>());
    }
    for (Rule rule : g.rules) {
      addSimpleFirsts(rule, epsilons, firsts);
    }
    for (NonTerminalSymbol symbol : g.symbols.getNonTerminals()) {
      transitiveClosure(symbol, firsts);
    }
    return firsts;
  }

  private static void addSimpleFirsts(Rule rule, Set<NonTerminalSymbol> epsilons, NonTerminalMap<Set<NonTerminalSymbol>> firsts) {
    for (Symbol symbol : rule.getSymbols()) {
      if (symbol.isTerminal()) {
        return;
      }
      NonTerminalSymbol nonTerminal = symbol.getNonTerminal();
      firsts.get(rule.getSource()).add(nonTerminal);
      if (!epsilons.contains(nonTerminal)) {
        return;
      }
    }
  }

  private static void transitiveClosure(NonTerminalSymbol source, NonTerminalMap<Set<NonTerminalSymbol>> firsts) {
    List<NonTerminalSymbol> toVisit = new ArrayList<>();
    toVisit.addAll(firsts.get(source));
    while (!toVisit.isEmpty()) {
      NonTerminalSymbol symbol = toVisit.remove(toVisit.size() - 1);
      Set<NonTerminalSymbol> nexts = firsts.get(symbol);
      for (NonTerminalSymbol next : nexts) {
        if (!firsts.get(source).contains(next)) {
          firsts.get(source).add(next);
          toVisit.add(next);
        }
      }
    }
  }

  public static class NonTerminalMap<T> {
    private T[] values;
    private Grammar grammar;

    public NonTerminalMap(Grammar g) {
      grammar = g;
      values = (T[]) new Object[g.symbols.getNonTerminals().size()];
    }

    public void set(NonTerminalSymbol key, T value) {
      values[key.getIndex()] = value;
    }

    public T get(NonTerminalSymbol key) {
      return values[key.getIndex()];
    }

    public String toString() {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < values.length; i++) {
        out.append(grammar.getSymbols().getNonTerminals().get(i));
        out.append(": ");
        out.append(values[i]);
        out.append("\n");
      }
      return out.toString();
    }

    public boolean equals(Object o) {
      if (!(o instanceof NonTerminalMap)) {
        return false;
      }
      return Arrays.equals(((NonTerminalMap<T>) o).values, this.values);
    }
  }

  public static NonTerminalMap<Set<TerminalSymbol>> firstTerminals(Grammar g, Set<NonTerminalSymbol> epsilons, NonTerminalMap<Set<NonTerminalSymbol>> firstNonTerminals) {
    NonTerminalMap<Set<TerminalSymbol>> firsts = new NonTerminalMap<>(g);
    for (NonTerminalSymbol key : g.symbols.getNonTerminals()) {
      firsts.set(key, new HashSet<>());
    }
    // Finds the first terminal in every rule following epsilon generating non-terminals.
    for (Rule rule : g.rules) {
      for (Symbol symbol : rule.getSymbols()) {
        if (symbol.isTerminal()) {
          firsts.get(rule.getSource()).add(symbol.getTerminal());
          break;
        }
        if (!epsilons.contains(symbol)) {
          break;
        }
      }
    }
    // Set the first terminals for each symbol to be the firsts from first non-terminals.
    for (NonTerminalSymbol key : g.symbols.getNonTerminals()) {
      for (NonTerminalSymbol firstNonTerminal : firstNonTerminals.get(key)) {
        firsts.get(key).addAll(firsts.get(firstNonTerminal));
      }
    }
    return firsts;
  }

  public static FollowingSymbols following(Grammar g) {
    Set<NonTerminalSymbol> epsilons = epsilons(g);
    NonTerminalMap<Set<NonTerminalSymbol>> firstNonTerminals = firstNonTerminals(g, epsilons);
    NonTerminalMap<Set<TerminalSymbol>> firstTerminals = firstTerminals(g, epsilons, firstNonTerminals);

    NonTerminalMap<Set<NonTerminalSymbol>> followingNonTerminals = new NonTerminalMap<>(g);
    NonTerminalMap<Set<NonTerminalSymbol>> followFollowingNonTerminals = new NonTerminalMap<>(g);
    NonTerminalMap<Set<TerminalSymbol>> followingTerminals = new NonTerminalMap<>(g);
    for (NonTerminalSymbol symbol : g.symbols.getNonTerminals()) {
      followingNonTerminals.set(symbol, new HashSet<>());
      followFollowingNonTerminals.set(symbol, new HashSet<>());
      followingTerminals.set(symbol, new HashSet<>());
    }
    followingTerminals.get(g.symbols.getStart()).add(g.symbols.getEof());
    for (Rule rule : g.rules) {
      fillFollowing(followingNonTerminals, followFollowingNonTerminals, followingTerminals, epsilons, firstNonTerminals, firstTerminals, rule);
    }
    cascadeFollowing(g, followingNonTerminals, followFollowingNonTerminals, followingTerminals);
    return new FollowingSymbols(followingNonTerminals, followingTerminals);
  }

  private static void fillFollowing(
      NonTerminalMap<Set<NonTerminalSymbol>> followingNonTerminals,
      NonTerminalMap<Set<NonTerminalSymbol>> followFollowingNonTerminals,
      NonTerminalMap<Set<TerminalSymbol>> followingTerminals,
      Set<NonTerminalSymbol> epsilons,
      NonTerminalMap<Set<NonTerminalSymbol>> firstNonTerminals,
      NonTerminalMap<Set<TerminalSymbol>> firstTerminals,
      Rule rule) {
    // TODO: this is quadratic. I think it could be made linear by
    // starting from the end and working backwards.

    for (int i = 0; i < rule.getSymbols().size(); i++) {
      if (rule.getSymbols().get(i).isTerminal()) {
        continue;
      }
      NonTerminalSymbol nonTerminal = rule.getSymbols().get(i).getNonTerminal();
      boolean done = false;
      int j;
      for (j = i + 1; !done && j < rule.getSymbols().size(); j++) {
        Symbol t = rule.getSymbols().get(j);
        if (t.isTerminal()) {
          followingTerminals.get(nonTerminal).add(t.getTerminal());
          done = true;
        } else {
          NonTerminalSymbol tt = t.getNonTerminal();
          followingNonTerminals.get(nonTerminal).add(tt);
          followingNonTerminals.get(nonTerminal).addAll(firstNonTerminals.get(tt));
          followingTerminals.get(nonTerminal).addAll(firstTerminals.get(tt));
          if (!epsilons.contains(tt)) {
            done = true;
          }
        }
      }
      if (!done) {
        followFollowingNonTerminals.get(nonTerminal).add(rule.getSource());
      }
    }
  }

  private static void cascadeFollowing(
      Grammar g,
      NonTerminalMap<Set<NonTerminalSymbol>> followingNonTerminals,
      NonTerminalMap<Set<NonTerminalSymbol>> followFollowingNonTerminals,
      NonTerminalMap<Set<TerminalSymbol>> followingTerminals) {
//    System.out.println("fnt");
//    System.out.println(followingNonTerminals);
//    System.out.println("ffnt");
//    System.out.println(followFollowingNonTerminals);
//    System.out.println("ft");
//    System.out.println(followingTerminals);

    boolean changed = true;
    while (changed) {
      changed = false;
      for (NonTerminalSymbol x : g.symbols.getNonTerminals()) {
        Set<NonTerminalSymbol> ys = followingNonTerminals.get(x);
        int sizeBefore = ys.size();
        for (NonTerminalSymbol y : ys) {
          ys.addAll(followingNonTerminals.get(y));
        }
        for (NonTerminalSymbol y : followFollowingNonTerminals.get(x)) {
          ys.addAll(followingNonTerminals.get(y));
        }
        if (sizeBefore != ys.size()) {
          changed = true;
        }
      }
    }
//    System.out.println("fnt transitive closure");
//    system.out.println(followingNonTerminals);
    for (NonTerminalSymbol x : g.symbols.getNonTerminals()) {
      for (NonTerminalSymbol y : followingNonTerminals.get(x)) {
        followingTerminals.get(x).addAll(followingTerminals.get(y));
      }
      for (NonTerminalSymbol y : followFollowingNonTerminals.get(x)) {
        followingTerminals.get(x).addAll(followingTerminals.get(y));
      }
    }
  }

  public static class FollowingSymbols {
    private NonTerminalMap<Set<NonTerminalSymbol>> nonTerminals;
    private NonTerminalMap<Set<TerminalSymbol>> terminals;

    public FollowingSymbols(
        NonTerminalMap<Set<NonTerminalSymbol>> nonTerminals,
        NonTerminalMap<Set<TerminalSymbol>> terminals) {
      this.nonTerminals = nonTerminals;
      this.terminals = terminals;
    }

    public Set<NonTerminalSymbol> getNonTerminals(NonTerminalSymbol symbol) {
      return nonTerminals.get(symbol);
    }

    public Set<TerminalSymbol> getTerminals(NonTerminalSymbol symbol) {
      return terminals.get(symbol);
    }

    public boolean equals(Object o) {
      if (!(o instanceof FollowingSymbols)) {
        return false;
      }
      FollowingSymbols other = (FollowingSymbols) o;
      return other.nonTerminals.equals(this.nonTerminals) && other.terminals.equals(this.terminals);
    }

    public String toString() {
      return nonTerminals.toString() + "\n" + terminals.toString();
    }
  }
}
