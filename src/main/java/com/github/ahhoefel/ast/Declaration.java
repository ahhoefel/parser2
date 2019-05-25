package com.github.ahhoefel.ast;

import com.github.ahhoefel.util.IndentedString;

public class Declaration {

  private FunctionDeclaration fn;

  public Declaration(FunctionDeclaration fn) {
    this.fn = fn;
  }

  public boolean isFunction() {
    return fn != null;
  }

  public FunctionDeclaration getFunction() {
    return fn;
  }

  public void toIndentedString(IndentedString out) {
    if (isFunction()) {
      fn.toIndentedString(out);
    }
  }
}
