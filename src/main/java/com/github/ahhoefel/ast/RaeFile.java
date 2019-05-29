package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.GotoOp;
import com.github.ahhoefel.ir.operation.LiteralLabelOp;
import com.github.ahhoefel.ir.operation.PushOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;
import java.util.List;

public class RaeFile {

  private SymbolCatalog symbols;
  private List<Import> imports;
  private List<FunctionDeclaration> functions;
  private Register endLabelRegister;
  private Label endLabel;

  public RaeFile() {
    imports = new ArrayList<>();
    functions = new ArrayList<>();
    symbols = new SymbolCatalog("file", false);
    endLabelRegister = new Register();
    endLabel = new Label();
  }

  public void addFunction(FunctionDeclaration f) {
    functions.add(f);
    f.setSymbolCatalog(symbols);
  }

  public void addImport(Import imp0rt) {
    imports.add(imp0rt);
  }

  public SymbolCatalog getSymbols() {
    return symbols;
  }

  public Representation representation() {
    Representation rep = new Representation();
    rep.add(new LiteralLabelOp(endLabel, endLabelRegister));
    rep.add(new PushOp(endLabelRegister));
    rep.add(new GotoOp(symbols.getFunction("main").getLabel()));
    for (FunctionDeclaration f : functions) {
      f.addToRepresentation(rep);
    }
    rep.add(new DestinationOp(endLabel));
    return rep;
  }

  public void addToRepresentation(Representation rep) {
    for (FunctionDeclaration f : functions) {
      f.addToRepresentation(rep);
    }
  }

  public String toString() {
    IndentedString s = new IndentedString();
    toIndentedString(s);
    return s.toString();
  }

  public void toIndentedString(IndentedString s) {
    for (Import i : imports) {
      i.toIndentedString(s);
    }
    s.endLine();
    for (FunctionDeclaration f : functions) {
      f.toIndentedString(s);
      s.endLine();
    }
  }

  public List<Import> getImports() {
    return imports;
  }
}
