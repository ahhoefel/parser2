package com.github.ahhoefel.ir;

import com.github.ahhoefel.ir.operation.DestinationOp;

import java.util.ArrayList;
import java.util.List;

public class Representation {

  List<Operation> operations;

  public Representation() {
    operations = new ArrayList<>();
  }

  public void add(Operation op) {
    if (op instanceof DestinationOp) {
      ((DestinationOp) op).setLocation(operations.size());
    }
    operations.add(op);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Operation op : operations) {
      builder.append(op);
      builder.append('\n');
    }
    return builder.toString();
  }

  public int size() {
    return operations.size();
  }

  public Operation getOperation(int i) {
    return operations.get(i);
  }
}
