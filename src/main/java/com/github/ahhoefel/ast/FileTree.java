package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.rules.Language;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileTree {

  public Map<Target, RaeFile> files;

  public Representation representation(Target target) {
    Representation rep = new Representation();
    RaeFile main = files.get(target);
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
    FileTree tree = new FileTree();
    Language lang = new Language();
    List<Target> targets = new ArrayList();
    while (!targets.isEmpty()) {
      Target t = targets.remove(targets.size() - 1);
      System.out.println("Reading target " + t);
      FileReader reader = new FileReader(t.getFilePath());
      RaeFile file = lang.parse(reader);
      tree.files.put(t, file);
      for (Import im : file.getImports()) {
        Target next = im.getTarget(target.getBase());
        if (!tree.files.containsKey(next)) {
          targets.add(next);
          tree.files.put(t, null);
        }
      }
    }
    return tree;
  }
}
