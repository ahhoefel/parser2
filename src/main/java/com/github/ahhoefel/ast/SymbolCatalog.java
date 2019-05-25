package com.github.ahhoefel.ast;

import java.util.HashMap;
import java.util.Map;

public class SymbolCatalog {

  public Map<String, FunctionDeclaration> functions;
  public Map<String, VariableDeclaration> variables;

  public SymbolCatalog() {
    functions = new HashMap<>();
    variables = new HashMap<>();
  }

  public void addVariable(VariableDeclaration variable) {
    System.out.println("Variable added to symbol table: " + variable);
    variables.put(variable.getName(), variable);
  }

  public VariableDeclaration getVariable(String name) {
    return variables.get(name);
  }

  public void addFunction(FunctionDeclaration function) {
    functions.put(function.getName(), function);
  }

  public FunctionDeclaration getFunction(String name) {
    return functions.get(name);
  }
}
