package com.github.ahhoefel.ast;

import com.github.ahhoefel.Token;
import com.github.ahhoefel.ir.Label;
import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.DestinationOp;
import com.github.ahhoefel.ir.operation.PopOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionDeclaration {

  private String name;
  private List<Parameter> parameterList;
  private Block statements;
  private Label label;
  private SymbolCatalog symbols;

  public FunctionDeclaration(Token name, List<Parameter> parameterList, Optional<Type> returnType, Block statements) {
    this.name = name.getValue();
    this.parameterList = parameterList;
    this.statements = statements;
    this.label = new Label();

  }

  public String getName() {
    return name;
  }

  public Label getLabel() {
    return label;
  }

  public void addToRepresentation(Representation rep) {
    Map<String, Register> variables = new HashMap<>();
    for (int i = 0; i < parameterList.size(); i++) {
      Parameter param = parameterList.get(i);
      Register reg = new Register();
      variables.put(param.name, reg);
      if (i == 0) {
        Label label = symbols.getFunction(name).getLabel();
        rep.add(new DestinationOp(label));
      }
      rep.add(new PopOp(reg));
    }
    statements.addToRepresentation(rep);
  }

  public void toIndentedString(IndentedString out) {
    out.add("func ");
    out.add(name);
    out.add("(");
    for (int i = 0; i < parameterList.size(); i++) {
      parameterList.get(i).toIndentedString(out);
      if (i != parameterList.size() - 1) {
        out.add(", ");
      }
    }
    out.add(") {");
    out.endLine();
    statements.toIndentedString(out.indent());
    out.addLine("}");
  }
}
