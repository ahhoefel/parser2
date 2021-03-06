package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.type.Type;

public class TypeDeclaration implements Declaration {
  private String identifier;
  private Type type;

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
}
