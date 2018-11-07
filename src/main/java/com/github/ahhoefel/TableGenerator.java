package com.github.ahhoefel;

import java.util.*;

public class TableGenerator {

  private static class Item<L> {
    private Rule rule;
    private int index;
    private Set<TerminalSymbol<L>> lookAhead;
    public Item(Rule rule, int index, Set<TerminalSymbol<L>> lookAhead) {
      this.rule = rule;
      this.index = index;
      this.lookAhead = lookAhead;
    }
  }

  public <L> LRTable<L> generate(List<Rule> rules) {
     if (rules.isEmpty()) {
       return null;
     }
     Map<NonTerminalSymbol, List<Rule>> ruleMap = toMap(rules);
     Rule start = rules.get(0);
     Item<L> startItem = new Item(start, 0, first(start, ruleMap));
     //Set<Item<L>> startSet = closure(startItem, ruleMap);
    return null;
  }

  private Map<NonTerminalSymbol, List<Rule>> toMap(List<Rule> rules) {
    Map<NonTerminalSymbol, List<Rule>> map = new HashMap<>();
    for (Rule rule : rules) {
      if (!map.containsKey(rule.getSource())) {
        map.put(rule.getSource(), new ArrayList<Rule>());
      }
      map.get(rule.getSource()).add(rule);
    }
    return map;
  }

  private Set<TerminalSymbol> first(Rule start, Map<NonTerminalSymbol, List<Rule>> ruleMap) {

    return null;
  }

}
