package com.github.ahhoefel.ast;

import java.util.*;

public class SymbolCatalog {

  private Optional<SymbolCatalog> parent;
  private Map<String, FunctionDeclaration> functions;
  private Map<String, VariableDeclaration> variables;
  private List<VariableDeclaration> orderedVariables;
  private String scopeName;
  private boolean functionDeclaration;

  public SymbolCatalog(String scopeName, boolean functionDeclaration) {
    this.scopeName = scopeName;
    this.functionDeclaration = functionDeclaration;
    functions = new HashMap<>();
    variables = new HashMap<>();
    parent = Optional.empty();
    orderedVariables = new ArrayList<>();
  }

  public SymbolCatalog(String scopeName, SymbolCatalog parent, boolean functionDeclaration) {
    this(scopeName, functionDeclaration);
    this.parent = Optional.ofNullable(parent);
  }

  public void addVariable(VariableDeclaration variable) {
    System.out.println("Variable added to symbol table: " + variable);
    variables.put(variable.getName(), variable);
    orderedVariables.add(variable);
  }

  public VariableDeclaration getVariable(String name) {
    Optional<VariableDeclaration> variable = getVariableInternal(name);
    if (!variable.isPresent()) {
      throw new RuntimeException(String.format("Variable %s not declared\n%s", name, this.toString()));
    }
    return variable.get();
  }

  private Optional<VariableDeclaration> getVariableInternal(String name) {
    Optional<VariableDeclaration> variable = Optional.ofNullable(variables.get(name));
    if (!variable.isPresent() && !functionDeclaration) {
      if (!parent.isPresent()) {
        throw new RuntimeException("Expected parent to be present");
      }
      variable = parent.get().getVariableInternal(name);
    }
    return variable;
  }

  public VariableIterator getVariablesInOrder() {
    return new VariableIterator();
  }

  private class VariableIterator implements Iterator<VariableDeclaration> {
    private int index;
    private Optional<VariableIterator> parentIter;

    public VariableIterator() {
      index = 0;
      if (!functionDeclaration && parent.isPresent()) {
        parentIter = Optional.of(parent.get().getVariablesInOrder());
      } else {
        parentIter = Optional.empty();
      }
    }

    @Override
    public boolean hasNext() {
      return index < orderedVariables.size() || (parentIter.isPresent() && parentIter.get().hasNext());
    }

    @Override
    public VariableDeclaration next() {
      if (index < orderedVariables.size()) {
        return orderedVariables.get(index++);
      }
      return parentIter.get().next();
    }
  }

  public void addFunction(FunctionDeclaration function) {
    functions.put(function.getName(), function);
  }

  public FunctionDeclaration getFunction(String name) {
    FunctionDeclaration out = functions.get(name);
    if (out != null) {
      return out;
    }
    if (parent.isPresent()) {
      return parent.get().getFunction(name);
    }

    return null;
  }

  public void toStringBuilder(StringBuilder builder) {
    builder.append("Scope ").append(scopeName).append("\n");
    for (String var : variables.keySet()) {
      builder.append("  var  ").append(var).append("\n");
    }
    for (String fn : functions.keySet()) {
      builder.append("  func ").append(fn).append("\n");
    }
    if (parent.isPresent()) {
      builder.append("Parent ");
      parent.get().toStringBuilder(builder);
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    toStringBuilder(out);
    return out.toString();
  }
}
