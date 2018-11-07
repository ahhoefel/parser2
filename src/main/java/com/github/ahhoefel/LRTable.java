package com.github.ahhoefel;

import java.util.List;
import java.util.Map;

public class LRTable<L> {
  public List<State<L>> state;

  public LRTable(List<State<L>> state) {
    this.state = state;
  }

  public static class State<L> {
    public Map<TerminalSymbol<L>, Rule> reduce;
    public Map<TerminalSymbol<L>, Integer> shift;
    public Map<NonTerminalSymbol, Integer> state;

    public State(Map<TerminalSymbol<L>, Rule> reduce, Map<TerminalSymbol<L>, Integer> shift, Map<NonTerminalSymbol, Integer> state) {
      this.reduce = reduce;
      this.shift = shift;
      this.state = state;
    }

    public String toString() {
      StringBuilder out = new StringBuilder();
      out.append("Reduce: ").append(reduce).append('\n');
      out.append("Shift: ").append(shift).append('\n');
      out.append("State: ").append(state).append('\n');
      return out.toString();
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("LRTable\n");
    for (State<L> state : this.state) {
      out.append(state).append("\n");
    }
    return out.toString();
  }
}
