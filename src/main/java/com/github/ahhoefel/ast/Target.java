package com.github.ahhoefel.ast;

import java.util.Objects;

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

  public String getSuffix() {
    return file;
  }

  public String toString() {
    return base + ":" + file;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Target)) {
      return false;
    }
    Target t = (Target) obj;
    return Objects.equals(base, t.base) && Objects.equals(file, t.file);
  }

  @Override
  public int hashCode() {
    return base.hashCode() + 31 * file.hashCode();
  }
}
