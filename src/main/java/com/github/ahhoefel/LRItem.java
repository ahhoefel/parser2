package com.github.ahhoefel;

import java.util.*;

public class LRItem {

  private Set<MarkedRule> rules;
  private Map<Symbol, LRItem> next;
  private Map<Symbol, Integer> nextIndex;
  private int index;

  public LRItem(Set<MarkedRule> rules) {
    this.rules = rules;
    this.next = new HashMap<>();
    this.nextIndex = new HashMap<>();
  }

  public static Set<MarkedRule> closure(MarkedRule start, Rules rules) {
    Set<MarkedRule> seed = new HashSet<>();
    seed.add(start);
    return closure(seed, rules);
  }

  public static Set<MarkedRule> closure(Set<MarkedRule> seed, Rules rules) {
    Set<MarkedRule> markedRules = new HashSet<>();
    List<MarkedRule> toVisit = new ArrayList<>();
    markedRules.addAll(seed);
    toVisit.addAll(markedRules);
    while (!toVisit.isEmpty()) {
      MarkedRule markedRule = toVisit.remove(toVisit.size()-1);
      Optional<Symbol> symbol = markedRule.getSymbolAtIndex();
      if (symbol.isPresent() && !symbol.get().isTerminal()) {
        List<Rule> nextRules = rules.getRulesForNonTerminal((NonTerminalSymbol) symbol.get());
        for (Rule nextRule : nextRules) {
          MarkedRule nextMarkedRule = new MarkedRule(nextRule, 0);
          if (!markedRules.contains(nextMarkedRule)) {
            markedRules.add(nextMarkedRule);
            toVisit.add(nextMarkedRule);
          }
        }
      }
    }
    return markedRules;
  }

  public static LRParser makeItemGraph(Rules rules) {
    Rule start = rules.getStart();
    MarkedRule markedStart = new MarkedRule(start, 0);
    LRItem startItem = new LRItem(closure(markedStart, rules));
    Map<LRItem, Integer> itemMap = new HashMap<>();
    List<LRItem> items = new ArrayList<>();
    List<LRItem> queue = new ArrayList<>();
    queue.add(startItem);
    items.add(startItem);
    while (!queue.isEmpty()) {
      LRItem item = queue.remove(queue.size() -1);
      Map<Symbol, Set<MarkedRule>> nexts = new HashMap<>();
      for (MarkedRule rule : item.rules) {
        Optional<Symbol> optSymbol = rule.getSymbolAtIndex();
        if (!optSymbol.isPresent()) {
          continue;
        }
        Symbol symbol = optSymbol.get();
        MarkedRule nextRule = new MarkedRule(rule.getRule(), rule.getIndex() + 1);
        if (nexts.containsKey(symbol)) {
          nexts.get(symbol).add(nextRule);
        } else {
          Set<MarkedRule> n = new HashSet();
          n.add(nextRule);
          nexts.put(symbol, n);
        }
      }

      for (Map.Entry<Symbol, Set<MarkedRule>> entry : nexts.entrySet()) {
        LRItem nextItem = new LRItem(closure(entry.getValue(), rules));
        int nextItemIndex = 0;
        if (itemMap.containsKey(nextItem)) {
          nextItemIndex = itemMap.get(nextItem);
          nextItem = items.get(nextItemIndex);
        } else {
          nextItemIndex = items.size();
          items.add(nextItem);
          queue.add(nextItem);
          itemMap.put(nextItem, nextItemIndex);
        }
        item.next.put(entry.getKey(), nextItem);
        item.nextIndex.put(entry.getKey(), nextItemIndex);
        nextItem.index = nextItemIndex;
      }
    }
    return new LRParser(startItem, items);
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

  public LRTable.State<String> toState(Rules rules) {
    Map<TerminalSymbol<String>, Rule> reduce = new HashMap<>();
    Map<TerminalSymbol<String>, Integer> shift = new HashMap<>();
    Map<NonTerminalSymbol, Integer> state = new HashMap<>();

    for (MarkedRule rule : this.rules) {
      if (rule.getIndex() == rule.getRule().getSymbols().size()) {
        if (rule.getRule() == rules.getStart()) {
          shift.put(rules.getEof(), -1);
        } else {
          for (TerminalSymbol terminal : rules.getFollow(rule.getRule().getSource())) {
            if (reduce.containsKey(terminal)) {
              throw new RuntimeException("Reduce error constructing LR(0) table.");
            }
            reduce.put(terminal, rule.getRule());
          }
        }
      }
    }

    for (Map.Entry<Symbol, Integer> entry : nextIndex.entrySet()) {
      if (entry.getKey().isTerminal()) {
        TerminalSymbol terminal = (TerminalSymbol) entry.getKey();
        if (shift.containsKey(terminal)) {
          throw new RuntimeException("Shift error constructing LR(0) table.");
        }
        shift.put(terminal, entry.getValue());
      } else {
        NonTerminalSymbol nonTerminal = (NonTerminalSymbol) entry.getKey() ;
        if (state.containsKey(nonTerminal)) {
          throw new RuntimeException("State error constructing LR(0) table.");
        }
        state.put(nonTerminal, entry.getValue());
      }
    }

    return new LRTable.State(reduce, shift, state);
  }
}
