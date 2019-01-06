package com.github.ahhoefel;

import java.util.Optional;

public class MarkedRule {
  private Rule rule;
  private int index;

  public MarkedRule(Rule rule, int index) {
    this.rule = rule;
    this.index = index;
  }

  public Rule getRule() {
    return rule;
  }

  public int getIndex() {
    return index;
  }

  public Optional<Symbol> getSymbolAtIndex() {
    if (index < rule.getSymbols().size()) {
      return Optional.of(rule.getSymbols().get(index));
    }
    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MarkedRule)) {
      return false;
    }
    MarkedRule other = (MarkedRule) o;
    return other.rule.equals(rule) && other.index == index;
  }

  @Override
  public int hashCode() {
    return 31 * rule.hashCode() + index;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append(rule.getSource());
    buf.append("=>");
    for (int i = 0; i < rule.getSymbols().size(); i++) {
      if (i == index) {
        buf.append('^');
      }
      buf.append(rule.getSymbols().get(i));
    }
    return buf.toString();
  }
}
