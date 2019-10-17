package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;

public class ReturnStatement implements Statement {

  private Expression expression;
  private Register returnDestination;
  private FunctionDeclaration functionDeclaration;

  public ReturnStatement(Expression expression) {
    this.expression = expression;
    returnDestination = new Register();
  }

  @Override
  public void addToSymbolCatalog(SymbolCatalog symbols) {
    functionDeclaration = symbols.getContainingFunction().get();
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
    rep.add(new CommentOp("Return expression"));
    expression.addToRepresentation(rep, new ArrayList<>());
    rep.add(new DebugFunctionReturnOp(expression));
    rep.add(new PopOp(returnDestination));
    rep.add(new PushOp(expression.getRegister()));
    rep.add(new GotoRegisterOp(returnDestination));
  }

  @Override
  public void typeCheck(ErrorLog log) {
    if (!functionDeclaration.getReturnType().equals(expression.checkType(log).get())) {
      log.add(new ParseError(null, "Type mismatch. Return type " + functionDeclaration.getReturnType() + ", return statement " + expression.getType()));
    }
    //returnDestination.setWidth(functionDeclaration.getReturnType().width());
  }
}
