package com.github.ahhoefel.lang.ast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class ParseError {

  private CodeLocation location;
  private String description;
  private String snippet;
  private Exception exception;

  public ParseError(CodeLocation location, String description) {
    this.location = location;
    this.description = description;
  }

  public ParseError(CodeLocation location, String snippet, String description) {
    this.location = location;
    this.snippet = snippet;
    this.description = description;
  }

  public ParseError(CodeLocation location, String description, Exception exception) {
    this.location = location;
    this.description = description;
    this.exception = exception;
  }

  public String toString() {
    String out = "";
    if (location != null) {
      out += location.toString();
    } else {
      out += "no location ";
    }
    if (snippet != null) {
      out += snippet;
    }
    out += "\n";
    out += description;
    if (exception != null) {
      out += "\n";
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      out += sw.toString();
    }
    return out;
  }

  public boolean equals(Object o) {
    if (!(o instanceof ParseError)) {
      return false;
    }
    ParseError err = (ParseError) o;
    return Objects.equals(location, err.location) && Objects.equals(description, err.description);
  }
}
