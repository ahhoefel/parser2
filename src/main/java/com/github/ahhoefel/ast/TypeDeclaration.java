package com.github.ahhoefel.ast;

public class TypeDeclaration implements Declaration {
  private String identifier;
  private Type type;

  public TypeDeclaration(String identifier, Type type) {
    this.identifier = identifier;
    this.type = type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public Type getType() {
    return type;
  }

  @Override
  public RaeFile addToFile(RaeFile file) {
    file.addType(this);
    return file;
  }

  public String toString() {
    return "type " + identifier + " " + type;
  }
}
