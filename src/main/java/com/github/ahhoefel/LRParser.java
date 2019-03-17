package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.List;

public class LRParser {
  LRItem start;
  List<LRItem> items;

  public LRParser(LRItem start, List<LRItem> items) {
    this.start = start;
    this.items = items;
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    for (LRItem item : items) {
      out.append(item);
      out.append("\n");
    }
    return out.toString();
  }

  /*
  public LRTable getTable(Rules rules) {
    List<LRTable.State> states = new ArrayList<>();
    for (LRItem item : items) {
      states.add(item.toState(rules));
    }
    return new LRTable(states);
  }
*/

  public LRTable getTable(Grammar grammar) {
    Grammar.FollowingSymbols following = Grammar.following(grammar);
    List<LRTable.State> states = new ArrayList<>();
    for (LRItem item : items) {
      states.add(item.toState(grammar, following));
    }
    return new LRTable(states);
  }
}
