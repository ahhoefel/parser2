package com.github.ahhoefel.lang.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class File implements Visitable {

    private Target target;
    private ImportCatalog imports;
    private List<Declaration> declarations;
    private Map<String, FunctionDeclaration> functions;
    private List<TypeDeclaration> types;

    public File() {
        imports = new ImportCatalog();
        declarations = new ArrayList<>();
        functions = new HashMap<>();
        types = new ArrayList<>();
    }

    public String toString() {
        String out = "File {\n";
        out += "\ttarget: " + target + "\n";
        for (Map.Entry<String, FunctionDeclaration> entry : functions.entrySet()) {
            out += "\tfunction {\n";
            out += "\t\tkey: " + entry.getKey() + "\n";
            out += "\t\tvalue: " + entry.getValue() + "\n";
            out += "\t}\n";
        }
        out += "}\n";
        return out;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public Optional<FunctionDeclaration> getFunction(String name) {
        return Optional.ofNullable(functions.get(name));
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public void addFunction(FunctionDeclaration f) {
        System.out.println("Added function " + f.getName() + " to file.");
        declarations.add(f);
        functions.put(f.getName(), f);
    }

    public void addType(TypeDeclaration t) {
        declarations.add(t);
        types.add(t);
    }

    public void addImport(Import imp0rt) {
        imports.add(imp0rt);
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public ImportCatalog getImports() {
        return imports;
    }
}
