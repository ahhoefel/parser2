package com.github.ahhoefel;

import java.util.*;

public class LRItem {

  Set<MarkedRule> rules;
  Map<Symbol, LRItem> next;
  Map<Symbol, Integer> nextIndex;
  int index;
  int lookAhead;

  public LRItem(Set<MarkedRule> rule, Grammar g, Grammar.FirstSymbols firsts, int lookAhead) {
    this.lookAhead = lookAhead;
    this.rules = closure(rule, g, firsts);
    this.next = new HashMap<>();
    this.nextIndex = new HashMap<>();
  }

  public static Set<MarkedRule> closure(MarkedRule start, Grammar grammar, Grammar.FirstSymbols first) {
    Set<MarkedRule> seed = new HashSet<>();
    seed.add(start);
    return closure(seed, grammar, first);
  }

  public static Set<MarkedRule> closure(Set<MarkedRule> seed, Grammar grammar, Grammar.FirstSymbols first) {
    Set<MarkedRule> markedRules = new HashSet<>();
    List<MarkedRule> toVisit = new ArrayList<>();
    markedRules.addAll(seed);
    toVisit.addAll(seed);
    while (!toVisit.isEmpty()) {
      MarkedRule markedRule = toVisit.remove(toVisit.size() - 1);
      Optional<Symbol> symbol = markedRule.getSymbolAtIndex();
      if (!symbol.isPresent() || grammar.isTerminal(symbol.get())) {
        continue;
      }
      Set<Symbol> lookAheads = firstBetaA(markedRule, grammar, first);
      List<Rule> nextRules = grammar.get(symbol.get());
      for (Rule nextRule : nextRules) {
        for (Symbol lookAhead : lookAheads) {
          MarkedRule nextMarkedRule = new MarkedRule(nextRule, 0, lookAhead);
          if (markedRules.contains(nextMarkedRule)) {
            continue;
          }
          markedRules.add(nextMarkedRule);
          toVisit.add(nextMarkedRule);
        }
      }
    }
    return markedRules;
  }

  private static Set<Symbol> firstBetaA(MarkedRule markedRule, Grammar grammar, Grammar.FirstSymbols first) {
    Set<Symbol> lookAheads = new HashSet<>();
    List<Symbol> markedRuleSymbols = markedRule.getRule().getSymbols();
    int i = markedRule.getIndex() + 1;
    while (i < markedRuleSymbols.size()) {
      Symbol betai = markedRuleSymbols.get(i);
      if (grammar.isTerminal(betai)) {
        lookAheads.add(betai);
        break;
      }
      lookAheads.addAll(first.getFirstTerminals(betai));
      if (!first.getEpsilons().contains(betai)) {
        break;
      }
      i++;
    }
    if (i == markedRuleSymbols.size()) {
      lookAheads.add(markedRule.getLookAhead());
    }
    //System.out.println("firstBetaA" + markedRule + ": " + lookAheads);
    return lookAheads;
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("LRItem: ").append(index).append('\n');
    out.append("Marked Rules:\n");
    for (MarkedRule rule : rules) {
      out.append(rule);
      out.append("\n");
    }
    out.append("Next indices:\n");
    for (Map.Entry<Symbol, Integer> entry : nextIndex.entrySet()) {
      out.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
    }
    return out.toString();
  }

  public boolean equals(Object o) {
    if (!(o instanceof LRItem)) {
      return false;
    }
    return rules.equals(((LRItem) o).rules);
  }

  public int hashCode() {
    return rules.hashCode();
  }

  public LRTable.State toState(Grammar grammar, Grammar.FollowingSymbols followingSymbols) {
    Map<Symbol, Rule> reduce = new HashMap<>();
    Map<Symbol, Integer> shift = new HashMap<>();
    Map<Symbol, Integer> state = new HashMap<>();

    for (MarkedRule rule : this.rules) {
      if (rule.getIndex() != rule.getRule().getSymbols().size()) {
        continue;
      }
      if (rule.getRule() == grammar.getAugmentedStartRule()) {
        shift.put(grammar.getTerminals().getEof(), -1);
        continue;
      }
      if (reduce.containsKey(rule.getLookAhead())) {
        throw new RuntimeException("Reduce error constructing LR table.");
      }
      reduce.put(rule.getLookAhead(), rule.getRule());
    }

    for (Map.Entry<Symbol, Integer> entry : nextIndex.entrySet()) {
      if (grammar.isTerminal(entry.getKey())) {
        Symbol terminal = entry.getKey();
        if (shift.containsKey(terminal)) {
          throw new RuntimeException("Shift error constructing LR table.");
        }
        if (reduce.containsKey(terminal)) {
          StringBuilder error = new StringBuilder();
          error.append("Shift/Reduce error while constructing LR table:\n");
          error.append(this);
          error.append("\n");
          error.append("Terminal: ");
          error.append(terminal);
          error.append("\n");
          error.append(String.format("Shift to state %d and consume terminal\n", entry.getValue()));
          error.append("Reduce " + reduce.get(terminal) + "\n");
          error.append("Defaulting to shift\n");
          System.out.println(error);
          reduce.remove(terminal);
          //throw new RuntimeException(error.toString());
        }
        shift.put(terminal, entry.getValue());
      } else {
        Symbol nonTerminal = entry.getKey();
        if (state.containsKey(nonTerminal)) {
          throw new RuntimeException("State error constructing LR table.");
        }
        state.put(nonTerminal, entry.getValue());
      }
    }

    return new LRTable.State(reduce, shift, state);
  }
}
