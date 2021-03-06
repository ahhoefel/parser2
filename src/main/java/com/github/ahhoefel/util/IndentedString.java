package com.github.ahhoefel.util;

public class IndentedString {

  private StringBuilder builder;
  private String indent;
  private boolean startOfLine;

  public IndentedString() {
    this.builder = new StringBuilder();
    this.indent = "";
    this.startOfLine = true;
  }

  public IndentedString indent() {
    if (!startOfLine) {
      this.endLine();
    }
    this.indent += "  ";
    return this;
  }

  public IndentedString unindent() {
    this.indent = this.indent.substring(2);
    return this;
  }

  public IndentedString addLine(String line) {
    if (!startOfLine) {
      endLine();
    }
    builder.append(indent);
    builder.append(line);
    builder.append('\n');
    return this;
  }

  public IndentedString add(String line) {
    if (startOfLine) {
      builder.append(indent);
      startOfLine = false;
    }
    builder.append(line);
    return this;
  }

  public IndentedString endLine() {
    builder.append('\n');
    startOfLine = true;
    return this;
  }

  public String toString() {
    return builder.toString();
  }
}
