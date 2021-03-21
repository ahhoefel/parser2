package com.github.ahhoefel.interpreter;

import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Register;

import java.util.*;

public class Context {

  private Optional<String> stopMessage;
  private Alloc stopResult;
  private Type stopType;
  private Map<Register, Alloc> registers;
  private List<Alloc> stack;
  private int stackDepth = 0;
  private long index;
  private boolean stopped = false;

  public Context() {
    registers = new HashMap<>();
    stack = new ArrayList<>();
    stopMessage = Optional.empty();
  }

  public Alloc getRegister(Register r) {
    if (!registers.containsKey(r)) {
      registers.put(r, r.createAlloc());
    }
    Alloc value = registers.get(r);
    // System.out.print(String.format("get %d; ", value));
    return value;
  }

  public void copyToRegister(Register r, Alloc value) {
    Alloc alloc = registers.get(r);
    if (alloc == null) {
      alloc = r.createAlloc();
      registers.put(r, alloc);
    }
    alloc.copyFrom(value);
    // System.out.println(r + " " + alloc);
  }

  public void copyToRegister(Register r, Alloc value, int inputOffsetBits, int outputOffsetBits, int lenBits) {
    Alloc alloc = registers.get(r);
    if (alloc == null) {
      alloc = r.createAlloc();
      registers.put(r, alloc);
    }
    alloc.copyFrom(value, inputOffsetBits, outputOffsetBits, lenBits);
  }

  public void push(Register r) {
    Alloc value = getRegister(r);
    stack.add(value.copy());
    // System.out.print(String.format("push %d; ", value));
  }

  public void pop(Register r) {
    Alloc value = stack.remove(stack.size() - 1);
    copyToRegister(r, value);
    // System.out.print(String.format("pop %d; remaining stack: %s;\n", value,
    // stack));
  }

  public void setIndex(long index) {
    this.index = index;
  }

  public long getIndex() {
    return index;
  }

  public void incrementIndex() {
    index++;
  }

  public boolean isStopped() {
    return stopped;
  }

  public void stop(String message) {
    stopMessage = Optional.of(message);
    stopped = true;
  }

  public void stop(Register result, Type type) {
    stopResult = registers.get(result);
    stopType = type;
    stopped = true;
  }

  public Optional<String> getStopMessage() {
    return stopMessage;
  }

  public Alloc getStopResult() {
    return stopResult;
  }

  public Type getStopType() {
    return stopType;
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
