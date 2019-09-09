package com.github.ahhoefel.ast;

import com.github.ahhoefel.util.IndentedString;

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

  public void toIndentedString(IndentedString out) {
    out.add("import ").add(shortName).add(path).endLine();
  }

  @Override
  public RaeFile addToFile(RaeFile file) {
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
    RaeFile f = map.get(path);
    symbols = f.getSymbols();
  }

  public SymbolCatalog getSymbols() {
    return symbols;
  }

  public String toString() {
    return String.format("import %s %s", shortName, path);
  }
}
