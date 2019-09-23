package com.github.ahhoefel.ast;

import java.util.*;

public class StructType implements Type {

  private List<String> orderedMembers;
  private Map<String, Type> members;
  private Map<String, Integer> memberOffsets;
  private Optional<Integer> width = Optional.empty();

  public StructType(List<Member> members) {
    this.members = new HashMap<>();
    this.orderedMembers = new ArrayList<>();
    this.memberOffsets = new HashMap<>();
    for (Member member : members) {
      this.members.put(member.getIdentifier(), member.getType());
      this.orderedMembers.add(member.getIdentifier());
    }
  }

  public Type getMember(String identifier) {
    return members.get(identifier);
  }

  public List<String> memberNames() {
    return orderedMembers;
  }

  @Override
  public void linkTypes(SymbolCatalog catalog) {
    for (Type type : members.values()) {
      type.linkTypes(catalog);
    }
  }

  @Override
  public int width() {
    if (width.isPresent()) {
      return width.get();
    }
    int w = 0;
    for (Map.Entry<String, Type> entry : members.entrySet()) {
      memberOffsets.put(entry.getKey(), w);
      w += entry.getValue().width();
    }
    width = Optional.of(w);
    return w;
  }

  public String toString() {
    String out = "struct {\n";
    for (Map.Entry<String, Type> member : members.entrySet()) {
      out += "  " + member.getKey() + ": " + member.getValue() + "\n";
    }
    out += "}";
    return out;
  }

  public int getMemberOffset(String memberName) {
    return memberOffsets.get(memberName);
  }

  public static StructType toStructType(Type type) {
    Type t = type;
    while (t instanceof NamedType) {
      t = ((NamedType) t).getType();
    }
    if (!(t instanceof StructType)) {
      throw new RuntimeException("Only struct type can be used for struct literal expressions: " + t + " from named type " + type);
    }
    return (StructType) t;
  }

}
