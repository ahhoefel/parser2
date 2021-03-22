package com.github.ahhoefel.ast.expression;

import com.github.ahhoefel.ast.*;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.*;
import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.Token;

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
  private Type type;
  private CodeLocation location;

  public FunctionInvocationExpression(Token identifier, List<Expression> args) {
    this.identifier = identifier.getValue();
    this.args = args;
    this.register = new Register();
    this.returnLabelRegister = new Register();
    this.returnLabel = new Label();
    this.implicitArg = Optional.empty();
    this.location = identifier.getLocation();
  }

  public void setImplicitArg(Expression implicitArg) {
    this.implicitArg = Optional.of(implicitArg);
  }

  public String getIdentifier() {
    return identifier;
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  public Optional<Expression> getImplicitArg() {
    return implicitArg;
  }

  public List<Expression> getArgs() {
    return args;
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
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
    FunctionDeclaration fn = getDeclaration();

    for (Expression arg : args) {
      arg.addToRepresentation(rep, liveRegisters);
    }
    // rep.add(new DebugFunctionCallOp(fn, args));
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
      arg.removeLiveRegisters(liveRegisters);
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
    if (type != Type.VOID) {
      rep.add(new CommentOp("Popping return value"));
      rep.add(new PopOp(register));
    } else {
      rep.add(new CommentOp("Void return type. Not popping."));
    }

    rep.add(new CommentOp("Popping live registers, except args"));
    for (int i = 0; i < liveRegisters.size(); i++) {
      rep.add(new PopOp(liveRegisters.get(liveRegisters.size() - i - 1)));
    }
    addLiveRegisters(liveRegisters);

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

  @Override
  public void addLiveRegisters(List<Register> stack) {
    stack.add(register);
  }

  @Override
  public void removeLiveRegisters(List<Register> stack) {
    stack.remove(stack.size() - 1);
  }

  private FunctionDeclaration getDeclaration() {
    SymbolCatalog catalog = symbols;
    if (implicitArg.isPresent()) {
      if (!(implicitArg.get() instanceof VariableExpression)) {
        throw new RuntimeException("Calling functions on expression not implemented.");
      }
      // Treating the variable expression as a package.
      VariableExpression pkg = (VariableExpression) implicitArg.get();
      catalog = symbols.getImport(pkg.getIdentifier());
    }
    return catalog.getFunction(identifier);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    FunctionDeclaration declaration = getDeclaration();
    List<Type> paramTypes = declaration.getParameterTypes();
    if (args.size() != paramTypes.size()) {
      log.add(new ParseError(location, String.format(
          "Number of parameters (%d) does not match number of arguments (%d)", paramTypes.size(), args.size())));
    } else {
      for (int i = 0; i < args.size(); i++) {
        Optional<Type> type = args.get(i).checkType(log);
        if (!type.get().equals(paramTypes.get(i))) {
          log.add(new ParseError(location, String.format("Type mismatch: %s passed to %s parameter %s", type.get(),
              paramTypes.get(i), declaration.getParameterName(i))));
        }
      }
    }
    type = declaration.getReturnType();
    register.setWidth(type.width());
    return Optional.of(type);
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public boolean isLValue() {
    return false;
  }
}
