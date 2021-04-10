package com.github.ahhoefel.ast;

import java.nio.file.Path;
import java.util.Objects;

public class Target {

  private Path source;
  private String base;
  private String file;

  public Target(Path source, String target) {
    int index = target.indexOf(":");
    this.source = source;
    base = target.substring(0, index);
    file = target.substring(index + 1);
  }

  public Target(Path source, Path target) {
    this.source = source;
    Path relative = source.relativize(target);
    base = relative.getParent().toString();
    file = relative.getFileName().toString();
  }

  public Target(Path source, String base, String file) {
    this.source = source;
    this.base = base;
    this.file = file;
  }

  public Path getFilePath() {
    return source.resolve(base + "/" + file + ".ro");
  }

  public Path getErrorPath() {
    return source.resolve(base + "/" + file + ".err");
  }

  public Path getSource() {
    return source;
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
