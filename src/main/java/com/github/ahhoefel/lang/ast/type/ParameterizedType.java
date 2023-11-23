package com.github.ahhoefel.lang.ast.type;

import java.util.List;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.parser.Locateable;

public class ParameterizedType implements Locateable {

  // private List<Type> params;
  private CodeLocation location;

  public ParameterizedType(List<Type> params) {
    // this.params = params;
  }

  @Override
  public CodeLocation getLocation() {
    return location;
  }

  @Override
  public void setLocation(CodeLocation location) {
    this.location = location;
  }
}
