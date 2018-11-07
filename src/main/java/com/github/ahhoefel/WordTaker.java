package com.github.ahhoefel;

import java.util.AbstractList;
import java.util.List;

public class WordTaker<T> implements TokenTaker<T> {

  public static WordTaker<Character> newStringTaker(String word) {
     return new WordTaker<Character>(
        new AbstractList<Character>() {
          public int size() { return word.length(); }
          public Character get(int index) { return word.charAt(index); }
        });
  }

  private List<T> tokens;
  private int index;
  private boolean error;

  public WordTaker(List<T> tokens) {
    this.tokens = tokens;
    this.index = 0;
    this.error = false;
  }

  public void add(T t) {
    if (error || t == null || index == tokens.size()) {
      error = true;
      return;
    }
    if (!t.equals(tokens.get(index))) {
      error = true;
      return;
    }
    index++;
  }

  public State getState() {
    if (error) return TokenTaker.State.ERROR;
    if (index != tokens.size()) return TokenTaker.State.ACCEPTING;
    return TokenTaker.State.MATCH;
  }

  public void reset() {
    index = 0;
    error = false;
  }
}
