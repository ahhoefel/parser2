package com.github.ahhoefel.interpreter;

import com.github.ahhoefel.ast.RaeFile;
import com.github.ahhoefel.ir.Operation;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.rules.Language;

import java.io.FileReader;
import java.io.IOException;

public class Interpreter {

  public static void main(String[] args) throws IOException {
    readFile(args[0]);
  }
/*
  public static void interactiveTerminal() throws IOException {
    Language lang = new Language();
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    Context context = new Context();
    while (true) {
      System.out.print(" > ");
      String line = in.readLine();
      Reader reader = new CharArrayReader(line.toCharArray());
      context.push(lang.parse(reader));
      while (context.hasNextStatement()) {
        System.out.println(context.nextStatement().execute(context));
      }
    }
  }
*/

  public static void readFile(String fileName) throws IOException {
    System.out.println("Reading from \"" + fileName + "\"");
    Language lang = new Language();
    FileReader reader = new FileReader(fileName);
    Context context = new Context();
    RaeFile file = lang.parse(reader);
    Representation rep = file.representation();
    System.out.print(file);
    System.out.println(file.representation());

    System.out.println("Starting execution.");
    while (!context.isStopped()) {
      if (context.getIndex() >= rep.size()) {
        System.out.println("Ran off the end of the code.");
        break;
      }
      Operation op = rep.getOperation(context.getIndex());
      System.out.print(String.format("%d: %s # ", context.getIndex(), op));
      op.run(context);
      System.out.println();
      context.incrementIndex();
    }
  }
}
