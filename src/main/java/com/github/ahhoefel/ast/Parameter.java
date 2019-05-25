package com.github.ahhoefel.ast;

import com.github.ahhoefel.Token;
import com.github.ahhoefel.util.IndentedString;

public class Parameter {

  public String name;
  public String type;

  public Parameter(Token name, Token type) {
    this.name = name.getValue();
    this.type = type.getValue();
  }

  public void toIndentedString(IndentedString out) {
    out.add(type);
    out.add(" ");
    out.add(name);
  }
}
