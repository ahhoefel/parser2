package com.github.ahhoefel.lang.ast;

import java.util.List;
import java.util.Objects;

import com.github.ahhoefel.parser.Locateable;

public class CodeLocation {

  private Target target;
  private int lineNumber;
  private int character;
  private int position;
  private int length;

  public CodeLocation(Target target, int lineNumber, int character, int position) {
    this.target = target;
    this.lineNumber = lineNumber;
    this.character = character;
    this.position = position;
    this.length = 1;
  }

  public CodeLocation(CodeLocation from, CodeLocation to) {
    if (from == null) {
      throw new RuntimeException("Codelocations cannot be null: from.");
    }
    if (to == null) {
      throw new RuntimeException("Codelocations cannot be null: to.");
    }
    if (from.target == null || to.target == null) {
      if (from.target != null || to.target != null) {
        throw new RuntimeException("Codelocations cannot span multiple targets:" + from.target + ", " + to.target);
      }
    } else if (!Objects.equals(from.target, to.target)) {
      throw new RuntimeException("Codelocations cannot span multiple targets:" + from.target + ", " + to.target);
    }
    this.target = from.target;
    this.lineNumber = from.lineNumber;
    this.character = from.character;
    this.position = from.position;
    this.length = from.length + to.length;
  }

  public CodeLocation(Locateable[] locateables) {
    CodeLocation from = locateables[0].getLocation();
    this.target = from.target;
    this.lineNumber = from.lineNumber;
    this.character = from.character;
    this.position = from.position;
    for (int i = 0; i < locateables.length; i++) {
      this.length += locateables[i].getLocation().length;
    }
  }

  public String toString() {
    return String.format("%s:%d:%d:%d", target != null ? target : "", lineNumber + 1, character + 1, position);
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
