package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.interpreter.Alloc;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.CommentOp;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.GotoRegisterOp;
import com.github.ahhoefel.ir.operation.PopOp;
import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Calling conventions.
 * <p>
 * FunctionInvocation: - store all local variables to the stack. - put return
 * pointer on the stack - put all parameters on the stack.
 * <p>
 * FunctionDeclaration: - pop all parameters into registers - execute body
 * Return statement: - pop return pointer - push return values
 * <p>
 * FunctionInvocation at return pointer: - pop return values into registers
 */
public class FunctionDeclaration implements Declaration {

  private String name;
  private List<VariableDeclaration> parameters;
  private Block statements;
  private Label label;
  private SymbolCatalogOld symbols;
  private Optional<Type> returnType;
  private Register returnLabelRegister;

  public FunctionDeclaration(Token name, List<VariableDeclaration> parameters, Optional<Type> returnType,
      Block statements) {
    this.name = name.getValue();
    this.parameters = parameters;
    this.statements = statements;
    this.label = new Label();
    this.returnType = returnType;
    this.returnLabelRegister = new Register();
  }

  public void accept(Visitor v, Object... objs) {
    v.visit(this, objs);
  }

  public String getName() {
    return name;
  }

  public Label getLabel() {
    return label;
  }

  public Block getBlock() {
    return statements;
  }

  public Type getReturnType() {
    if (!returnType.isPresent()) {
      return Type.VOID;
    }
    return returnType.get();
  }

  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<>();
    for (VariableDeclaration param : parameters) {
      types.add(param.getType());
    }
    return types;
  }

  public String getParameterName(int i) {
    return parameters.get(i).getName();
  }

  public void setSymbolCatalog(SymbolCatalogOld parent) {
    parent.addFunction(this);
    this.symbols = new SymbolCatalogOld(name, parent, Optional.of(this));
    for (VariableDeclaration param : parameters) {
      symbols.addVariable(param);
    }
    this.statements.setSymbolCatalog(symbols);
  }

  public String formatParametersWithValues(List<Alloc> values) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < parameters.size(); i++) {
      out.append(parameters.get(i).getName());
      out.append(" ");
      out.append(parameters.get(i).getType().toString());
      out.append("[");
      out.append(values.get(i));
      out.append("]");
    }
    return out.toString();
  }

  public void addToRepresentation(Representation rep) {
    rep.add(new CommentOp("func " + name));
    rep.add(new DestinationOp(label));
    for (int i = parameters.size() - 1; i >= 0; i--) {
      rep.add(new PopOp(parameters.get(i).getRegister()));
    }
    statements.addToRepresentation(rep);
    // if (!returnType.isPresent() && returnType.get().equals(Type.VOID)) {
    rep.add(new PopOp(returnLabelRegister));
    rep.add(new GotoRegisterOp(returnLabelRegister));
    rep.add(new CommentOp("end func " + name));
  }

  @Override
  public File addToFile(File file) {
    file.addFunction(this);
    return file;
  }

  public void typeCheck(ErrorLog log) {
    this.statements.typeCheck(log);
  }
}
