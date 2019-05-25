package com.github.ahhoefel.ast;

import com.github.ahhoefel.Token;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.GotoOp;
import com.github.ahhoefel.ir.operation.PushOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;

public class FunctionInvocationExpression implements Expression {

  private List<Expression> args;
  private String identifier;
  private Register register;
  private SymbolCatalog symbols;

  public FunctionInvocationExpression(Token identifier, List<Expression> args) {
    this.identifier = identifier.getValue();
    this.args = args;
    this.register = new Register();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add(identifier);
    out.add("(");
    for (int i = 0; i < args.size(); i++) {
      args.get(i).toIndentedString(out);
      if (i != args.size() - 1) {
        out.add(", ");
      }
    }
    out.add(")");
  }

  @Override
  public void addToRepresentation(Representation rep) {
    for (Expression arg : args) {
      arg.addToRepresentation(rep);
    }
    for (Expression arg : args) {
      rep.add(new PushOp(arg.getRegister()));
    }
    rep.add(new GotoOp(symbols.getFunction(identifier).getLabel()));
  }
}
