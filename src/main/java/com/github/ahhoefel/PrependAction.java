package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PrependAction implements Function<Object[], Object> {

  @Override
  public Object apply(Object[] objects) {
    if (objects.length == 1) {
      ArrayList<Object> list = new ArrayList();
      list.add(objects[0]);
      return list;
    }
    List<Object> list = (List<Object>) objects[1];
    list.add(0, objects[0]);
    return list;
  }

  public static final PrependAction SINGLETON = new PrependAction();
}
