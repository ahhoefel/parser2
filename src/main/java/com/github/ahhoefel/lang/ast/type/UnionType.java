package com.github.ahhoefel.lang.ast.type;

import java.util.List;

import com.github.ahhoefel.lang.ast.Member;
import com.github.ahhoefel.lang.ast.Visitor;

public class UnionType implements Type {

    private List<Member> members;
    private int width;

    public UnionType(List<Member> members) {
        this.members = members;
        width = 8; // Could be replaced with log_2(#members)
        for (Member member : members) {
            width += member.getType().getWidthBits();
        }
    }

    @Override
    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public List<Member> getMembers() {
        return members;
    }

    @Override
    public int getWidthBits() {
        return width;
    }
}
