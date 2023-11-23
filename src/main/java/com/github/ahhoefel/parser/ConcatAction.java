package com.github.ahhoefel.parser;

import java.util.function.Function;

import com.github.ahhoefel.lang.ast.CodeLocation;

public class ConcatAction implements Function<Locateable[], Locateable> {
  @Override
  public Locateable apply(Locateable[] objects) {
    if (objects.length == 0) {
      throw new RuntimeException("ConcatAction only applies to one or more object. Got 0.");
    }
    StringBuffer buf = new StringBuffer();
    Symbol type = null;
    CodeLocation location = null;
    for (int i = 0; i < objects.length; i++) {
      Locateable o = objects[i];
      if (location == null) {
        location = o.getLocation();
      } else {
        location = new CodeLocation(location, o.getLocation());
      }
      if (o instanceof Token) {
        Token token = (Token) o;
        buf.append(token.getValue());
        if (type == null) {
          type = token.getSymbol();
        }
      } else {
        throw new RuntimeException("Unsupported type.");
      }
    }
    return new Token(type, buf.toString(), location);
  }

  public static final ConcatAction SINGLETON = new ConcatAction();
}
