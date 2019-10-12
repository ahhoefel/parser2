package com.github.ahhoefel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Stack<T> {

  private List<T> elems;

  public Stack() {
    elems = new ArrayList<>();
  }

  public T pop() {
    return elems.remove(elems.size() - 1);
  }

  public void push(T t) {
    elems.add(t);
  }

  public T peek() {
    return elems.get(elems.size() - 1);
  }

  public Optional<T> deepPeek(int i) {
    if (i >= elems.size()) {
      return Optional.empty();
    }
    return Optional.of(elems.get(elems.size() - i - 1));
  }

  public boolean isEmpty() {
    return elems.isEmpty();
  }

  public String toString() {
    return elems.toString();
  }
}
