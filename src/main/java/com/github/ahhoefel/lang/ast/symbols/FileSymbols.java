package com.github.ahhoefel.lang.ast.symbols;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.ahhoefel.arm.Label;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;
import com.github.ahhoefel.util.IndentedString;

public class FileSymbols {

    private Target target;
    private Map<String, Target> imports;
    private Map<String, Expression> types;
    private Map<String, FunctionDefinition> functions;
    private List<SymbolReference> symbolReferences;

    public FileSymbols(Target target) {
        this.target = target;
        imports = new HashMap<>();
        functions = new HashMap<>();
        types = new HashMap<>();
        symbolReferences = new ArrayList<>();
    }

    public Collection<FunctionDefinition> getFunctions() {
        return functions.values();
    }

    public void addImport(String shortName, Target target) {
        imports.put(shortName, target);
    }

    public Optional<Target> getImport(String shortName) {
        return Optional.ofNullable(imports.get(shortName));
    }

    public Collection<Target> getImports() {
        return imports.values();
    }

    public void addFunction(FunctionDeclaration function, LocalSymbols localSymbols) {
        functions.put(function.getName(), new FunctionDefinition(function, localSymbols));
    }

    public Optional<FunctionDefinition> getFunction(String name) {
        return Optional.ofNullable(functions.get(name));
    }

    public void addType(String identifier, Expression type) {
        this.types.put(identifier, type);
    }

    public Target getTarget() {
        return target;
    }

    public void addSymbolReference(SymbolReference ref) {
        this.symbolReferences.add(ref);
    }

    public boolean resolve() {
        boolean resolved = true;
        for (SymbolReference ref : symbolReferences) {
            resolved = resolved && ref.resolve();
        }
        return resolved;
    }

    public void toIndentedString(IndentedString out) {
        out.add("File: ").add(target.toString()).endLine().indent();
        out.addLine("Imports:").indent();
        listImports(out);
        out.unindent();

        out.addLine("Functions:").indent();
        listFunctions(out);
        out.unindent();

        out.addLine("Symbol References:").indent();
        listSymbolReferences(out);
        out.unindent();

        out.unindent();
    }

    public void listImports(IndentedString out) {
        List<Target> targets = new ArrayList<>(imports.values());
        targets.sort((o1, o2) -> {
            Target t1 = (Target) o1;
            Target t2 = (Target) o2;
            return t1.getFilePath().compareTo(t2.getFilePath());
        });
        for (Target t : targets) {
            out.addLine(t.toString());
        }
    }

    public void listFunctions(IndentedString out) {
        List<String> fns = new ArrayList<>(functions.keySet());
        Collections.sort(fns);
        for (String fn : fns) {
            out.addLine(fn);
            out.indent();
            out.addLine("Local symbol table:");
            out.indent();
            functions.get(fn).localSymbols.toIndentedString(out);
            out.unindent();
            out.unindent();
        }
    }

    public void listSymbolReferences(IndentedString out) {
        for (SymbolReference ref : this.symbolReferences) {
            out.addLine(ref.toString());
        }
    }

    public static class FunctionDefinition {
        private FunctionDeclaration declaration;
        private LocalSymbols localSymbols;
        private RegisterScope registerScope;
        private Label returnLabel;
        private RegisterTracker returnProgramCounterRegister;

        private static int FUNCTION_COUNTER = 0;

        public FunctionDefinition(FunctionDeclaration declaration, LocalSymbols localSymbols) {
            this.declaration = declaration;
            this.localSymbols = localSymbols;
            this.registerScope = new RegisterScope();
            this.returnLabel = new Label("fn_return_" + FUNCTION_COUNTER++);
        }

        public FunctionDeclaration getDeclaration() {
            return declaration;
        }

        public LocalSymbols getLocalSymbols() {
            return localSymbols;
        }

        public RegisterScope getRegisterScope() {
            return registerScope;
        }

        public Label getReturnLabel() {
            return returnLabel;
        }

        public void setReturnProgramCounterRegister(RegisterTracker returnProgramCounterRegister) {
            this.returnProgramCounterRegister = returnProgramCounterRegister;
        }

        public RegisterTracker getReturnProgramCounterRegister() {
            return returnProgramCounterRegister;
        }
    }
}
