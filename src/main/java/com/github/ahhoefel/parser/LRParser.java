package com.github.ahhoefel.parser;

import java.util.*;

public class LRParser {

  public static LRTable getSLRTable(Grammar g) {
    Grammar.FirstSymbols first = g.first();
    List<LRItem> items = getLRItems(g, first, 0);
    List<LRTable.State> states = new ArrayList<>();
    for (LRItem item : items) {
      states.add(item.toState(g, new ShiftReduceResolver()));
    }
    return new LRTable(states);
  }

  public static LRTable getCanonicalLRTable(Grammar g) {
    return getCanonicalLRTable(g, new ShiftReduceResolver());
  }

  public static LRTable getCanonicalLRTable(Grammar g, ShiftReduceResolver r) {
    Grammar.FirstSymbols first = g.first();
    List<LRItem> items = getLRItems(g, first, 1);
    List<LRTable.State> states = new ArrayList<>();
    for (LRItem item : items) {
      states.add(item.toState(g, r));
    }
    return new LRTable(states);
  }

  private static List<LRItem> getLRItems(Grammar g, Grammar.FirstSymbols first, int lookAhead) {
    Rule start = g.getAugmentedStartRule();
    MarkedRule markedStart = new MarkedRule(start, 0, g.getTerminals().getEof());
    LRItem startItem = new LRItem(Set.of(markedStart), g, first, lookAhead);
    Map<LRItem, Integer> itemMap = new HashMap<>();
    List<LRItem> items = new ArrayList<>();
    List<LRItem> queue = new ArrayList<>();
    queue.add(startItem);
    items.add(startItem);
    while (!queue.isEmpty()) {
      LRItem item = queue.remove(queue.size() - 1);
      Map<Symbol, Set<MarkedRule>> nexts = gotos(item);
      for (Map.Entry<Symbol, Set<MarkedRule>> entry : nexts.entrySet()) {
        LRItem nextItem = new LRItem(entry.getValue(), g, first, lookAhead);
        int nextItemIndex;
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
    return items;
  }

  private static Map<Symbol, Set<MarkedRule>> gotos(LRItem item) {
    Map<Symbol, Set<MarkedRule>> nexts = new HashMap<>();
    for (MarkedRule rule : item.rules) {
      Optional<Symbol> optSymbol = rule.getSymbolAtIndex();
      if (!optSymbol.isPresent()) {
        continue;
      }
      Symbol symbol = optSymbol.get();
      MarkedRule nextRule = new MarkedRule(rule.getRule(), rule.getIndex() + 1, rule.getLookAhead());
      if (nexts.containsKey(symbol)) {
        nexts.get(symbol).add(nextRule);
      } else {
        Set<MarkedRule> n = new HashSet<>();
        n.add(nextRule);
        nexts.put(symbol, n);
      }
    }
    return nexts;
  }
}
