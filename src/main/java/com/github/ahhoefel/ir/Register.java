package com.github.ahhoefel.ir;

public class Register {
  private static int nextId = 0;
  private int id;

  public Register() {
    this.id = nextId;
    nextId++;
  }

  public String toString() {
    return "R" + Integer.toString(id);
  }
}
