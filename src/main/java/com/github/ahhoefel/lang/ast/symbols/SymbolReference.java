package com.github.ahhoefel.lang.ast.symbols;

import java.util.Optional;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols.FunctionDefinition;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols.LocalSymbol;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols.SymbolIndex;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;
import com.github.ahhoefel.lang.ast.symbols.TypeTable.TypeRecord;
import com.github.ahhoefel.lang.ast.type.Type;

public class SymbolReference {
    private static class ResolutionKey {
        private String name;
        private GlobalSymbols globals;
        private FileSymbols file;
        private LocalSymbols locals;
        private SymbolIndex localSymbolIndex;
        private Optional<Expression> subject;

        public ResolutionKey(String name, GlobalSymbols globals, FileSymbols file, LocalSymbols locals,
                SymbolIndex locaSymbolIndex) {
            this.name = name;
            this.globals = globals;
            this.file = file;
            this.locals = locals;
            this.localSymbolIndex = locaSymbolIndex;
            this.subject = Optional.empty();
        }

        public ResolutionKey(String name, GlobalSymbols globals, FileSymbols file, LocalSymbols locals,
                Expression subject,
                SymbolIndex locaSymbolIndex) {
            this.name = name;
            this.globals = globals;
            this.file = file;
            this.locals = locals;
            this.localSymbolIndex = locaSymbolIndex;
            this.subject = Optional.of(subject);
        }
    }

    private ResolutionKey key;
    private CodeLocation location;

    public static class Resolution {
        private Optional<VariableDeclaration> localVariable;
        private Optional<FileSymbols> fileImport;
        private Optional<FunctionDefinition> functionDefinition;
        private Optional<TypeRecord> globalType;
        private Optional<VariableDeclaration> memberVariable;

        public Resolution() {
            localVariable = Optional.empty();
            fileImport = Optional.empty();
            functionDefinition = Optional.empty();
            globalType = Optional.empty();
            memberVariable = Optional.empty();
        }

        public Optional<FunctionDefinition> getFunctionDefinition() {
            return functionDefinition;
        }

        public Optional<TypeRecord> getGlobalType() {
            return globalType;
        }

        public Expression getType() {
            if (localVariable.isPresent()) {
                return localVariable.get().getType();
            }
            if (globalType.isPresent()) {
                return globalType.get().getType();
            }
            return null;
        }

        public Optional<VariableDeclaration> getLocalVariable() {
            return localVariable;
        }
    }

    private Optional<Resolution> resolution;

    public SymbolReference(String name, CodeLocation location, GlobalSymbols globals, FileSymbols file,
            LocalSymbols locals,
            SymbolIndex localSymbolIndex) {
        this.key = new ResolutionKey(name, globals, file, locals, localSymbolIndex);
        this.location = location;
        this.resolution = Optional.empty();
        file.addSymbolReference(this);
    }

    // Used for member access expressions where the type of the subject determines
    // the meaning of the symbol.
    public SymbolReference(String name, CodeLocation location, GlobalSymbols globals, FileSymbols file,
            LocalSymbols locals, Expression subject,
            SymbolIndex localSymbolIndex) {
        this.key = new ResolutionKey(name, globals, file, locals, subject, localSymbolIndex);
        this.location = location;
        this.resolution = Optional.empty();
        file.addSymbolReference(this);
    }

    public boolean resolve() {
        if (key.subject.isPresent()) {
            System.out.println("Foo " + key.subject.get().getType());
            Expression type = key.subject.get().getType();
            if (!type.getType().equals(Type.TYPE)) {
                System.out.println("Bar");
                return false;
            }
            if (Type.hasMemberVariable(type, key.name)) {
                System.out.println("Baz");
                resolution = Optional.of(new Resolution());
                resolution.get().memberVariable = Optional.of(Type.getMemberVariable(type, key.name));
                return true;
            }
            System.out.println("False");
            return false;
        }

        Optional<LocalSymbol> symbol = key.locals.get(key.name, key.localSymbolIndex);
        if (symbol.isPresent()) {
            resolution = Optional.of(new Resolution());
            resolution.get().localVariable = Optional.of(symbol.get().declaration);
            return true;
        }

        Optional<FunctionDefinition> fn = key.file.getFunction(key.name);
        if (fn.isPresent()) {
            resolution = Optional.of(new Resolution());
            resolution.get().functionDefinition = fn;
            return true;
        }

        Optional<Target> importTarget = key.file.getImport(key.name);
        if (importTarget.isPresent()) {
            if (!key.globals.containsTarget(importTarget.get())) {
                return false;
            }
            resolution = Optional.of(new Resolution());
            resolution.get().fileImport = Optional.of(key.globals.get(importTarget.get()));
            return true;
        }

        Optional<TypeRecord> globalType = key.globals.getTypeTable().get(key.name);
        if (globalType.isPresent()) {
            resolution = Optional.of(new Resolution());
            resolution.get().globalType = globalType;
            return true;
        }

        return false;
    }

    public RegisterTracker getRegisterTracker() {
        if (resolution.isEmpty()) {
            throw new RuntimeException("Unresolved variables do not have registers assigned.");
        }
        if (resolution.get().localVariable.isPresent()) {
            return resolution.get().localVariable.get().getRegisterTracker();
        }
        throw new UnsupportedOperationException("Registers for imports not supported yet.");
    }

    public Optional<Resolution> getResolution() {
        return resolution;
    }

    public String toString() {
        String out = key.name + " ";
        if (resolution.isPresent()) {
            if (resolution.get().fileImport.isPresent()) {
                out += "(import)";
            } else if (resolution.get().localVariable.isPresent()) {
                out += "(local variable)";
            } else if (resolution.get().globalType.isPresent()) {
                out += "(global type)";
            } else if (resolution.get().memberVariable.isPresent()) {
                out += "(member variable)";
            } else if (resolution.get().functionDefinition.isPresent()) {
                out += "(function call)";
            }
            out += " " + location;
        } else {
            out += "(unresolved) " + location + ", " + key.localSymbolIndex;
        }
        return out;
    }
}
