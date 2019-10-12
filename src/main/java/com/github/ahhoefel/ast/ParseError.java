package com.github.ahhoefel.ast;

import java.util.Objects;

public class ParseError {

  private CodeLocation location;
  private String description;

  public ParseError(CodeLocation location, String description) {
    this.location = location;
    this.description = description;
  }

  public String toString() {
    return (location != null ? location.toString() : "no location") + " " + description;
  }

  public boolean equals(Object o) {
    if (!(o instanceof ParseError)) {
      return false;
    }
    ParseError err = (ParseError) o;
    return Objects.equals(location, err.location) && Objects.equals(description, err.description);
  }
}
