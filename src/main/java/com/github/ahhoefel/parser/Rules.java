package com.github.ahhoefel.parser;

import java.util.*;

public class Rules {

  private List<Rule> rules;
  private Map<Symbol, List<Rule>> rulesBySymbol;
  private Set<Symbol> epsilonRules;

  private Map<Symbol, Set<Symbol>> firstTerminals;
  private Map<Symbol, Set<Symbol>> simpleFirstTerminals;
  private Map<Symbol, Set<Symbol>> firstNonTerminals;
  private Map<Symbol, Set<Symbol>> simpleFirstNonTerminals;

  private Map<Symbol, Set<Symbol>> followingNonTerminals;
  private Map<Symbol, Set<Symbol>> followingTerminals;
  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;

  public Rules(SymbolTable.TerminalTable terminals, SymbolTable.NonTerminalTable nonTerminals, List<Rule> rules) {
    this.terminals = terminals;
    this.nonTerminals = nonTerminals;
    this.rules = new ArrayList<>();
    this.rules.add(0, new Rule(nonTerminals.getAugmentedStart(), List.of(nonTerminals.getStart())));
    this.rules.addAll(rules);
    rulesBySymbol = makeRulesBySymbol(this.rules);
    epsilonRules = makeEpsilonRules();
    makeFirstTerminals();
  }

  private static Map<Symbol, List<Rule>> makeRulesBySymbol(List<Rule> rules) {
    Map<Symbol, List<Rule>> rulesBySymbol = new HashMap<>();
    for (Rule rule : rules) {
      List<Rule> rulesForSource = rulesBySymbol.get(rule.getSource());
      if (rulesForSource == null) {
        rulesForSource = new ArrayList<>();
        rulesBySymbol.put(rule.getSource(), rulesForSource);
      }
      rulesForSource.add(rule);
    }
    return rulesBySymbol;
  }

  private Set<Symbol> makeEpsilonRules() {
    Set<Symbol> epsilonRules = new HashSet<>();
    Set<Symbol> visited = new HashSet<>();
    for (Symbol symbol : rulesBySymbol.keySet()) {
      isEpsilonNonTerminal(epsilonRules, visited, symbol);
    }
    return epsilonRules;
  }

  private boolean isEpsilonNonTerminal(Set<Symbol> epsilonRules, Set<Symbol> visited, Symbol source) {
    if (visited.contains(source)) {
      return epsilonRules.contains(source);
    }
    visited.add(source);
    for (Rule rule : rulesBySymbol.get(source)) {
      if (isEpsilonRule(epsilonRules, visited, rule)) {
        epsilonRules.add(source);
        return true;
      }
    }
    return false;
  }

  private boolean isEpsilonRule(Set<Symbol> epsilonRules, Set<Symbol> visited, Rule rule) {
    for (Symbol symbol : rule.getSymbols()) {
      if (terminals.contains(symbol)) {
        return false;
      }
    }
    for (Symbol symbol : rule.getSymbols()) {
      if (!isEpsilonNonTerminal(epsilonRules, visited, symbol)) {
        return false;
      }
    }
    return true;
  }

  public boolean isEpsilon(Symbol nonTerminalSymbol) {
    return epsilonRules.contains(nonTerminalSymbol);
  }

  public List<Rule> getRuleList() {
    return rules;
  }

  public Set<Symbol> getNonTerminals() {
    return rulesBySymbol.keySet();
  }

  public List<Rule> getRulesForNonTerminal(Symbol nonTerminalSymbol) {
    return rulesBySymbol.get(nonTerminalSymbol);
  }

  private void makeFirstTerminals() {
    simpleFirstTerminals = new HashMap<>();
    for (Symbol symbol : getNonTerminals()) {
      simpleFirstTerminals.put(symbol, makeSimpleFirstTerminals(symbol));
    }

    simpleFirstNonTerminals = new HashMap<>();
    for (Symbol symbol : getNonTerminals()) {
      simpleFirstNonTerminals.put(symbol, makeSimpleFirstNonTerminals(symbol));
    }

    firstNonTerminals = new HashMap<>();
    for (Symbol symbol : getNonTerminals()) {
      firstNonTerminals.put(symbol, makeFirstNonTerminals(symbol, simpleFirstNonTerminals));
    }

    firstTerminals = new HashMap<>();
    for (Symbol symbol : getNonTerminals()) {
      firstTerminals.put(symbol, makeFirstTerminals(symbol, simpleFirstTerminals, firstNonTerminals));
    }

    followingNonTerminals = new HashMap<>();
    followingTerminals = new HashMap<>();
    for (Symbol symbol : rulesBySymbol.keySet()) {
      followingNonTerminals.put(symbol, new HashSet<>());
      followingTerminals.put(symbol, new HashSet<>());
    }
    for (Rule rule : rules) {
      fillFollowing(rule);
    }
    cascadeFollowing();
  }

