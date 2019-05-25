package com.github.ahhoefel.interpreter;

import com.github.ahhoefel.ir.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {

  private Map<Register, Integer> registers;
  private List<Integer> stack;
  private int index;

  public Context() {
    registers = new HashMap<>();
    stack = new ArrayList<>();
  }

  public Integer getRegister(Register r) {
    return registers.get(r);
  }

  public void putRegister(Register r, int value) {
    registers.put(r, value);
    System.out.println(String.format("%s <- %d", r, value));
  }

  public void push(Register r) {
    int value = getRegister(r);
    stack.add(value);
    System.out.println(String.format("push %d <- %s", value, r));
  }

  public void pop(Register r) {
    int value = stack.remove(stack.size() - 1);
    putRegister(r, value);
    System.out.println(String.format("pop  %s <- %d", r, value));
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public void incrementIndex() {
    index++;
  }
}
