package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.type.NamedType;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.GotoOp;
import com.github.ahhoefel.ir.operation.LiteralLabelOp;
import com.github.ahhoefel.ir.operation.PushOp;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class File implements Visitable {

  private Target target;
  private ImportCatalog imports;
  private SymbolCatalog symbols;
  private List<Declaration> declarations;
  private List<FunctionDeclaration> functions;
  private List<TypeDeclaration> types;
  private List<NamedType> unresolvedTypes;
  private Register endLabelRegister;
  private Label endLabel;

  public File() {
    imports = new ImportCatalog();
    declarations = new ArrayList<>();
    functions = new ArrayList<>();
    types = new ArrayList<>();
    unresolvedTypes = new ArrayList<>();
    symbols = new SymbolCatalog("file", imports, Optional.empty());
    endLabelRegister = new Register();
    endLabel = new Label();
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public List<FunctionDeclaration> getFunctions() {
    return functions;
  }

  public void setTarget(Target target) {
    this.target = target;
  }

  public Target getTarget() {
    return target;
  }

  public void addFunction(FunctionDeclaration f) {
    declarations.add(f);
    functions.add(f);
    f.setSymbolCatalog(symbols);
  }

  public void addType(TypeDeclaration t) {
    declarations.add(t);
    types.add(t);
    symbols.addType(t);
  }

  public void addImport(Import imp0rt) {
    imports.add(imp0rt);
  }

  public List<Declaration> getDeclarations() {
    return declarations;
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

  public ImportCatalog getImports() {
    return imports;
  }

  public void linkImports(FileTree.TargetMap map) {
    // System.out.println("Linking imports of " + target);
    imports.linkImports(map);
  }

  public void linkSymbols(ErrorLog log) {
    for (NamedType type : unresolvedTypes) {
      type.linkTypes(symbols, log);
    }
    unresolvedTypes.clear();
  }

  public void deferResolution(NamedType type) {
    unresolvedTypes.add(type);
  }

  public void typeCheck(ErrorLog log) {
    for (FunctionDeclaration fn : functions) {
      fn.typeCheck(log);
    }
  }
}
