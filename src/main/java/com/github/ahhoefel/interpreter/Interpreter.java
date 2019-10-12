package com.github.ahhoefel.interpreter;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.FileTree;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Representation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Interpreter {
  public static final Path SOURCE_DIR = Paths.get("/Users/hoefel/IdeaProjects/parser2/src/main");
  public static final Path TEST_DIR = Paths.get("/Users/hoefel/IdeaProjects/parser2/src/tests");

  public static void main(String[] args) throws IOException {
    runTarget(args[0]);
  }

  public static void runTarget(String targetString) throws IOException {
    Target target = new Target(SOURCE_DIR, targetString);
    FileTree.Result result = FileTree.fromTarget(target);

    if (!result.pass()) {
      System.out.println(result.getLog());
      if (!result.getLog().equals(ErrorLog.readErrors(target))) {
        System.out.println("Incorrect errors.");
      } else {
        System.out.println("PASS!");
      }
      return;
    }
    FileTree tree = result.getTree();
    Representation rep = tree.representation(target);
    System.out.println(rep);
    runRepresentation(rep);
  }

  public static boolean testTarget(String targetString) {
    Target target = new Target(TEST_DIR, targetString);
    try {
      FileTree.Result result = FileTree.fromTarget(target);
      ErrorLog expectedError = ErrorLog.readErrors(target);
      if (result.pass()) {
        if (!expectedError.isEmpty()) {
          System.out.println("FAIL: expected error: " + expectedError);
          return false;
        }
        return testExecution(target, result.getTree());
      }
      System.out.println(result.getLog());
      boolean pass = Objects.equals(result.getLog(), expectedError);
      if (!pass) {
        if (expectedError.isEmpty()) {
          System.out.println("FAIL: unexpected error.");
        } else {
          System.out.println("FAIL: expected " + expectedError);
        }
        return false;
      }
      return pass;
    } catch (IOException e) {
      System.out.println(e);
      return false;
    }
  }

  private static boolean testExecution(Target target, FileTree tree) throws IOException {
    Representation rep = tree.representation(target);
    //System.out.println(rep);
    Context cxt = runRepresentation(rep);
    if (cxt.getStopType().width() != 1) {
      System.out.println("FAIL: expected boolean result");
      return false;
    }
    if (cxt.getStopResult().getWord(0) == 0) {
      System.out.println("FAIL: returned false");
    }
    return cxt.getStopResult().getWord(0) != 0;
  }

  private static Context runRepresentation(Representation rep) throws IOException {
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
      try {
        op.run(context);
      } catch (Exception e) {
        System.out.println("Exception at operation " + context.getIndex());
        System.out.println(op);
        throw e;
      }
      context.incrementIndex();
    }
    if (context.getStopMessage().isPresent()) {
      System.out.println(context.getStopMessage().get());
    } else {
      System.out.println("Result: " + context.getStopResult());
      System.out.println("Result type: " + context.getStopType());
    }
    return context;
  }
}
