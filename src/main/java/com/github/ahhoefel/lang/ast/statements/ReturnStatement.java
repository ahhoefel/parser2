package com.github.ahhoefel.lang.ast.statements;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.ParseError;
import com.github.ahhoefel.lang.ast.SymbolCatalogOld;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.parser.ErrorLog;

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
  public void addToSymbolCatalog(SymbolCatalogOld symbols) {
    functionDeclaration = symbols.getContainingFunction().get();
    expression.setSymbolCatalog(symbols);
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  public Expression getExpression() {
    return expression;
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
      log.add(new ParseError(null, "Type mismatch. Return type " + functionDeclaration.getReturnType()
          + ", return statement " + expression.getType()));
    }
    // returnDestination.setWidth(functionDeclaration.getReturnType().width());
  }
}
