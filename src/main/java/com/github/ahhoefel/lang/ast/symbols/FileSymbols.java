package com.github.ahhoefel.lang.ast.symbols;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.util.IndentedString;

public class FileSymbols {

    private Target target;
    private Map<Target, FileSymbols> imports;
    private Map<String, Type> types;
    private Map<String, FunctionDefinition> functions;

    public FileSymbols(Target target) {
        this.target = target;
        imports = new HashMap<>();
        functions = new HashMap<>();
        types = new HashMap<>();
    }

    public void addImport(Target target, FileSymbols symbols) {
        imports.put(target, symbols);
    }

    public void addFunction(FunctionDeclaration function) {
        functions.put(function.getName(), new FunctionDefinition(function));
    }

    public Optional<FunctionDefinition> getFunction(String name) {
        return Optional.ofNullable(functions.get(name));
    }

    public void addType(String identifier, Type type) {
        this.types.put(identifier, type);
    }

    public Target getTarget() {
        return target;
    }

    public void toIndentedString(IndentedString out) {
        out.add("File: ").add(target.toString()).endLine().indent();
        out.addLine("Imports:").indent();
        listImports(out);
        out.unindent();

        out.addLine("Functions:").indent();
        listFunctions(out);
        out.unindent();

        out.unindent();
    }

    public void listImports(IndentedString out) {
        List<Target> targets = new ArrayList<>(imports.keySet());
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
        }
    }

    public static class FunctionDefinition {
        private FunctionDeclaration declaration;

        public FunctionDefinition(FunctionDeclaration declaration) {
            this.declaration = declaration;
        }

        public FunctionDeclaration getDeclaration() {
            return declaration;
        }
    }
}
