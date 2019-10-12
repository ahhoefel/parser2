package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.util.IndentedString;

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

  @Override
  public void toIndentedString(IndentedString out) {
    out.add(lvalue.toString());
    out.add(" = ");
    expression.toIndentedString(out);
    out.endLine();
  }

  @Override
  public void addToRepresentation(Representation rep) {
    List<Register> liveRegisters = new ArrayList<>();
    expression.addToRepresentation(rep, liveRegisters);
    if (expression.getType() == null) {
      System.out.println(lvalue.getLocation());
    }
    rep.add(new SetOp(expression.getRegister(), lvalue.getRegister(), 0, 0, expression.getType().width()));
  }

  @Override
  public void typeCheck(ErrorLog log) {
    Optional<Type> exprType = expression.checkType(log);
    if (!exprType.isPresent()) {
      return;
    }

    if (!lvalue.getType(log).equals(exprType.get())) {
      log.add(new ParseError(lvalue.getLocation(), "Type mismatch: " + lvalue.getType() + " = " + expression.getType()));
    }
  }
}
