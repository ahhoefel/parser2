package com.github.ahhoefel;

public interface TokenTaker<T> {
  enum State {
    MATCH,
    ACCEPTING,
    ERROR
  }

  void add(T t);

  State getState();

  void reset();
}
