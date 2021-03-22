package com.github.ahhoefel.ast;

public class Import implements Declaration {

  private String shortName;
  private String path;
  private SymbolCatalog symbols;

  public Import(String shortName, String path) {
    this.shortName = shortName;
    this.path = path;
  }

  public Import(String path) {
    int index = path.lastIndexOf('/');
    shortName = index == -1 ? path : path.substring(index);
    this.path = path;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  @Override
  public File addToFile(File file) {
    file.addImport(this);
    return file;
  }

  public String getShortName() {
    return shortName;
  }

  public String getPath() {
    return path;
  }

  public void link(FileTree.TargetMap map) {
    File f = map.get(path);
    symbols = f.getSymbols();
  }

  public SymbolCatalog getSymbols() {
    return symbols;
  }

  public String toString() {
    return String.format("import %s %s", shortName, path);
  }
}
