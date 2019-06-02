package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.rules.Language;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    FunctionDeclaration mainFn = main.getSymbols().getFunction("main");
    mainFn.getLabel();
    Label stopLabel = new Label();
    Register stopLabelRegister = new Register();
    rep.add(new LiteralLabelOp(stopLabel, stopLabelRegister));
    rep.add(new PushOp(stopLabelRegister));
    rep.add(new GotoOp(main.getSymbols().getFunction("main").getLabel()));
    rep.add(new DestinationOp(stopLabel));
    rep.add(new StopOp("Ended execution"));
    for (RaeFile file : files.values()) {
      file.addToRepresentation(rep);
    }
    return rep;
  }

  public static FileTree fromTarget(Target target) throws IOException {
    String base = target.getBase();
    FileTree tree = new FileTree(base);
    Language lang = new Language();
    List<String> paths = new ArrayList();
    paths.add(target.getSuffix());
    while (!paths.isEmpty()) {
      String p = paths.remove(paths.size() - 1);
      Target t = new Target(base, p);
      System.out.println("Reading target " + t);
      FileReader reader = new FileReader(t.getFilePath());
      RaeFile file = lang.parse(reader);
      tree.files.put(p, file);
      for (Import im : file.getImports().toList()) {
        String next = im.getPath();
        if (!tree.files.containsKey(next)) {
          paths.add(next);
          tree.files.put(next, null);
        }
      }
    }

    for (RaeFile file : tree.files.values()) {
      file.linkImports(tree.getTargetMap());
    }
    return tree;
  }

  private TargetMap getTargetMap() {
    return new TargetMap();
  }

  public class TargetMap {
    public RaeFile get(String p) {
      return files.get(p);
    }
  }
}
