package com.github.ahhoefel.lang.ast;

import java.util.Objects;

public class ParseError {

  private CodeLocation location;
  private String description;
  private String snippet;

  public ParseError(CodeLocation location, String description) {
    this.location = location;
    this.description = description;
  }

  public ParseError(CodeLocation location, String snippet, String description) {
    this.location = location;
    this.snippet = snippet;
    this.description = description;
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
