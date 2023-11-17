package com.github.ahhoefel.lang.ast.symbols;

import java.util.Optional;

import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols.LocalSymbol;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols.SymbolIndex;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;

public class SymbolReference {
    private static class ResolutionKey {
        private String name;
        private GlobalSymbols globals;
        private FileSymbols file;
        private LocalSymbols locals;
        private SymbolIndex localSymbolIndex;

        public ResolutionKey(String name, GlobalSymbols globals, FileSymbols file, LocalSymbols locals, SymbolIndex locaSymbolIndex) {
            this.name = name;
            this.globals = globals;
            this.file = file;
            this.locals = locals;
            this.localSymbolIndex = locaSymbolIndex;
        }
    }
    private ResolutionKey key;

    private static class Resolution {
        private Optional<VariableDeclaration> localVariable;
        private Optional<FileSymbols> fileImport;
        public Resolution() {
            localVariable = Optional.empty();
            fileImport = Optional.empty();
        }
    }
    private Optional<Resolution> resolution;

    public SymbolReference(String name, GlobalSymbols globals, FileSymbols file, LocalSymbols locals, SymbolIndex localSymbolIndex) {
        this.key = new ResolutionKey(name, globals, file, locals, localSymbolIndex);
        this.resolution = Optional.empty();
        file.addSymbolReference(this);
    }

    public boolean resolve() {
        Optional<LocalSymbol> symbol = key.locals.get(key.name, key.localSymbolIndex);
        if (symbol.isPresent()) {
            resolution = Optional.of(new Resolution());
            resolution.get().localVariable = Optional.of(symbol.get().declaration);
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

    public String toString() {
        String out = key.name + " ";
        if (resolution.isPresent()) {
            if (resolution.get().fileImport.isPresent()) {
                out += "(import)";
            } else if (resolution.get().localVariable.isPresent()) {
                out += "(local variable)";
            }
        } else {
            out += "(unresolved)";
        }
        return out;
    }
}
