package com.github.ahhoefel.ast.statements;

import com.github.ahhoefel.ast.LValue;
import com.github.ahhoefel.ast.ParseError;
import com.github.ahhoefel.ast.SymbolCatalog;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.parser.ErrorLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssignmentStatement implements Statement {

  private final LValue lvalue;
  private final Expression expression;

  public AssignmentStatement(LValue lvalue, Expression expression) {
    this.lvalue = lvalue;
    this.expression = expression;
  }

  @Override
  public void addToSymbolCatalog(SymbolCatalog symbols) {
    lvalue.setSymbolCatalog(symbols);
    expression.setSymbolCatalog(symbols);
  }

  public LValue getLValue() {
    return lvalue;
  }

  public Expression getExpression() {
    return expression;
  }

  @Override
  public void addToRepresentation(Representation rep) {
    List<Register> liveRegisters = new ArrayList<>();
    lvalue.addToRepresentation(rep, liveRegisters);
    expression.addToRepresentation(rep, liveRegisters);
    if (expression.getType() == null) {
      System.out.println(lvalue.getLocation());
    }
    lvalue.addAssignmentToRepresentation(rep, liveRegisters, expression);
  }

  @Override
  public void typeCheck(ErrorLog log) {
    Optional<Type> exprType = expression.checkType(log);
    if (!exprType.isPresent()) {
      return;
    }
    Optional<Type> type = lvalue.checkType(log);
    if (!type.isPresent()) {
      return;
    }
    if (!type.get().equals(exprType.get())) {
      log.add(
          new ParseError(lvalue.getLocation(), "Type mismatch: " + lvalue.getType() + " = " + expression.getType()));
    }
  }

  @Override
  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }
}
