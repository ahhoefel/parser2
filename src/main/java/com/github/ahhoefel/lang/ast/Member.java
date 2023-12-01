package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.type.Type;

public class Member {

  private String identifier;
  private Expression type;

  public Member(String identifier, Type type) {
    this.identifier = identifier;
    this.type = type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public Expression getType() {
    return type;
  }

  public String toString() {
    return identifier + " " + type;
  }
}
