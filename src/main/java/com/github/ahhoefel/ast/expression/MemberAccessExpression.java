package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.*;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.CommentOp;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.parser.Token;

import java.util.List;
import java.util.Optional;

public class MemberAccessExpression implements LValueExpression {

  private final Token member;
  private final Expression expression;
  private StructType structType;
  private Register register;
  private Type memberType;

  public MemberAccessExpression(Expression expression, Token member) {
    this.member = member;
    this.expression = expression;
    this.register = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  public Expression getExpression() {
    return expression;
  }

  public Token getMember() {
    return member;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    expression.setSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    expression.addToRepresentation(rep, liveRegisters);
    expression.removeLiveRegisters(liveRegisters);
    rep.add(new CommentOp("Accessing member " + member.getValue()));
    if (expression == null) {
      System.out.println("Null expr");
    }
    if (structType == null) {
      System.out.println("null strut type");
    }
    rep.add(new SetOp(expression.getRegister(), register, structType.getMemberOffset(member.getValue()), 0,
        structType.getMember(member.getValue()).width()));
    addLiveRegisters(liveRegisters);
  }

  @Override
  public void addLiveRegisters(List<Register> stack) {
    stack.add(register);
  }

  @Override
  public void removeLiveRegisters(List<Register> stack) {
    stack.remove(stack.size() - 1);
  }

  @Override
  public Type getType() {
    return memberType;
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    Optional<Type> type = expression.checkType(log);
    structType = StructType.toStructType(type.get());
    memberType = structType.getMember(member.getValue());
    if (memberType == null) {
      log.add(new ParseError(null, String.format("No member %s on %s", member.getValue(), expression.getType())));
      return Optional.empty();
    }
    register.setWidth(memberType.width());
    // if (!memberType.equals(expression.checkType())) {
    // throw new RuntimeException(String.format("Type mismatch: Cannot assign
    // expression of type %s to member %s on type %s", expression.checkType(),
    // member.getValue(), type));
    // }
    return Optional.of(memberType);
  }

  @Override
  public boolean isLValue() {
    return true;
  }

  @Override
  public void addToRepresentationAsLValue(Representation rep, List<Register> liveRegisters, Expression out) {
    rep.add(new CommentOp("LValue expression"));
    expression.addToRepresentation(rep, liveRegisters);
    // liveRegisters.remove(liveRegisters.size() - 1);
    liveRegisters.add(register);
    rep.add(new CommentOp("Assigning to member " + member.getValue()));
    if (structType == null) {
      System.out.println("null strut type");
    }
    rep.add(new SetOp(out.getRegister(), expression.getRegister(), 0, structType.getMemberOffset(member.getValue()),
        structType.getMember(member.getValue()).width()));
  }
}
