package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.ir.operation.SetOp;
import com.github.ahhoefel.util.IndentedString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StructLiteralExpression implements Expression {

  private Type type;
  private StructType structType;
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
    int destinationOffset = 0;
    for (String name : structType.memberNames()) {
      Type type = structType.getMember(name);
      Expression e = values.get(name);
      e.addToRepresentation(rep, liveRegisters);
      rep.add(new SetOp(e.getRegister(), register, 0, destinationOffset, e.getType().width()));
      destinationOffset += type.width();
    }
    liveRegisters.add(register);
  }

  @Override
  public Optional<Type> checkType(ErrorLog log) {
    if (structType != null) {
      return Optional.of(structType);
    }
    structType = StructType.toStructType(type);

    int width = 0;
    for (Map.Entry<String, Expression> entry : values.entrySet()) {
      Type memberType = structType.getMember(entry.getKey());
      Optional<Type> exprType = entry.getValue().checkType(log);
      if (!exprType.isPresent()) {
        return Optional.empty();
      }
      if (!memberType.equals(exprType.get())) {
        log.add(new ParseError(null, "Struct literal member " + entry.getKey() + " expected to be type " + memberType + ". Got " + exprType + "."));
        return Optional.empty();
      }
      width += memberType.width();
    }
    this.register.setWidth(width);
    return Optional.of(structType);
  }

  @Override
  public Type getType() {
    return type;
  }
}
