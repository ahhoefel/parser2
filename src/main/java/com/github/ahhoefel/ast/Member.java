package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.type.Type;

public class Member {

  private String identifier;
  private Type type;

  public Member(String identifier, Type type) {
    this.identifier = identifier;
    this.type = type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public Type getType() {
    return type;
  }

  public String toString() {
    return identifier + " " + type;
  }
}
