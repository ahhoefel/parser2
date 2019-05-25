package com.github.ahhoefel;

public class Precedence {

  private Rule rule;
  private Symbol shift;
  private boolean preferReduce;

  public Precedence(Rule rule, Symbol shift, boolean preferReduce) {
    this.rule = rule;
    this.shift = shift;
    this.preferReduce = preferReduce;
  }

}
