package com.github.ahhoefel.ast;

import java.util.HashMap;
import java.util.Map;

public class Type {

  private String name;

  private Type(String name) {
    this.name = name;
    types.put(name, this);
  }

  private static Map<String, Type> types = new HashMap<>();

  public static Type getType(String name) {
    if (!types.containsKey(name)) {
      types.put(name, new Type(name));
    }
    return types.get(name);
  }

  public String toString() {
    return name;
  }

  public static Type INT = new Type("int");
  public static Type BOOL = new Type("bool");
  public static Type STRING = new Type("string");

}
