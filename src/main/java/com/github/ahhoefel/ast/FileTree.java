package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.rules.Language;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FileTree {

  public String base;
  public Map<String, RaeFile> files;

  public FileTree(String base) {
    this.base = base;
    files = new HashMap<>();
  }

  public Representation representation(Target target) {
    Representation rep = new Representation();
    RaeFile main = files.get(target.getSuffix());
    if (main == null) {
      throw new RuntimeException("Target not in tree: " + target);
    }
    if (main.getSymbols().getFunction("main") != null) {
      Label stopLabel = new Label();
      Register stopLabelRegister = new Register();
      rep.add(new LiteralLabelOp(stopLabel, stopLabelRegister));
      rep.add(new PushOp(stopLabelRegister));
      rep.add(new GotoOp(main.getSymbols().getFunction("main").getLabel()));
      rep.add(new DestinationOp(stopLabel));
      rep.add(new StopOp("Ended execution."));
    } else {
      rep.add(new StopOp("No main."));
    }
    for (RaeFile file : files.values()) {
      file.addToRepresentation(rep);
    }
    return rep;
  }

  public static FileTree fromTarget(Target target) throws IOException {
    String base = target.getBase();
    FileTree tree = new FileTree(base);
    Language lang = new Language();
    Stack<String> paths = new Stack<>();
    paths.push(target.getSuffix());
    while (!paths.isEmpty()) {
      String p = paths.pop();
      Target t = new Target(base, p);
      System.out.println("Reading target " + t.getFilePath());
      FileReader reader = new FileReader(t.getFilePath());
      RaeFile file = lang.parse(reader);
      file.setTarget(t);
      tree.files.put(p, file);
      if (file == null) {
        System.out.println("Unable to parse file " + t.getFilePath());
      }
      for (Import im : file.getImports().toList()) {
        String next = im.getPath();
        if (!tree.files.containsKey(next)) {
          paths.add(next);
          tree.files.put(next, null);
        }
      }
      System.out.println("Symbols: " + file.getSymbols().toString());
    }
    tree.linkImports();
    tree.linkSymbols();
    tree.typeCheck();
    return tree;
  }

  private void linkImports() {
    for (RaeFile file : this.files.values()) {
      file.linkImports(new TargetMap());
    }
  }

  private void linkSymbols() {
    for (Map.Entry<String, RaeFile> entry : this.files.entrySet()) {
      System.out.println("Linking symbols: " + entry.getKey());
      entry.getValue().linkSymbols();
    }
    for (Map.Entry<String, RaeFile> entry : this.files.entrySet()) {
      System.out.println("Symbols of: " + entry.getKey());
      System.out.println(entry.getValue().getSymbols());
    }
  }

  private void typeCheck() {
    System.out.println("Type checking tree.");
    for (RaeFile file : this.files.values()) {
      System.out.println("  " + file.getTarget().toString());
      file.typeCheck();
    }
  }

  public class TargetMap {
    public RaeFile get(String p) {
      return files.get(p);
    }
  }
}
