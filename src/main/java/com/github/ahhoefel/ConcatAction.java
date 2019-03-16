package com.github.ahhoefel;

import java.util.function.Function;

public class ConcatAction implements Function<Object[], Object> {
  @Override
  public Object apply(Object[] objects) {
    if (objects.length == 0) {
      return "";
    }
    StringBuffer buf = new StringBuffer();
    for (Object o : objects) {
      if (o instanceof Token) {
        buf.append(((Token) o).getValue());
      } else {
        buf.append(o);
      }
    }
    return buf.toString();
  }

  public static final ConcatAction SINGLETON = new ConcatAction();
}
