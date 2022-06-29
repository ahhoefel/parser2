package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.ir.Register;
import com.github.ahhoefel.ir.Representation;

import java.util.List;

public interface LValueExpression extends Expression {
  void addToRepresentationAsLValue(Representation rep, List<Register> liveRegisters, Expression value);
}
