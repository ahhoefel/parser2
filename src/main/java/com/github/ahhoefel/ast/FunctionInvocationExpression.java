package com.github.ahhoefel.ast;

import com.github.ahhoefel.Token;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.util.IndentedString;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class FunctionInvocationExpression implements Expression {

  private Optional<Expression> implicitArg;
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
    this.implicitArg = Optional.empty();
  }

  public void setImplicitArg(Expression implicitArg) {
    this.implicitArg = Optional.of(implicitArg);
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
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    SymbolCatalog catalog = symbols;
    if (implicitArg.isPresent()) {
      if (!(implicitArg.get() instanceof VariableExpression)) {
        throw new RuntimeException("Calling functions on expression not implemented.");
      }
      // Treating the variable expression as a package.
      VariableExpression pkg = (VariableExpression) implicitArg.get();
      catalog = symbols.getImport(pkg.getIdentifier());
    }
    FunctionDeclaration fn = catalog.getFunction(identifier);

    for (Expression arg : args) {
      arg.addToRepresentation(rep, liveRegisters);
    }
    rep.add(new DebugFunctionCallOp(fn, args));
    rep.add(new CommentOp("Invoking " + identifier));

    rep.add(new CommentOp("Pushing local vars"));
    Iterator<VariableDeclaration> iter = symbols.getVariablesInOrder();
    while (iter.hasNext()) {
      VariableDeclaration var = iter.next();
      rep.add(new CommentOp("Push " + var.getName()));
      rep.add(new PushOp(var.getRegister()));
    }

    // Remove args from the live registers.
    for (Expression arg : args) {
      liveRegisters.remove(liveRegisters.size() - 1);
    }

    rep.add(new CommentOp("Pushing live registers"));
    for (Register reg : liveRegisters) {
      rep.add(new PushOp(reg));
    }

    rep.add(new CommentOp("Pushing return label"));
    rep.add(new LiteralLabelOp(returnLabel, returnLabelRegister));
    rep.add(new PushOp(returnLabelRegister));

    // Remove args from the live registers.
    for (Expression arg : args) {
      rep.add(new PushOp(arg.getRegister()));
    }

    rep.add(new CommentOp("Jumping to function " + identifier));
    rep.add(new GotoOp(fn.getLabel()));
    rep.add(new DestinationOp(returnLabel));
    rep.add(new CommentOp("Popping return value"));
    rep.add(new PopOp(register));

    rep.add(new CommentOp("Popping live registers, except args"));
    for (int i = 0; i < liveRegisters.size(); i++) {
      rep.add(new PopOp(liveRegisters.get(liveRegisters.size() - i - 1)));
    }
    liveRegisters.add(register);

    iter = symbols.getVariablesInReverseOrder();
    // Should be in reverse order.
    rep.add(new CommentOp("Popping local variables"));
    while (iter.hasNext()) {
      VariableDeclaration var = iter.next();
      rep.add(new CommentOp("Popping local variable " + var.getName()));
      rep.add(new PopOp(var.getRegister()));
    }
    rep.add(new CommentOp("Ending invocation of function " + identifier));
  }
}
