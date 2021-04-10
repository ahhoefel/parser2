package com.github.ahhoefel.ast;

public class Import implements Declaration {

  private boolean explicitShortName;
  private String shortName;
  private String path;
  private SymbolCatalogOld symbols;

  public Import(String shortName, String path) {
    this.shortName = shortName;
    this.path = path;
    this.explicitShortName = true;
  }

  public Import(String path) {
    this.path = path;
    int index = path.lastIndexOf('/');
    shortName = index == -1 ? path : path.substring(index + 1);
    this.explicitShortName = false;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  @Override
  public File addToFile(File file) {
    file.addImport(this);
    return file;
  }

  public boolean hasExplicitShortName() {
    return explicitShortName;
  }

  public String getShortName() {
    return shortName;
  }

  public String getPath() {
    return path;
  }

  public String getTargetString() {
    int index = path.lastIndexOf('/');
    return path.substring(0, index) + ":" + path.substring(index + 1, path.length());
  }

  public void link(FileTree.TargetMap map) {
    File f = map.get(path);
    symbols = f.getSymbols();
  }

  public SymbolCatalogOld getSymbols() {
    return symbols;
  }

  public String toString() {
    if (explicitShortName) {
      return String.format("import %s %s", shortName, path);
    }
    return String.format("import %s", path);
  }
}
