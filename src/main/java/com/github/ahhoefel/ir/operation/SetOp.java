package com.github.ahhoefel.ir.operation;

import com.github.ahhoefel.interpreter.Context;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Register;

public class SetOp implements Operation {

  private Register input;
  private Register destination;
  private int inputOffsetBits;
  private int outputOffsetBits;
  private int lenBits;

  public SetOp(Register input, Register destination, int inputOffsetBits, int outputOffsetBits, int lenBits) {
    this.input = input;
    this.destination = destination;
    this.inputOffsetBits = inputOffsetBits;
    this.outputOffsetBits = outputOffsetBits;
    this.lenBits = lenBits;
  }

  public String toString() {
    return String.format("SET  %s -> %s, input(%d width, %d offset), output(%d width, %d offset), %d len", input, destination, input.getWidth(), inputOffsetBits, destination.getWidth(), outputOffsetBits, lenBits);
  }

  @Override
  public void run(Context context) {
    context.copyToRegister(destination, context.getRegister(input), inputOffsetBits, outputOffsetBits, lenBits);
  }
}
