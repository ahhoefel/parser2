package com.github.ahhoefel.lang.ast.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bytedeco.llvm.LLVM.LLVMValueRef;
import com.github.ahhoefel.lang.ast.VariableDeclaration;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;

public class LocalSymbols {

    private Optional<LocalSymbols> parent;
    private Map<String, LocalSymbol> variables;

    public LocalSymbols() {
        this.parent = Optional.empty();
        variables = new HashMap<>();
    }

    public LocalSymbols(LocalSymbols parent) {
        this.parent = Optional.of(parent);
        variables = new HashMap<>();
    }

    public Optional<LocalSymbol> get(String s) {
        Optional<LocalSymbols> scope = Optional.of(this);
        while (scope.isPresent()) {
            if (scope.get().variables.containsKey(s)) {
                return Optional.of(scope.get().variables.get(s));
            }
            scope = scope.get().parent;
        }
        return Optional.empty();
    }

    public void put(VariableDeclaration declaration, LLVMTypeRef type, LLVMValueRef value) {
        LocalSymbol s = new LocalSymbol();
        s.declaration = declaration;
        s.type = type;
        s.value = value;
        variables.put(declaration.getName(), s);
    }

    public static class LocalSymbol {
        public VariableDeclaration declaration;
        public LLVMTypeRef type;
        public LLVMValueRef value;
    }
}
