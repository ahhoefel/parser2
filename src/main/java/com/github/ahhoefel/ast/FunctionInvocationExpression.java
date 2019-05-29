package com.github.ahhoefel.ast;

import com.github.ahhoefel.Token;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.util.IndentedString;

import java.util.Iterator;
import java.util.List;

public class FunctionInvocationExpression implements Expression {

  private List<Expression> args;
  private String identifier;
  private Register register;
  private SymbolCatalog symbols;
  private Register returnLabelRegister;
  private Label returnLabel;

  public FunctionInvocationExpression(Token identifier, List<Expression> args) {
    this.identifier = identifier.getValue();
    this.args = args;
    this.register = new Register();
    this.returnLabelRegister = new Register();
    this.returnLabel = new Label();
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
    for (Expression arg : args) {
      arg.setSymbolCatalog(symbols);
    }
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
    rep.add(new CommentOp("Invoking " + identifier));
    rep.add(new CommentOp("Pushing local vars"));
    Iterator<VariableDeclaration> iter = symbols.getVariablesInOrder();
    while (iter.hasNext()) {
      rep.add(new PushOp(iter.next().getRegister()));
    }
    rep.add(new CommentOp("Pushing return label"));
    rep.add(new LiteralLabelOp(returnLabel, returnLabelRegister));
    rep.add(new PushOp(returnLabelRegister));
    rep.add(new CommentOp("Pushing arguments"));
    for (Expression arg : args) {
      rep.add(new PushOp(arg.getRegister()));
    }
    FunctionDeclaration fn = symbols.getFunction(identifier);
    rep.add(new CommentOp("Jumping to function " + identifier));
    rep.add(new GotoOp(fn.getLabel()));
    rep.add(new DestinationOp(returnLabel));
    rep.add(new CommentOp("Popping return value"));
    rep.add(new PopOp(register));
    iter = symbols.getVariablesInOrder();
    // Should be in reverse order.
    rep.add(new CommentOp("Popping local variables"));
    while (iter.hasNext()) {
      rep.add(new PopOp(iter.next().getRegister()));
    }
    rep.add(new CommentOp("Ending invocation of function " + identifier));
  }
}
