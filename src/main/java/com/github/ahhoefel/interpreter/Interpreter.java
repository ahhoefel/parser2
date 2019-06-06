package com.github.ahhoefel.interpreter;

import com.github.ahhoefel.ast.FileTree;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Representation;

import java.io.IOException;

public class Interpreter {

  public static void main(String[] args) throws IOException {
    runTarget(args[0]);
  }

  private static void runRepresentation(Representation rep) throws IOException {
    System.out.println("Starting execution.");
    Context context = new Context();
    while (!context.isStopped()) {
      if (context.getIndex() >= rep.size()) {
        System.out.println("Ran off the end of the code.");
        break;
      }
      Operation op = rep.getOperation(context.getIndex());
      //int ch = System.in.read();
      //System.out.println(op);
      //System.out.println(context);
      op.run(context);
      context.incrementIndex();
    }
    System.out.println(context.getStopMessage().get());
  }

  public static void runTarget(String targetString) throws IOException {
    Target target = new Target(targetString);
    FileTree tree = FileTree.fromTarget(target);
    Representation rep = tree.representation(target);
    System.out.println(rep);
    runRepresentation(rep);
  }
}
