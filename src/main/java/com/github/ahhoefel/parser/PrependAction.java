package com.github.ahhoefel.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PrependAction implements Function<Object[], Object> {

  @Override
  @SuppressWarnings("unchecked")
  public Object apply(Object[] objects) {
    List<Object> list;
    if (objects.length == 1) {
      list = new ArrayList<Object>();
    } else {
      list = (List<Object>) objects[1];
    }
    if (objects[0] != null) {
      list.add(0, objects[0]);
    }
    return list;
  }

  public static final PrependAction SINGLETON = new PrependAction();
}
