package com.github.ahhoefel.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ShiftReduceResolver {

  private Map<Pair, Preference> prefs = new HashMap<>();

  public ShiftReduceResolver() {
  }

  public void addShiftPreference(Rule rule, Symbol terminal) {
    prefs.put(new Pair(rule, terminal), Preference.SHIFT);
  }

  public void addReducePreference(Rule rule, Symbol terminal) {
    prefs.put(new Pair(rule, terminal), Preference.REDUCE);
  }

  public Optional<Preference> getPreference(Rule rule, Symbol terminal) {
    return Optional.ofNullable(prefs.get(new Pair(rule, terminal)));
  }

  public String toString() {
    String out = "ShiftReduceResolver:\n";
    for (Map.Entry<Pair, Preference> e : prefs.entrySet()) {
      out += e.getKey().toString() + ": " + e.getValue() + "\n";
    }
    return out;
  }

  public enum Preference {
    SHIFT,
    REDUCE
  }

  private static class Pair {
    private Rule rule;
    private Symbol terminal;

    public Pair(Rule rule, Symbol terminal) {
      this.rule = rule;
      this.terminal = terminal;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Pair)) {
        return false;
      }
      Pair p = (Pair) o;
      return Objects.equals(rule, p.rule) && Objects.equals(terminal, p.terminal);
    }

    @Override
    public int hashCode() {
      return rule.hashCode() + 31 * terminal.hashCode();
    }

    public String toString() {
      return String.format("Rule: %s, Terminal: %s", rule, terminal);
    }
  }
}
