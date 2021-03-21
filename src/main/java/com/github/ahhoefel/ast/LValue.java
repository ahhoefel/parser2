package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.expression.LValueExpression;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.Token;

import java.util.List;
import java.util.Optional;

public class LValue implements Visitable {
  private boolean declaration;
  private String identifier;
  private Type type;

  private LValueExpression expression;

  private SymbolCatalog symbols;
  private CodeLocation location;

  public static LValue withDeclaration(Token identifer, Type type) {
    return new LValue(identifer, type);
  }

  private LValue(Token token, Type type) {
    this.declaration = true;
    this.identifier = token.getValue();
    this.location = token.getLocation();
    this.type = type;
  }

  public String toString() {
    return "var " + identifier;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public static LValue fromExpression(Expression e, CodeLocation location) {
    if (!(e instanceof LValueExpression)) {
      throw new RuntimeException("Not an lvalue expression");
    }
    return new LValue((LValueExpression) e, location);
  }

  private LValue(LValueExpression e, CodeLocation location) {
    this.declaration = false;
    this.expression = e;
    this.location = location;
  }

  public String getIdentifier() {
    return identifier;
  }

  public boolean isDeclaration() {
    return declaration;
  }

  public Expression getExpression() {
    return expression;
  }

  public CodeLocation getLocation() {
    return location;
  }

  public Optional<Type> checkType(ErrorLog log) {
    if (declaration) {
      return Optional.of(type);
    }
    Optional<Type> t = expression.checkType(log);
    if (!t.isPresent()) {
      log.add(new ParseError(location, "Failed to determine type of lvalue."));
      return t;
    }
    type = t.get();
    if (!expression.isLValue()) {
      log.add(new ParseError(location, "Expected lvalue."));
    }
    return Optional.of(type);
  }

  public Type getType() {
    return type;
  }

  // Code added before computation of the RHS.
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {
  }

  // Code added after computation of the RHS.
  public void addAssignmentToRepresentation(Representation rep, List<Register> liveRegisters, Expression value) {
    if (declaration) {
      Register register = symbols.getVariable(identifier).get().getRegister();
      rep.add(new SetOp(value.getRegister(), register, 0, 0, value.getType().width()));
      return;
    } else {
      expression.addToRepresentationAsLValue(rep, liveRegisters, value);
    }
  }

  public void setSymbolCatalog(SymbolCatalog symbols) {
    this.symbols = symbols;
    if (declaration) {
      symbols.addVariable(new VariableDeclaration(this));
    } else {
      expression.setSymbolCatalog(symbols);
    }
  }
}
