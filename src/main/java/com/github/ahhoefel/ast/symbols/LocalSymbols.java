package com.github.ahhoefel.ast.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.ahhoefel.ast.LValue;

import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

public class LocalSymbols {

    private Optional<LocalSymbols> parent;
    private Map<String, LocalSymbol> variables;

    public LocalSymbols() {
        parent = Optional.empty();
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

    public void put(LValue declaration, LLVMTypeRef type, LLVMValueRef value) {
        LocalSymbol s = new LocalSymbol();
        s.declaration = declaration;
        s.type = type;
        s.value = value;
        variables.put(declaration.getIdentifier(), s);
    }

    public static class LocalSymbol {
        public LValue declaration;
        public LLVMTypeRef type;
        public LLVMValueRef value;
    }
}
