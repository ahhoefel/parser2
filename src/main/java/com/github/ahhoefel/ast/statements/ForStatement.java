package com.github.ahhoefel.ast.statements;

import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalogOld;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.ConditionalGotoOp;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.GotoOp;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.ArrayList;

public class ForStatement implements Statement {

  private final Block block;
  private final Expression condition;
  private Label conditionLabel;
  private Label blockLabel;

  public ForStatement(Expression condition, Block block) {
    this.condition = condition;
    this.block = block;
    this.conditionLabel = new Label();
    this.blockLabel = new Label();
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
    rep.add(new GotoOp(conditionLabel));
    rep.add(new DestinationOp(blockLabel));
    block.addToRepresentation(rep);
    rep.add(new DestinationOp(conditionLabel));
    condition.addToRepresentation(rep, new ArrayList<>());
    rep.add(new ConditionalGotoOp(condition.getRegister(), blockLabel));
  }

  @Override
  public void typeCheck(ErrorLog log) {
    if (condition.getType() != Type.BOOL) {
      log.add(new ParseError(null, "For condition type should be boolean. Got: " + condition.getType()));
    }
    block.typeCheck(log);
  }
}
