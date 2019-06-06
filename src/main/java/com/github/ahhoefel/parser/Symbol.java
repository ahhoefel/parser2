package com.github.ahhoefel.parser;

public class Symbol {
  private String label;
  private int index;

  public Symbol(String label, int index) {
    this.label = label;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public String toString() {
    return label;
  }

  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public int hashCode() {
    return index;
  }
}
