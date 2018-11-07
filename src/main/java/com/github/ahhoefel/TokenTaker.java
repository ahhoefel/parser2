package com.github.ahhoefel;

public interface TokenTaker<T> {
  public enum State {
    MATCH,
    ACCEPTING,
    ERROR
  }
  public void add(T t);
  public State getState();
  public void reset();
}
