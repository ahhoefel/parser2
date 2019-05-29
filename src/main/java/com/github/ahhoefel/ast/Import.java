package com.github.ahhoefel.ast;

import com.github.ahhoefel.util.IndentedString;

public class Import implements Declaration {

  private String shortName;
  private String path;

  public Import(String shortName, String path) {
    this.shortName = shortName;
    this.path = path;
  }

  public Import(String path) {
    shortName = path.substring(path.lastIndexOf('/'));
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

  public Target getTarget(String base) {
    return new Target(base, path);
  }
}
