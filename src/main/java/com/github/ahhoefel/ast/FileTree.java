package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.rules.Language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class FileTree {

  public String base;
  public Map<String, File> files;
  public Register mainFnReturnRegister;

  public FileTree(String base) {
    this.base = base;
    files = new HashMap<>();
    mainFnReturnRegister = new Register();
  }

  public Representation representation(Target target) {
    Representation rep = new Representation();
    File main = files.get(target.getSuffix());
    if (main == null) {
      throw new RuntimeException("Target not in tree: " + target);
    }
    FunctionDeclaration mainFn = main.getSymbols().getFunction("main");
    if (mainFn != null) {
      Label stopLabel = new Label();
      Register stopLabelRegister = new Register();
      rep.add(new LiteralLabelOp(stopLabel, stopLabelRegister));
      rep.add(new PushOp(stopLabelRegister));
      rep.add(new GotoOp(mainFn.getLabel()));
      rep.add(new DestinationOp(stopLabel));
      if (mainFn.getReturnType().equals(Type.VOID)) {
        rep.add(new StopOp("Ended execution. Main function void."));
      } else {
        rep.add(new PopOp(mainFnReturnRegister));
        rep.add(new StopOp(mainFnReturnRegister, mainFn.getReturnType()));
      }
      mainFnReturnRegister.setWidth(mainFn.getReturnType().width());
    } else {
      rep.add(new StopOp("No main."));
    }
    for (File file : files.values()) {
      file.addToRepresentation(rep);
    }
    return rep;
  }

  public static Result fromTarget(Target target) throws IOException {
    ErrorLog log = new ErrorLog();
    String base = target.getBase();
    FileTree tree = new FileTree(base);
    Language lang = new Language();
    Stack<String> paths = new Stack<>();
    List<Target> targets = new ArrayList<>();
    paths.push(target.getSuffix());
    while (!paths.isEmpty()) {
      String p = paths.pop();
      Target t = new Target(target.getSource(), base, p);
      File file = lang.parse(t, log);
      file.setTarget(t);
      tree.files.put(p, file);
      for (Import im : file.getImports().getImports()) {
        String next = im.getPath();
        if (!tree.files.containsKey(next)) {
          paths.add(next);
          tree.files.put(next, null);
        }
      }
      // System.out.println("Symbols: " + file.getSymbols().toString());
    }
    // This is what should be replaced with the visitor pattern.
    tree.linkImports();
    tree.linkSymbols(log);
    tree.typeCheck(log);
    if (log.isEmpty()) {
      return new Result(targets, tree);
    }
    return new Result(targets, log);
  }

  private void linkImports() {
    TargetMap map = new TargetMap();
    for (File file : this.files.values()) {
      file.linkImports(map);
    }
  }

  private void linkSymbols(ErrorLog log) {
    for (Map.Entry<String, File> entry : this.files.entrySet()) {
      entry.getValue().linkSymbols(log);
    }
  }

  private void typeCheck(ErrorLog log) {
    for (File file : this.files.values()) {
      file.typeCheck(log);
    }
  }

  public class TargetMap {
    public File get(String p) {
      return files.get(p);
    }
  }

  public static class Result {
    private FileTree tree;
    private ErrorLog log;
    private List<Target> targets;

    public Result(List<Target> targets, FileTree tree) {
      this.tree = tree;
      this.targets = targets;
    }

    public Result(List<Target> targets, ErrorLog log) {
      this.log = log;
      this.targets = targets;
    }

    public boolean pass() {
      return tree != null;
    }

    public ErrorLog getLog() {
      return log;
    }

    public FileTree getTree() {
      return tree;
    }

    public List<Target> getTargets() {
      return targets;
    }
  }
}
