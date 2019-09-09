package com.github.ahhoefel.ast;

import java.util.List;
import java.util.Optional;

public class StructType implements Type {

  private List<Member> members;
  private Optional<Integer> width = Optional.empty();

  public StructType(List<Member> members) {
    this.members = members;
  }

  @Override
  public void linkTypes(SymbolCatalog catalog) {
    for (Member member : members) {
      member.getType().linkTypes(catalog);
    }
  }

  @Override
  public int width() {
    if (width.isPresent()) {
      return width.get();
    }
    int w = 0;
    for (Member member : members) {
      w += member.getType().width();
    }
    width = Optional.of(w);
    return w;
  }

  public String toString() {
    String out = "struct {\n";
    for (Member member : members) {
      out += "  " + member.toString() + "\n";
    }
    out += "}";
    return out;
  }
}
