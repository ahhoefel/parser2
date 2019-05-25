package com.github.ahhoefel.ir;

import java.util.Optional;

public class Label {

  public Optional<Integer> index;

  public Label() {
    index = Optional.empty();
  }

  public void setIndex(int index) {
    this.index = Optional.of(index);
  }

  public boolean hasIndex() {
    return index.isPresent();
  }

  public int getIndex() {
    return index.get();
  }

  public String toString() {
    if (index.isPresent()) {
      return "L" + Integer.toString(index.get());
    } else {
      return "n/a";
    }
  }
}
