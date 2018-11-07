package com.github.ahhoefel;

import java.util.Iterator;
import java.util.Stack;

public class StackParser<T> {
  private Stack<JointParser<T>> stack;
  private Iterator<T> tokens;
  public StackParser(Iterator<T> tokens) {
    this.stack = new Stack<>();
    this.tokens = tokens;
  }

  public TokenTaker<T> pop() {
    TokenTaker<T> p = stack.peek().pop();
    if (stack.peek().isEmpty()) {
      stack.pop();
    }
    return p;
  }

  public void push(TokenTaker<T> parser, boolean fallThrough) {
    if (!fallThrough) {
      stack.push(new JointParser());
    }
    stack.peek().push(parser);
  }

  public void add(T t) {
    stack.peek().addToken(t);
  }

  private class JointParser<T> extends Stack<TokenTaker<T>> {
    public void addToken(T t) {


    }
  }
}
