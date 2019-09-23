package com.github.ahhoefel.ir;

import com.github.ahhoefel.interpreter.Alloc;

public class Register {
  private static int nextId = 0;
  private int id;
  private int width;

  public Register() {
    this(64);
  }

  public Register(int width) {
    this.id = nextId;
    nextId++;
    this.width = width;
  }

  public Alloc createAlloc() {
    return new Alloc(width);
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getWidth() {
    return width;
  }

  public String toString() {
    return "R" + Integer.toString(id);
  }
}
