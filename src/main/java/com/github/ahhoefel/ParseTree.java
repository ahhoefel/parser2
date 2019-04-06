package com.github.ahhoefel;

import java.util.List;
import java.util.Objects;

public class ParseTree {
  private List<ParseTree> children;
  private Rule rule;
  private Token token;

  public ParseTree(Rule rule, List<ParseTree> children) {
    this.rule = rule;
    this.children = children;
  }

  public ParseTree(Token token) {
    this.token = token;
  }

  public Rule getRule() {
    return rule;
  }

  public List<ParseTree> getChildren() {
    return children;
  }

  public Token getToken() {
    return token;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    toString(buf, "");
    return buf.toString();
  }

  public boolean equals(Object o) {
    if (!(o instanceof ParseTree)) {
      return false;
    }
    ParseTree t = (ParseTree) o;
    return Objects.equals(t.token, this.token) && Objects.equals(t.children, this.children) && Objects.equals(t.rule, this.rule);
  }

  /*
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
  */
  public void toString(StringBuffer buf, String indent) {
    if (token != null) {
      buf.append(indent).append("Token: ").append(token.toString()).append('\n');
      return;
    }
    buf.append(indent).append("Rule: ").append(rule.toString()).append('\n');
    int i = 0;
    for (ParseTree child : children) {
      child.toString(buf, indent + i + ". ");
      i++;
    }
  }

  public String getText() {
    StringBuffer buf = new StringBuffer();
    appendText(buf);
    return buf.toString();
  }

  private void appendText(StringBuffer buf) {
    if (token != null) {
      buf.append(token.getValue());
      return;
    }
    for (ParseTree child : children) {
      child.appendText(buf);
    }
  }
}
