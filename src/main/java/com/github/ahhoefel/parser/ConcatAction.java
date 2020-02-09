package com.github.ahhoefel.parser;

import com.github.ahhoefel.ast.CodeLocation;

import java.util.function.Function;

public class ConcatAction implements Function<Object[], Object> {
  @Override
  public Object apply(Object[] objects) {
    if (objects.length == 0) {
      return "";
    }
    StringBuffer buf = new StringBuffer();
    Symbol type = null;
    CodeLocation location = null;
    for (int i = 0; i < objects.length; i++) {
      Object o = objects[i];
      if (o instanceof Token) {
        Token token = (Token) o;
        buf.append(token.getValue());
        if (type == null) {
          type = token.getSymbol();
          location = token.getLocation();
        }
      } else {
        buf.append(o);
      }
    }
    return new Token(type, buf.toString(), location);
  }

  public static final ConcatAction SINGLETON = new ConcatAction();
}
