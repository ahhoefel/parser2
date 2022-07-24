package com.github.ahhoefel.lang.ast.symbols;

import java.util.Optional;

import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;

public class Context {

    public GlobalSymbols globals;
    public FileSymbols file;
    public LocalSymbols locals;
    public LLVMModuleRef fileModule;

    public Context(GlobalSymbols globals, FileSymbols file) {
        this.globals = globals;
        this.file = file;
        this.fileModule = LLVM.LLVMModuleCreateWithName(file.getTarget().toString());
        this.locals = new LocalSymbols();
    }

    public Context(Context context) {
        this.globals = context.globals;
        this.file = context.file;
        this.fileModule = context.fileModule;
        this.locals = new LocalSymbols(context.locals);
    }

    public GlobalSymbols getGlobalSymbols() {
        return globals;
    }

    public LLVMModuleRef getFileModule() {
        return fileModule;
    }

    public FileSymbols getFileSymbols() {
        return file;
    }

    public LocalSymbols getLocals() {
        return locals;
    }

    public Optional<LLVMValueRef> getFunction(String name) {
        return Optional.of(LLVM.LLVMGetNamedFunction(fileModule, name));
    }
}
