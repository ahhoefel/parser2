package com.github.ahhoefel.ast;

public class Target {

  private String base;
  private String file;

  public Target(String target) {
    int index = target.indexOf(":");
    base = target.substring(0, index);
    file = target.substring(index + 1);
  }

  public Target(String base, String file) {
    this.base = base;
    this.file = file;
  }

  public String getFilePath() {
    return base + "/" + file + ".ro";
  }

  public String getBase() {
    return base;
  }

  public String toString() {
    return base + ":" + file;
  }
}
