package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.DebugFunctionReturnOp;
import com.github.ahhoefel.ir.operation.GotoRegisterOp;
import com.github.ahhoefel.ir.operation.PopOp;
import com.github.ahhoefel.ir.operation.PushOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;

public class ReturnStatement implements Statement {

  private Expression expression;
  private Register returnDestination;

  public ReturnStatement(Expression expression) {
    this.expression = expression;
    returnDestination = new Register();
  }

  @Override
  public void addToSymbolCatalog(SymbolCatalog symbols) {
    expression.setSymbolCatalog(symbols);
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add("return ");
    expression.toIndentedString(out);
    out.endLine();
  }

  @Override
  public void addToRepresentation(Representation rep) {
    expression.addToRepresentation(rep, new ArrayList<>());
    rep.add(new DebugFunctionReturnOp(expression));
    rep.add(new PopOp(returnDestination));
    rep.add(new PushOp(expression.getRegister()));
    rep.add(new GotoRegisterOp(returnDestination));
  }
}
