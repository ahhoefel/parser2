package com.github.ahhoefel;

import java.util.Optional;

public class MarkedRule {
  private Rule rule;
  private int index;
  private Symbol lookAhead;

  public MarkedRule(Rule rule, int index, Symbol lookAhead) {
    this.rule = rule;
    this.index = index;
    this.lookAhead = lookAhead;
  }

  public Rule getRule() {
    return rule;
  }

  public int getIndex() {
    return index;
  }

  public Symbol getLookAhead() {
    return lookAhead;
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
    return other.rule.equals(rule) && other.index == index && lookAhead.equals(other.lookAhead);
  }

  @Override
  public int hashCode() {
    return 31 * rule.hashCode() + index + 31 * 31 * (lookAhead == null ? 0 : lookAhead.hashCode());
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append('[');
    buf.append(rule.getSource());
    buf.append(" => ");
    for (int i = 0; i < rule.getSymbols().size(); i++) {
      if (i == index) {
        buf.append("^ ");
      }
      buf.append(rule.getSymbols().get(i));
      buf.append(' ');
    }
    if (index == rule.getSymbols().size()) {
      buf.append('^');
    }
    buf.append(", ");
    buf.append(lookAhead);
    buf.append(']');
    return buf.toString();
  }
}
