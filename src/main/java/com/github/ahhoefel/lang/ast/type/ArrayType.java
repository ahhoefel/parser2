package com.github.ahhoefel.lang.ast.type;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.parser.Locateable;

public class ArrayType implements Locateable {
  private CodeLocation location;
  // private Type type;

  public ArrayType(Type type) {
    // this.type = type;
  }

  public CodeLocation getLocation() {
    return location;
  }

  @Override
  public void setLocation(CodeLocation location) {
    this.location = location;
  }
}
