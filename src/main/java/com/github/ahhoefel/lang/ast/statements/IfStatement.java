package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.CommentOp;
import com.github.ahhoefel.ir.operation.ConditionalGotoOp;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.NegateOp;
import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.ParseError;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.ArrayList;
import java.util.Optional;

public class IfStatement implements Statement {

  private final Block block;
  private final Expression condition;
  private final Label destination;
  private final Register negation;
  private final CodeLocation location;

  public IfStatement(Expression condition, Block block, CodeLocation location) {
    this.condition = condition;
    this.block = block;
    this.destination = new Label();
    this.negation = new Register(1);
    this.location = location;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  public Expression getCondition() {
    return condition;
  }

  public Block getBlock() {
    return block;
  }

  @Override
  public void addToSymbolCatalog(SymbolCatalogOld symbols) {
    condition.setSymbolCatalog(symbols);
    block.addToSymbolCatalog(symbols);
  }

  @Override
  public void addToRepresentation(Representation rep) {
    rep.add(new CommentOp("if condition"));
    condition.addToRepresentation(rep, new ArrayList<>());
    rep.add(new NegateOp(condition.getRegister(), negation));
    rep.add(new ConditionalGotoOp(negation, this.destination));
    rep.add(new CommentOp("if block"));
    block.addToRepresentation(rep);
    rep.add(new DestinationOp(this.destination));
  }

  @Override
  public void typeCheck(ErrorLog log) {
    Optional<Type> type = condition.checkType(log);
    if (!type.isPresent()) {
      log.add(new ParseError(location, "failed to type check condition."));
      return;
    }
    if (type.get() != Type.BOOL) {
      log.add(new ParseError(location, "If condition should be boolean. Got: " + condition.getType()));
    }
    block.typeCheck(log);
  }
}
