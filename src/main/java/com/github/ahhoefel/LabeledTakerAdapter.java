package com.github.ahhoefel;

public class LabeledTakerAdapter<T,L> implements LabeledTaker<T, L> {

  private L label;
  private TokenTaker<T> taker;

  public LabeledTakerAdapter(L label, TokenTaker<T> taker) {
    this.label = label;
    this.taker = taker;
  }

  public void add(T t) {
    taker.add(t);
  }

  public State getState() { 
    return taker.getState();
  }

  public void reset() {
    taker.reset();
  }

  public L getLabel() {
    return label;
  }
}

