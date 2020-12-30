package com.github.ahhoefel.interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestHarness {
  private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/tests/";

  public static void main(String[] args) throws IOException {
    boolean pass = Files.walk(Paths.get(BASE_PATH)).filter(Files::isRegularFile)
        .filter(f -> f.toString().endsWith(".ro")).map(f -> {
          String s = f.toString();
          s = s.substring(BASE_PATH.length());
          s = s.substring(0, s.length() - 3);
          int i = s.lastIndexOf("/");
          return s.substring(0, i) + ":" + s.substring(i + 1);
        })
        // .forEach(System.out::println);
        .allMatch(targetString -> Interpreter.testTarget(targetString));
    if (pass) {
      System.out.println("PASSED TESTS");
    } else {
      System.out.println("FAILED TESTS");
    }
  }

}
