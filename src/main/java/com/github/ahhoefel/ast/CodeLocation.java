package com.github.ahhoefel.ast;

import java.util.Objects;

public class CodeLocation {

  private Target target;
  private int lineNumber;
  private int character;
  private int position;

  public CodeLocation(Target target, int lineNumber, int character, int position) {
    this.target = target;
    this.lineNumber = lineNumber;
    this.character = character;
    this.position = position;
  }

  public String toString() {
    return String.format("%s:%d:%d:%d", target, lineNumber + 1, character + 1, position);
  }

  public boolean equals(Object o) {
    if (!(o instanceof CodeLocation)) {
      return false;
    }
    CodeLocation location = (CodeLocation) o;
    boolean sameLineChar = lineNumber == location.lineNumber && character == location.character;
    boolean samePos = position == location.position;
    return Objects.equals(target, location.target) && (sameLineChar || samePos);
  }
}
