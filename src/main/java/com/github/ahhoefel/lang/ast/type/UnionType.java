package com.github.ahhoefel.lang.ast.type;

import java.util.List;

import com.github.ahhoefel.lang.ast.Member;
import com.github.ahhoefel.lang.ast.Visitor;

public class UnionType implements Type {

    private List<Member> members;

    public UnionType(List<Member> members) {
        this.members = members;
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public List<Member> getMembers() {
        return members;
    }
}
