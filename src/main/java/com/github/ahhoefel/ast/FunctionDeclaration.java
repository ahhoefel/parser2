package com.github.ahhoefel.ast;

import com.github.ahhoefel.interpreter.Alloc;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.CommentOp;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.GotoRegisterOp;
import com.github.ahhoefel.ir.operation.PopOp;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.util.IndentedString;

import java.util.List;
import java.util.Optional;

/**
 * Calling conventions.
 * <p>
 * FunctionInvocation:
 * - store all local variables to the stack.
 * - put return pointer on the stack
 * - put all parameters on the stack.
 * <p>
 * FunctionDeclaration:
 * - pop all parameters into registers
 * - execute body
 * Return statement:
 * - pop return pointer
 * - push return values
 * <p>
 * FunctionInvocation at return pointer:
 * - pop return values into registers
 */
public class FunctionDeclaration implements Declaration {

  private String name;
  private List<VariableDeclaration> parameters;
  private Block statements;
  private Label label;
  private SymbolCatalog symbols;
  private Optional<Type> returnType;
  private Register returnLabelRegister;

  public FunctionDeclaration(Token name, List<VariableDeclaration> parameters, Optional<Type> returnType, Block statements) {
    this.name = name.getValue();
    this.parameters = parameters;
    this.statements = statements;
    this.label = new Label();
    this.returnType = returnType;
    this.returnLabelRegister = new Register();
  }

  public String getName() {
    return name;
  }

  public Label getLabel() {
    return label;
  }

  public Type getReturnType() {
    if (!returnType.isPresent()) {
      return Type.VOID;
    }
    return returnType.get();
  }

  public void setSymbolCatalog(SymbolCatalog parent) {
    parent.addFunction(this);
    this.symbols = new SymbolCatalog(name, parent, Optional.of(this));
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
    for (VariableDeclaration param : parameters) {
      rep.add(new PopOp(param.getRegister()));
    }
    statements.addToRepresentation(rep);
    if (!returnType.isPresent()) {
      rep.add(new PopOp(returnLabelRegister));
      rep.add(new GotoRegisterOp(returnLabelRegister));
    }
    rep.add(new CommentOp("end func " + name));
  }

  public void toIndentedString(IndentedString out) {
    out.add("func ");
    out.add(name);
    out.add("(");
    for (int i = 0; i < parameters.size(); i++) {
      out.add(parameters.get(i).toString());
      if (i != parameters.size() - 1) {
        out.add(", ");
      }
    }
    out.add(") {");
    out.endLine();
    statements.toIndentedString(out.indent());
    out.addLine("}");
  }

  @Override
  public RaeFile addToFile(RaeFile file) {
    file.addFunction(this);
    return file;
  }

  public void typeCheck() {
    this.statements.typeCheck();
  }
}
