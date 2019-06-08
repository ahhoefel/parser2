package com.github.ahhoefel.parser;

import java.util.List;
import java.util.Map;

public class LRTable {
  public List<State> state;

  public LRTable(List<State> state) {
    this.state = state;
  }

  public static class State {
    Map<Symbol, Rule> reduce;
    Map<Symbol, Integer> shift;
    Map<Symbol, Integer> state;

    public State(Map<Symbol, Rule> reduce, Map<Symbol, Integer> shift, Map<Symbol, Integer> state) {
      this.reduce = reduce;
      this.shift = shift;
      this.state = state;
    }

    public String toString() {
      return "Reduce: " + reduce + '\n' +
          "Shift: " + shift + '\n' +
          "State: " + state + '\n';
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("LRTable\n");
    int i = 0;
    for (State state : this.state) {
      out.append(i).append(":\n").append(state).append("\n");
      i++;
    }
    return out.toString();
  }
}
