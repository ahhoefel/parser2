package com.github.ahhoefel.ast;

import java.util.List;
import java.util.Optional;

public class UnionType implements Type {

  private List<Member> members;
  private Optional<Integer> width;

  public UnionType(List<Member> members) {
    this.members = members;
    width = Optional.empty();
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
    int maxMemberWidth = 0;
    for (Member member : members) {
      maxMemberWidth = Math.max(maxMemberWidth, member.getType().width());
    }
    width = Optional.of(1 + maxMemberWidth);
    return width.get();
  }
}
