package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;
import java.util.List;

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
    rep.add(new SetOp(expression.getRegister(), lvalue.getRegister()));
  }

  @Override
  public void typeCheck() {
    if (!lvalue.getType().equals(expression.getType())) {
      throw new RuntimeException("Type mismatch: " + lvalue.getType() + " = " + expression.getType());
    }
  }
}
