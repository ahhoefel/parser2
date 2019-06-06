package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.ConditionalGotoOp;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.GotoOp;
import com.github.ahhoefel.util.IndentedString;

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

  @Override
  public void addToSymbolCatalog(SymbolCatalog symbols) {
    condition.setSymbolCatalog(symbols);
    block.addToSymbolCatalog(symbols);
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("for ");
    condition.toIndentedString(out);
    out.add(" {").endLine();
    block.toIndentedString(out.indent());
    out.addLine("}");
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
}
