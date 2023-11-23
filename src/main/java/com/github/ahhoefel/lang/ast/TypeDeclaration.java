package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.type.Type;

public class TypeDeclaration implements Declaration {
  private String identifier;
  private Type type;
  private CodeLocation location;

  public TypeDeclaration(String identifier, Type type) {
    this.identifier = identifier;
    this.type = type;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  public String getIdentifier() {
    return identifier;
  }

  public Type getType() {
    return type;
  }

  @Override
  public File addToFile(File file) {
    file.addType(this);
    return file;
  }

  @Override
  public CodeLocation getLocation() {
    return location;
  }

  @Override
  public void setLocation(CodeLocation location) {
    this.location = location;
  }
}
