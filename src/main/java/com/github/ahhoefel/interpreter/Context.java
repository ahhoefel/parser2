package com.github.ahhoefel.interpreter;

import com.github.ahhoefel.ir.Register;

import java.util.*;

public class Context {

  private Optional<String> stopMessage;
  private Map<Register, Integer> registers;
  private List<Integer> stack;
  private int stackDepth = 0;
  private int index;

  public Context() {
    registers = new HashMap<>();
    stack = new ArrayList<>();
    stopMessage = Optional.empty();
  }

  public Integer getRegister(Register r) {
    if (!registers.containsKey(r)) {
      return 0;
    }
    Integer value = registers.get(r);
    //System.out.print(String.format("get %d; ", value));
    return value;
  }

  public void putRegister(Register r, int value) {
    registers.put(r, value);
    //System.out.print(String.format("put %d; ", value));
  }

  public void push(Register r) {
    int value = getRegister(r);
    stack.add(value);
    //System.out.print(String.format("push %d; ", value));
  }

  public void pop(Register r) {
    int value = stack.remove(stack.size() - 1);
    putRegister(r, value);
    //System.out.print(String.format("pop %d; remaining stack: %s;\n", value, stack));
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

  public boolean isStopped() {
    return stopMessage.isPresent();
  }

  public void stop(String message) {
    stopMessage = Optional.of(message);
  }

  public Optional<String> getStopMessage() {
    return stopMessage;
  }

  public void incrementStackDepth() {
    stackDepth++;
  }

  public void decrementStackDepth() {
    stackDepth--;
  }

  public String getStackIndent() {
    String out = "";
    for (int i = 0; i < stackDepth; i++) {
      out += "  ";
    }
    return out;
  }

  public String toString() {
    String out = "";
    out += "Registers: " + registers + " \n";
    out += stack + "\n";
    return out;
  }
}
