package com.github.ahhoefel.lang.ast;

import java.nio.file.Path;
import java.util.Objects;

public class Target {

  private Path source;
  private String base;
  private String file;
  private String name;

  public Target(Path source, String target) {
    this.source = source;
    int index = target.indexOf(":");
    base = target.substring(0, index);
    name = target.substring(index + 1);
    file = name + ".ro";
  }

  public Target(Path source, Path target) {
    this.source = source;
    Path relative = source.relativize(target);
    base = relative.getParent().toString();
    file = relative.getFileName().toString();
    name = file.substring(0, file.length() - 3);
  }

  public Target(Path source, String base, String file) {
    this.source = source;
    this.base = base;
    this.file = file;
    this.name = file.substring(0, file.length() - 3);
  }

  public Path getFilePath() {
    return source.resolve(base + "/" + file);
  }

  public Path getErrorPath() {
    return source.resolve(base + "/" + name + ".err");
  }

  public Path getSource() {
    return source;
  }

  public String getBase() {
    return base;
  }

  public String getSuffix() {
    return name;
  }

  public String toString() {
    return base + ":" + name;
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
