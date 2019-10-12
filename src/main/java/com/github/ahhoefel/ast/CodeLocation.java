package com.github.ahhoefel.ast;

import java.util.Objects;

public class CodeLocation {

  private Target target;
  private int lineNumber;
  private int character;

  public CodeLocation(Target target, int lineNumber, int character) {
    this.target = target;
    this.lineNumber = lineNumber;
    this.character = character;
  }

  public String toString() {
    return String.format("%s:%d:%d", target, lineNumber, character);
  }

  public boolean equals(Object o) {
    if (!(o instanceof CodeLocation)) {
      return false;
    }
    CodeLocation location = (CodeLocation) o;
    return Objects.equals(target, location.target) && lineNumber == location.lineNumber && character == location.character;
  }
}
