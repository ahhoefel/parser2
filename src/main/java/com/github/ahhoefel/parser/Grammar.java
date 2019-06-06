package com.github.ahhoefel.parser;

import java.util.*;

public class Grammar {

  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private List<Rule> rules;
  private List<List<Rule>> rulesBySymbolIndex;
  private Rule augmentedStart;

  public Grammar(SymbolTable.TerminalTable terminals, SymbolTable.NonTerminalTable nonTerminals, List<Rule> rules) {
    this.terminals = terminals;
    this.nonTerminals = nonTerminals;
    this.augmentedStart = new Rule(nonTerminals.getAugmentedStart(), List.of(nonTerminals.getStart()));
    this.rules = new ArrayList<>();
    this.rules.add(augmentedStart);
    this.rules.addAll(rules);
    rulesBySymbolIndex = new ArrayList<>(nonTerminals.size());
    for (int i = 0; i < nonTerminals.size(); i++) {
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

  public SymbolTable.TerminalTable getTerminals() {
    return terminals;
  }

  public SymbolTable.NonTerminalTable getNonTerminals() {
    return nonTerminals;
  }

  public Rule getAugmentedStartRule() {
    return augmentedStart;
  }

  public List<Rule> get(Symbol symbol) {
    return rulesBySymbolIndex.get(symbol.getIndex());
  }

  public boolean isTerminal(Symbol symbol) {
    return terminals.contains(symbol);
  }


  public FirstSymbols first() {
    return new FirstSymbols();
  }

  public static Set<Symbol> epsilons(Grammar g) {
    Set<Symbol> epsilonRules = new HashSet<>();
    Set<Symbol> visited = new HashSet<>();
    for (Symbol symbol : g.nonTerminals.getSymbols()) {
      isEpsilonNonTerminal(g, epsilonRules, visited, symbol);
    }
    return epsilonRules;
  }

  private static boolean isEpsilonNonTerminal(Grammar g, Set<Symbol> epsilonRules, Set<Symbol> visited, Symbol source) {
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

  private static boolean isEpsilonRule(Grammar g, Set<Symbol> epsilonRules, Set<Symbol> visited, Rule rule) {
    for (Symbol symbol : rule.getSymbols()) {
      if (g.isTerminal(symbol)) {
        return false;
      }
    }
    for (Symbol symbol : rule.getSymbols()) {
      if (!isEpsilonNonTerminal(g, epsilonRules, visited, symbol)) {
        return false;
      }
    }
    return true;
  }

  public static NonTerminalMap<Set<Symbol>> firstNonTerminals(Grammar g, Set<Symbol> epsilons) {
    NonTerminalMap<Set<Symbol>> firsts = new NonTerminalMap<>(g);
    for (Symbol symbol : g.nonTerminals.getSymbols()) {
      firsts.set(symbol, new HashSet<>());
    }
    for (Rule rule : g.rules) {
      addSimpleFirsts(g, rule, epsilons, firsts);
    }
    for (Symbol symbol : g.nonTerminals.getSymbols()) {
      transitiveClosure(symbol, firsts);
    }
    return firsts;
  }

  private static void addSimpleFirsts(Grammar g, Rule rule, Set<Symbol> epsilons, NonTerminalMap<Set<Symbol>> firsts) {
    for (Symbol symbol : rule.getSymbols()) {
      if (g.isTerminal(symbol)) {
        return;
      }
      firsts.get(rule.getSource()).add(symbol);
      if (!epsilons.contains(symbol)) {
        return;
      }
    }
  }

  private static void transitiveClosure(Symbol source, NonTerminalMap<Set<Symbol>> firsts) {
    List<Symbol> toVisit = new ArrayList<>();
    toVisit.addAll(firsts.get(source));
    while (!toVisit.isEmpty()) {
      Symbol symbol = toVisit.remove(toVisit.size() - 1);
      Set<Symbol> nexts = firsts.get(symbol);
      for (Symbol next : nexts) {
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
      values = (T[]) new Object[g.nonTerminals.getSymbols().size()];
    }

    public void set(Symbol key, T value) {
      values[key.getIndex()] = value;
    }

    public T get(Symbol key) {
      return values[key.getIndex()];
    }

    public String toString() {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < values.length; i++) {
        out.append(grammar.getNonTerminals().getSymbols().get(i));
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

  public static NonTerminalMap<Set<Symbol>> firstTerminals(Grammar g, Set<Symbol> epsilons, NonTerminalMap<Set<Symbol>> firstNonTerminals) {
    NonTerminalMap<Set<Symbol>> firsts = new NonTerminalMap<>(g);
    for (Symbol key : g.nonTerminals.getSymbols()) {
      firsts.set(key, new HashSet<>());
    }
    // Finds the first terminal in every rule following epsilon generating non-terminals.
    for (Rule rule : g.rules) {
      for (Symbol symbol : rule.getSymbols()) {
        if (g.isTerminal(symbol)) {
          firsts.get(rule.getSource()).add(symbol);
          break;
        }
        if (!epsilons.contains(symbol)) {
          break;
        }
      }
    }
    // Set the first terminals for each symbol to be the firsts from first non-terminals.
    for (Symbol key : g.nonTerminals.getSymbols()) {
      for (Symbol firstNonTerminal : firstNonTerminals.get(key)) {
        firsts.get(key).addAll(firsts.get(firstNonTerminal));
      }
    }
    return firsts;
  }

  public FollowingSymbols following() {
    return following(this.first());
  }

  public FollowingSymbols following(FirstSymbols first) {
    NonTerminalMap<Set<Symbol>> followingNonTerminals = new NonTerminalMap<>(this);
    NonTerminalMap<Set<Symbol>> followFollowingNonTerminals = new NonTerminalMap<>(this);
    NonTerminalMap<Set<Symbol>> followingTerminals = new NonTerminalMap<>(this);
    for (Symbol symbol : this.nonTerminals.getSymbols()) {
      followingNonTerminals.set(symbol, new HashSet<>());
      followFollowingNonTerminals.set(symbol, new HashSet<>());
      followingTerminals.set(symbol, new HashSet<>());
    }
    followingTerminals.get(this.nonTerminals.getStart()).add(this.terminals.getEof());
    for (Rule rule : this.rules) {
      fillFollowing(this, followingNonTerminals, followFollowingNonTerminals, followingTerminals, first, rule);
    }
    cascadeFollowing(this, followingNonTerminals, followFollowingNonTerminals, followingTerminals);
    return new FollowingSymbols(followingNonTerminals, followingTerminals);
  }

  private static void fillFollowing(
      Grammar g,
      NonTerminalMap<Set<Symbol>> followingNonTerminals,
      NonTerminalMap<Set<Symbol>> followFollowingNonTerminals,
      NonTerminalMap<Set<Symbol>> followingTerminals,
      FirstSymbols first,
      Rule rule) {
    // TODO: this is quadratic. I think it could be made linear by
    // starting from the end and working backwards.

    for (int i = 0; i < rule.getSymbols().size(); i++) {
      if (g.isTerminal(rule.getSymbols().get(i))) {
        continue;
      }
      Symbol nonTerminal = rule.getSymbols().get(i);
      boolean done = false;
      int j;
      for (j = i + 1; !done && j < rule.getSymbols().size(); j++) {
        Symbol t = rule.getSymbols().get(j);
        if (g.isTerminal(t)) {
          followingTerminals.get(nonTerminal).add(t);
          done = true;
        } else {
          followingNonTerminals.get(nonTerminal).add(t);
          followingNonTerminals.get(nonTerminal).addAll(first.getFirstNonTerminals(t));
          followingTerminals.get(nonTerminal).addAll(first.getFirstTerminals(t));
          if (!first.getEpsilons().contains(t)) {
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
      NonTerminalMap<Set<Symbol>> followingNonTerminals,
      NonTerminalMap<Set<Symbol>> followFollowingNonTerminals,
      NonTerminalMap<Set<Symbol>> followingTerminals) {

    boolean changed = true;
    while (changed) {
      changed = false;
      for (Symbol x : g.nonTerminals.getSymbols()) {
        Set<Symbol> ys = followingNonTerminals.get(x);
        int sizeBefore = ys.size();
        List<Symbol> toAdd = new ArrayList<>();
        for (Symbol y : ys) {
          toAdd.addAll(followingNonTerminals.get(y));
        }
        ys.addAll(toAdd);
        for (Symbol y : followFollowingNonTerminals.get(x)) {
          ys.addAll(followingNonTerminals.get(y));
        }
        if (sizeBefore != ys.size()) {
          changed = true;
        }
      }
    }
    for (Symbol x : g.nonTerminals.getSymbols()) {
      for (Symbol y : followingNonTerminals.get(x)) {
        followingTerminals.get(x).addAll(followingTerminals.get(y));
      }
      for (Symbol y : followFollowingNonTerminals.get(x)) {
        followingTerminals.get(x).addAll(followingTerminals.get(y));
      }
    }
  }

  public static class FollowingSymbols {
    private NonTerminalMap<Set<Symbol>> nonTerminals;
    private NonTerminalMap<Set<Symbol>> terminals;

    public FollowingSymbols(
        NonTerminalMap<Set<Symbol>> nonTerminals,
        NonTerminalMap<Set<Symbol>> terminals) {
      this.nonTerminals = nonTerminals;
      this.terminals = terminals;
    }

    public Set<Symbol> getNonTerminals(Symbol symbol) {
      return nonTerminals.get(symbol);
    }

    public Set<Symbol> getTerminals(Symbol symbol) {
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

  public class FirstSymbols {
    private NonTerminalMap<Set<Symbol>> nonTerminals;
    private NonTerminalMap<Set<Symbol>> terminals;
    private Set<Symbol> epsilons;

    public FirstSymbols() {
      this.epsilons = epsilons(Grammar.this);
      this.nonTerminals = firstNonTerminals(Grammar.this, epsilons);
      this.terminals = firstTerminals(Grammar.this, epsilons, nonTerminals);
    }

    public Set<Symbol> getEpsilons() {
      return epsilons;
    }

    public Set<Symbol> getFirstTerminals(Symbol nonTerminal) {
      return terminals.get(nonTerminal);
    }

    public Set<Symbol> getFirstNonTerminals(Symbol nonTerminal) {
      return nonTerminals.get(nonTerminal);
    }
  }
}
