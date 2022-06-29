package com.github.ahhoefel.lang.ast;

import java.util.*;
import java.util.stream.Collectors;

import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.ErrorLog;

public class SymbolCatalogOld {

  private Optional<SymbolCatalogOld> parent;
  private ImportCatalog imports;
  private Map<String, FunctionDeclaration> functions;
  private Map<String, VariableDeclaration> variables;
  private Map<String, TypeDeclaration> types;
  private List<VariableDeclaration> orderedVariables;
  private String scopeName;
  private Optional<FunctionDeclaration> functionDeclaration;

  public SymbolCatalogOld(String scopeName, ImportCatalog imports, Optional<FunctionDeclaration> functionDeclaration) {
    this.scopeName = scopeName;
    this.functionDeclaration = functionDeclaration;
    functions = new HashMap<>();
    variables = new HashMap<>();
    types = new HashMap<>();
    parent = Optional.empty();
    this.imports = imports;
    orderedVariables = new ArrayList<>();
  }

  public SymbolCatalogOld(String scopeName, SymbolCatalogOld parent,
      Optional<FunctionDeclaration> functionDeclaration) {
    this(scopeName, parent.imports, functionDeclaration);
    this.parent = Optional.ofNullable(parent);
  }

  public void addVariable(VariableDeclaration variable) {
    // System.out.println("Variable added to symbol table: " + variable);
    variables.put(variable.getName(), variable);
    orderedVariables.add(variable);
  }

  public void addType(TypeDeclaration type) {
    types.put(type.getIdentifier(), type);
  }

  public Optional<VariableDeclaration> getVariable(String name) {
    Optional<VariableDeclaration> variable = Optional.ofNullable(variables.get(name));
    if (!variable.isPresent() && !functionDeclaration.isPresent()) {
      if (!parent.isPresent()) {
        throw new RuntimeException("Expected parent to be present");
      }
      variable = parent.get().getVariable(name);
    }
    return variable;
  }

  public VariableIterator getVariablesInOrder() {
    return new VariableIterator();
  }

  public ReverseVariableIterator getVariablesInReverseOrder() {
    return new ReverseVariableIterator();
  }

  private class VariableIterator implements Iterator<VariableDeclaration> {
    private int index;
    private Optional<VariableIterator> parentIter;

    public VariableIterator() {
      index = 0;
      if (!functionDeclaration.isPresent() && parent.isPresent()) {
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

  private class ReverseVariableIterator implements Iterator<VariableDeclaration> {
    private int index;
    private Optional<ReverseVariableIterator> parentIter;

    public ReverseVariableIterator() {
      index = orderedVariables.size() - 1;
      if (!functionDeclaration.isPresent() && parent.isPresent()) {
        parentIter = Optional.of(parent.get().getVariablesInReverseOrder());
      } else {
        parentIter = Optional.empty();
      }
    }

    @Override
    public boolean hasNext() {
      return index >= 0 || (parentIter.isPresent() && parentIter.get().hasNext());
    }

    @Override
    public VariableDeclaration next() {
      if (parentIter.isPresent() && parentIter.get().hasNext()) {
        return parentIter.get().next();
      }
      return orderedVariables.get(index--);
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

  public SymbolCatalogOld getImport(String shortName) {
    return this.imports.get(shortName).getSymbols();
  }

  public Type getType(Optional<String> packageName, String typeName, ErrorLog log) {
    SymbolCatalogOld symbols = this;
    if (packageName.isPresent()) {
      symbols = imports.get(packageName.get()).getSymbols();
    }
    TypeDeclaration typeDeclaration = symbols.types.get(typeName);
    if (typeDeclaration == null) {
      String error = "Could not find symbol ";
      if (packageName.isPresent()) {
        error += packageName.get() + ".";
      }
      error += typeName + ". ";
      error += "Known types: ";
      error += String.join(", ", symbols.types.keySet().stream().map(String::toString).collect(Collectors.toList()));

      log.add(new ParseError(null, error));
      return null;
    }
    return typeDeclaration.getType();
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

    for (Map.Entry<String, FunctionDeclaration> entry : functions.entrySet()) {
      builder.append("func ").append(entry.getKey()).append('\n');
    }

    for (Map.Entry<String, TypeDeclaration> entry : types.entrySet()) {
      builder.append(entry.getValue());
      builder.append('\n');
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    toStringBuilder(out);
    return out.toString();
  }

  public Optional<FunctionDeclaration> getContainingFunction() {
    Optional<SymbolCatalogOld> catalog = Optional.of(this);
    while (catalog.isPresent() && !catalog.get().functionDeclaration.isPresent()) {
      catalog = catalog.get().parent;
    }
    if (!catalog.isPresent()) {
      return Optional.empty();
    }
    return catalog.get().functionDeclaration;
  }
}
