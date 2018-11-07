package com.github.ahhoefel;

import java.util.*;

public class Rules {

  private List<Rule> rules;
  private Map<NonTerminalSymbol, List<Rule>> rulesBySymbol;
  private Set<NonTerminalSymbol> epsilonRules;

  private Map<NonTerminalSymbol, Set<TerminalSymbol>> firstTerminals;
  private Map<NonTerminalSymbol, Set<TerminalSymbol>> simpleFirstTerminals;
  private Map<NonTerminalSymbol, Set<NonTerminalSymbol>> firstNonTerminals;
  private Map<NonTerminalSymbol, Set<NonTerminalSymbol>> simpleFirstNonTerminals;

  private Map<NonTerminalSymbol, Set<NonTerminalSymbol>> followingNonTerminals;
  private Map<NonTerminalSymbol, Set<TerminalSymbol>> followingTerminals;
  private TerminalSymbol eof;

  public Rules(List<Rule> rules, TerminalSymbol eof) {
    this.eof = eof;
    this.rules = new ArrayList<>();
    this.rules.add(0, new Rule(new NonTerminalSymbol("start"), List.of(rules.get(0).getSource())));
    this.rules.addAll(rules);
    rulesBySymbol = makeRulesBySymbol(this.rules);
    epsilonRules = makeEpsilonRules();
    makeFirstTerminals();
  }