  private Set<Symbol> makeSimpleFirstTerminals(Symbol start) {
    Set<Symbol> firsts = new HashSet<>();
    for (Rule rule : getRulesForNonTerminal(start)) {
      if (!rule.getSymbols().isEmpty() && terminals.contains(rule.getSymbols().get(0))) {
        firsts.add(rule.getSymbols().get(0));
      }
    }
    return firsts;
  }

  private Set<Symbol> makeSimpleFirstNonTerminals(Symbol start) {
    Set<Symbol> firsts = new HashSet<>();
    for (Rule rule : getRulesForNonTerminal(start)) {
      for (Symbol symbol : rule.getSymbols()) {
        if (terminals.contains(rule.getSymbols().get(0))) {
          continue;
        }
        firsts.add(symbol);
        if (!isEpsilon(symbol)) {
          break;
        }
      }
    }
    return firsts;
  }

  private Set<Symbol> makeFirstNonTerminals(Symbol start, Map<Symbol, Set<Symbol>> simpleFirstNonTerminals) {
    Set<Symbol> firsts = new HashSet<>();
    List<Symbol> toVisit = new ArrayList<>();
    firsts.add(start);
    toVisit.add(start);
    while (!toVisit.isEmpty()) {
      Symbol symbol = toVisit.remove(toVisit.size() - 1);
      Set<Symbol> nexts = simpleFirstNonTerminals.get(symbol);
      for (Symbol next : nexts) {
        if (!firsts.contains(next)) {
          firsts.add(next);
          toVisit.add(next);
        }
      }
    }
    return firsts;
  }

  private Set<Symbol> makeFirstTerminals(Symbol start, Map<Symbol, Set<Symbol>> simpleFirstTerminals, Map<Symbol, Set<Symbol>> firstNonTerminals) {
    Set<Symbol> firstTerminals = new HashSet<>();
    for (Symbol symbol : firstNonTerminals.get(start)) {
      firstTerminals.addAll(simpleFirstTerminals.get(symbol));
    }
    return firstTerminals;
  }

  private void fillFollowing(Rule rule) {
    // TODO: this is quadratic. I think it could be made linear by
    // starting from the end and working backwards.
    followingTerminals.get(getStart().getSource()).add(terminals.getEof());

    for (int i = 0; i < rule.getSymbols().size(); i++) {
      if (terminals.contains(rule.getSymbols().get(i))) {
        continue;
      }
      Symbol nonTerminal = rule.getSymbols().get(i);
      boolean done = false;
      int j;
      for (j = i + 1; !done && j < rule.getSymbols().size(); j++) {
        Symbol t = rule.getSymbols().get(j);
        if (terminals.contains(t)) {
          followingTerminals.get(nonTerminal).add(t);
          done = true;
        } else {
          followingNonTerminals.get(nonTerminal).add(t);
          if (!isEpsilon(t)) {
            done = true;
          }
        }
      }
      if (!done) {
        followingNonTerminals.get(rule.getSource()).add(nonTerminal);
      }
    }
  }

  private void cascadeFollowing() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Map.Entry<Symbol, Set<Symbol>> entry : followingNonTerminals.entrySet()) {
        Set<Symbol> from = followingTerminals.get(entry.getKey());
        for (Symbol toNonTerminal : entry.getValue()) {
          Set<Symbol> to = followingTerminals.get(toNonTerminal);
          int sizeBefore = to.size();
          to.addAll(from);
          if (sizeBefore != to.size()) {
            changed = true;
          }
        }
      }
    }
  }

  public Set<Symbol> getFirstTerminals(Symbol symbol) {
    return firstTerminals.get(symbol);
  }

  public Set<Symbol> getSimpleFirstTerminals(Symbol symbol) {
    return simpleFirstTerminals.get(symbol);
  }

  public Set<Symbol> getSimpleFirstNonTerminals(Symbol symbol) {
    return simpleFirstNonTerminals.get(symbol);
  }

  public Set<Symbol> getFirstNonTerminals(Symbol symbol) {
    return firstNonTerminals.get(symbol);
  }

  public Set<Symbol> getFollow(Symbol symbol) {
    return followingTerminals.get(symbol);
  }

  public Rule getStart() {
    return this.rules.get(0);
  }

  public Symbol getEof() {
    return this.terminals.getEof();
  }
}
