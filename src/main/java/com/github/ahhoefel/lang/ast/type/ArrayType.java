package com.github.ahhoefel.lang.ast.type;

import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;

public class ArrayType extends Type {
  private Expression type;

  public ArrayType(Expression type) {
    this.type = type;
  }

  @Override
  public void accept(Visitor v, Object... args) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'accept'");
  }

  @Override
  public int getWidthBits() {
    return 64;
  }

  @Override
  public int getEncoding() {
    return 0;
  }

  @Override
  public boolean isLValue() {
    return false;
  }

  @Override
  public Expression getType() {
    return Type.TYPE;
  }
}
