package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructLiteralExpression implements Expression {

  private Type type;
  private Map<String, Expression> values;
  private Register register;

  public StructLiteralExpression(Type type) {
    this.type = type;
    this.values = new HashMap<>();
    this.register = new Register();
  }

  public void add(String identifier, Expression value) {
    assert !values.containsKey(identifier);
    values.put(identifier, value);
  }

  @Override
  public Register getRegister() {
    return register;
  }

  @Override
  public void toIndentedString(IndentedString out) {
    out.add(type.toString()).add("{");
    out.endLine();
    out.indent();
    for (Map.Entry<String, Expression> entry : values.entrySet()) {
      out.add(entry.getKey()).add(": ");
      entry.getValue().toIndentedString(out);
      out.endLine();
    }
    out.endLine();
  }

  @Override
  public void setSymbolCatalog(SymbolCatalog symbols) {
    for (Expression e : values.values()) {
      e.setSymbolCatalog(symbols);
    }
  }

  @Override
  public void addToRepresentation(Representation rep, List<Register> liveRegisters) {

  }

  @Override
  public Type getType() {
    throw new RuntimeException("Expressions in struct literal not type checked yet.");
  }
}
