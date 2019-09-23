package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.CommentOp;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class MemberAccessExpression implements Expression {

  private final Token member;
  private final Expression expression;
  private StructType structType;
  private SymbolCatalog symbols;
  private Register register;

  public MemberAccessExpression(Expression expression, Token member) {
    this.member = member;
    this.expression = expression;
    this.register = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    expression.toIndentedString(out);
    out.add(".");
    out.add(member.getValue());
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
    expression.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    expression.addToRepresentation(rep, liveRegisters);
    liveRegisters.remove(liveRegisters.size() - 1);
    liveRegisters.add(register);
    rep.add(new CommentOp("Accessing member " + member.getValue()));
    rep.add(new SetOp(expression.getRegister(), register, structType.getMemberOffset(member.getValue()), 0, structType.getMember(member.getValue()).width()));
  }

  @Override
  public Type getType() {
    structType = StructType.toStructType(expression.getType());
    Type memberType = structType.getMember(member.getValue());
    if (memberType == null) {
      throw new RuntimeException(String.format("No member %s on %s", member.getValue(), expression.getType()));
    }
    register.setWidth(memberType.width());
    //if (!memberType.equals(expression.getType())) {
    //  throw new RuntimeException(String.format("Type mismatch: Cannot assign expression of type %s to member %s on type %s", expression.getType(), member.getValue(), type));
    //}
    return memberType;
  }
}
