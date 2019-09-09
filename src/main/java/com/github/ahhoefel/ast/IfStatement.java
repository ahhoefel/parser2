package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.CommentOp;
import com.github.ahhoefel.ir.operation.ConditionalGotoOp;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.NegateOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;

public class IfStatement implements Statement {

  private final Block block;
  private final Expression condition;
  private final Label destination;
  private final Register negation;

  public IfStatement(Expression condition, Block block) {
    this.condition = condition;
    this.block = block;
    this.destination = new Label();
    this.negation = new Register();
  }

  @Override
  public void addToSymbolCatalog(SymbolCatalog symbols) {
    condition.setSymbolCatalog(symbols);
    block.addToSymbolCatalog(symbols);
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("if ");
    condition.toIndentedString(out);
    out.add(" {").endLine();
    block.toIndentedString(out.indent());
    out.addLine("}");
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
  public void typeCheck() {
    if (condition.getType() != Type.BOOL) {
      throw new RuntimeException("If condition should be boolean. Got: " + condition.getType());
    }
    block.typeCheck();
  }
}
