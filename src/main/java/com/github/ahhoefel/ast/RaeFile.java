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
import java.util.Optional;

public class RaeFile {

  private Target target;
  private ImportCatalog imports;
  private SymbolCatalog symbols;
  private List<FunctionDeclaration> functions;
  private List<TypeDeclaration> types;
  private List<NamedType> unresolvedTypes;
  private Register endLabelRegister;
  private Label endLabel;

  public RaeFile() {
    imports = new ImportCatalog();
    functions = new ArrayList<>();
    types = new ArrayList<>();
    unresolvedTypes = new ArrayList<>();
    symbols = new SymbolCatalog("file", imports, Optional.empty());
    endLabelRegister = new Register();
    endLabel = new Label();
  }

  public void setTarget(Target target) {
    this.target = target;
  }

  public Target getTarget() {
    return target;
  }

  public void addFunction(FunctionDeclaration f) {
    functions.add(f);
    f.setSymbolCatalog(symbols);
  }

  public void addType(TypeDeclaration t) {
    types.add(t);
    symbols.addType(t);
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
    imports.toIndentedString(s);
    s.endLine();
    for (FunctionDeclaration f : functions) {
      f.toIndentedString(s);
      s.endLine();
    }
  }

  public ImportCatalog getImports() {
    return imports;
  }

  public void linkImports(FileTree.TargetMap map) {
    //System.out.println("Linking imports of " + target);
    imports.linkImports(map);
  }

  public void linkSymbols() {
    for (NamedType type : unresolvedTypes) {
      type.linkTypes(symbols);
    }
    unresolvedTypes.clear();
  }

  public void deferResolution(NamedType type) {
    System.out.println("Unresolved type: " + type);
    unresolvedTypes.add(type);
  }

  public void typeCheck(ErrorLog log) {
    for (FunctionDeclaration fn : functions) {
      fn.typeCheck(log);
    }
  }
}
