package com.github.ahhoefel;

public abstract class AbstractSymbol {
  private String label;
  private int index;

  public AbstractSymbol(String label, int index) {
    this.label = label;
    this.index = index;
  }

  public String getLabel() {
    return this.label;
  }

  public int getIndex() {
    return this.index;
  }

  @Override
  public String toString() {
    return this.label;
  }

  @Override
  public abstract boolean equals(Object o);

  @Override
  public int hashCode() {
    return index;
  }
}
