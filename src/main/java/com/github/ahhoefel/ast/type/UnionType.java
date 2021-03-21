package com.github.ahhoefel.ast.type;

import java.util.List;
import java.util.Optional;

import com.github.ahhoefel.ast.Member;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.parser.ErrorLog;

public class UnionType implements Type {

  private List<Member> members;
  private Optional<Integer> width;

  public UnionType(List<Member> members) {
    this.members = members;
    width = Optional.empty();
  }

  @Override
  public void linkTypes(SymbolCatalog catalog, ErrorLog log) {
    for (Member member : members) {
      member.getType().linkTypes(catalog, log);
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

  @Override
  public void accept(Visitor v) {
    v.visit(this);
  }
}
