package com.github.ahhoefel.ast;

import java.util.List;

public class ParameterizedType {

  private List<Type> params;

  public ParameterizedType(List<Type> params) {
    this.params = params;
  }
}
