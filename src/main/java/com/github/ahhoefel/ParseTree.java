package com.github.ahhoefel;

import java.util.List;

public class ParseTree<T> {
  private List<ParseTree> children;
  private Rule rule;
  private T token;

  public ParseTree(Rule rule, List<ParseTree> children) {
    this.rule = rule;
    this.children = children;
  }

  public ParseTree(T token) {
    this.token = token;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    toString(buf, "");
    return buf.toString();
  }

  public void toString(StringBuffer buf, String indent) {
    if (token != null) {
      buf.append(indent).append(token.toString()).append('\n');
      return;
    }
    String t = indent + "  ";
    buf.append(indent).append("Rule: ").append(rule.toString()).append('\n');
    for (ParseTree child : children) {
      buf.append(indent).append("Child: {\n");
      child.toString(buf, t);
      buf.append(indent).append("}\n");
    }
  }
}