  private static Map<NonTerminalSymbol, List<Rule>> makeRulesBySymbol(List<Rule> rules) {
    Map<NonTerminalSymbol, List<Rule>> rulesBySymbol = new HashMap<>();
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

  private Set<NonTerminalSymbol> makeEpsilonRules() {
    Set<NonTerminalSymbol> epsilonRules = new HashSet<>();
    Set<NonTerminalSymbol> visited = new HashSet<>();
    for (NonTerminalSymbol symbol : rulesBySymbol.keySet()) {
      isEpsilonNonTerminal(epsilonRules, visited, symbol);
    }
    return epsilonRules;
  }

  private boolean isEpsilonNonTerminal(Set<NonTerminalSymbol> epsilonRules, Set<NonTerminalSymbol> visited, NonTerminalSymbol source) {
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

  private boolean isEpsilonRule(Set<NonTerminalSymbol> epsilonRules, Set<NonTerminalSymbol> visited, Rule rule) {
    for (Symbol symbol : rule.getSymbols()) {
      if (symbol.isTerminal()) {
        return false;
      }
    }
    for (Symbol symbol : rule.getSymbols()) {
      if (!isEpsilonNonTerminal(epsilonRules, visited, (NonTerminalSymbol) symbol)) {
        return false;
      }
    }
    return true;
  }

  public boolean isEpsilon(NonTerminalSymbol nonTerminalSymbol) {
    return epsilonRules.contains(nonTerminalSymbol);
  }

  public List<Rule> getRuleList() {
    return rules;
  }

  public Set<NonTerminalSymbol> getNonTerminals() {
    return rulesBySymbol.keySet();
  }

  public List<Rule> getRulesForNonTerminal(NonTerminalSymbol nonTerminalSymbol) {
    return rulesBySymbol.get(nonTerminalSymbol);
  }

  private void makeFirstTerminals() {
    simpleFirstTerminals = new HashMap<>();
    for (NonTerminalSymbol symbol : getNonTerminals()) {
      simpleFirstTerminals.put(symbol, makeSimpleFirstTerminals(symbol));
    }

    simpleFirstNonTerminals = new HashMap<>();
    for (NonTerminalSymbol symbol : getNonTerminals()) {
      simpleFirstNonTerminals.put(symbol, makeSimpleFirstNonTerminals(symbol));
    }

    firstNonTerminals = new HashMap<>();
    for (NonTerminalSymbol symbol : getNonTerminals()) {
      firstNonTerminals.put(symbol, makeFirstNonTerminals(symbol, simpleFirstNonTerminals));
    }

    firstTerminals = new HashMap<>();
    for (NonTerminalSymbol symbol : getNonTerminals()) {
      firstTerminals.put(symbol, makeFirstTerminals(symbol, simpleFirstTerminals, firstNonTerminals));
    }

    followingNonTerminals = new HashMap<>();
    followingTerminals = new HashMap<>();
    for (NonTerminalSymbol symbol : rulesBySymbol.keySet()) {
      followingNonTerminals.put(symbol, new HashSet<>());
      followingTerminals.put(symbol, new HashSet<>());
    }
    for (Rule rule : rules) {
      fillFollowing(rule);
    }
    cascadeFollowing();
  }

  private Set<TerminalSymbol> makeSimpleFirstTerminals(NonTerminalSymbol start)  {
    Set<TerminalSymbol> firsts = new HashSet<>();
    for (Rule rule : getRulesForNonTerminal(start)) {
      if (!rule.getSymbols().isEmpty() && rule.getSymbols().get(0).isTerminal()) {
        firsts.add((TerminalSymbol) rule.getSymbols().get(0));
      }
    }
    return firsts;
  }

  private Set<NonTerminalSymbol> makeSimpleFirstNonTerminals(NonTerminalSymbol start) {
    Set<NonTerminalSymbol> firsts = new HashSet<>();
    for (Rule rule : getRulesForNonTerminal(start)) {
      for (Symbol symbol : rule.getSymbols()) {
        if(rule.getSymbols().get(0).isTerminal()) {
          continue;
        }
        NonTerminalSymbol nonTerminal = (NonTerminalSymbol) symbol;
        firsts.add(nonTerminal);
        if (!isEpsilon(nonTerminal)) {
          break;
        }
      }
    }
    return firsts;
  }

  private Set<NonTerminalSymbol> makeFirstNonTerminals(NonTerminalSymbol start, Map<NonTerminalSymbol, Set<NonTerminalSymbol>> simpleFirstNonTerminals) {
    Set<NonTerminalSymbol> firsts = new HashSet<>();
    List<NonTerminalSymbol> toVisit = new ArrayList<>();
    firsts.add(start);
    toVisit.add(start);
    while (!toVisit.isEmpty()) {
      NonTerminalSymbol symbol = toVisit.remove(toVisit.size()-1);
      Set<NonTerminalSymbol> nexts = simpleFirstNonTerminals.get(symbol);
      for (NonTerminalSymbol next : nexts) {
        if (!firsts.contains(next)) {
          firsts.add(next);
          toVisit.add(next);
        }
      }
    }
    return firsts;
  }

  private Set<TerminalSymbol> makeFirstTerminals(NonTerminalSymbol start, Map<NonTerminalSymbol, Set<TerminalSymbol>> simpleFirstTerminals, Map<NonTerminalSymbol, Set<NonTerminalSymbol>> firstNonTerminals) {
    Set<TerminalSymbol> firstTerminals = new HashSet<>();
    for (NonTerminalSymbol symbol : firstNonTerminals.get(start)) {
      firstTerminals.addAll(simpleFirstTerminals.get(symbol));
    }
    return firstTerminals;
  }

  private void fillFollowing(Rule rule) {
    // TODO: this is quadratic. I think it could be made linear by
    // starting from the end and working backwards.
    followingTerminals.get(getStart().getSource()).add(eof);

    for (int i = 0; i < rule.getSymbols().size(); i++) {
      if (rule.getSymbols().get(i).isTerminal()) {
        continue;
      }
      NonTerminalSymbol nonTerminal = (NonTerminalSymbol) rule.getSymbols().get(i);
      boolean done = false;
      int j;
      for (j = i+1; !done && j < rule.getSymbols().size(); j++) {
        Symbol t = rule.getSymbols().get(j);
        if (t.isTerminal()) {
          followingTerminals.get(nonTerminal).add((TerminalSymbol) t);
          done = true;
        } else {
          NonTerminalSymbol tt = (NonTerminalSymbol) t;
          followingNonTerminals.get(nonTerminal).add(tt);
          if (!isEpsilon(tt)) {
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
      for (Map.Entry<NonTerminalSymbol, Set<NonTerminalSymbol>> entry : followingNonTerminals.entrySet()) {
        Set<TerminalSymbol> from = followingTerminals.get(entry.getKey());
        for (NonTerminalSymbol toNonTerminal : entry.getValue()) {
          Set<TerminalSymbol> to = followingTerminals.get(toNonTerminal);
          int sizeBefore = to.size();
          to.addAll(from);
          if (sizeBefore != to.size()) {
            changed = true;
          }
        }
      }
    }
  }

  public Set<TerminalSymbol> getFirstTerminals(NonTerminalSymbol symbol) {
    return firstTerminals.get(symbol);
  }

  public Set<TerminalSymbol> getSimpleFirstTerminals(NonTerminalSymbol symbol) {
    return simpleFirstTerminals.get(symbol);
  }

  public Set<NonTerminalSymbol> getSimpleFirstNonTerminals(NonTerminalSymbol symbol) {
    return simpleFirstNonTerminals.get(symbol);
  }

  public Set<NonTerminalSymbol> getFirstNonTerminals(NonTerminalSymbol symbol) {
    return firstNonTerminals.get(symbol);
  }

  public Set<TerminalSymbol> getFollow(NonTerminalSymbol symbol) {
    return followingTerminals.get(symbol);
  }

  public Rule getStart() {
    return this.rules.get(0);
  }

  public TerminalSymbol getEof() {
    return eof;
  }
}
