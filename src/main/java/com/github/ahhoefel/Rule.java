package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Rule {
  private Symbol source;
  private List<Symbol> symbols;
  private Function<Object[], Object> action;

  public Rule(Symbol source, List<Symbol> symbols) {
    this.source = source;
    this.symbols = symbols;
    this.action = new ParseTreeAction();
  }

  public Rule(Symbol source, List<Symbol> symbols, Function<Object[], Object> action) {
    this.source = source;
    this.symbols = symbols;
    this.action = action;
  }

  public Symbol getSource() {
    return source;
  }

  public List<Symbol> getSymbols() {
    return symbols;
  }

  public Function<Object[], Object> getAction() {
    return action;
  }

  public Rule setAction(Function<Object[], Object> action) {
    this.action = action;
    return this;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append(source.toString());
    buf.append(" ->");
    for (Symbol symbol : symbols) {
      buf.append(" ");
      buf.append(symbol.toString());
    }
    return buf.toString();
  }

  private class ParseTreeAction implements Function<Object[], Object> {
    @Override
    public Object apply(Object[] objects) {
      List<ParseTree> children = new ArrayList<>();
      for (Object o : objects) {
        if (o instanceof Token) {
          children.add(new ParseTree((Token) o));
        } else {
          children.add((ParseTree) o);
        }
      }
      return new ParseTree(Rule.this, children);
    }
  }

  public static class Builder {

    private List<Rule> rules;

    public Builder() {
      rules = new ArrayList<>();
    }

    public Rule add(Symbol from, Symbol... to) {
      Rule r = new Rule(from, Arrays.asList(to));
      rules.add(r);
      return r;
    }

    public List<Rule> build() {
      return rules;
    }
  }
}