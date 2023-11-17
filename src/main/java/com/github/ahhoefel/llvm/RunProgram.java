package com.github.ahhoefel.llvm;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.visitor.LLVMVisitor;
import com.github.ahhoefel.lang.ast.visitor.SymbolVisitor;
import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef;
import org.bytedeco.llvm.LLVM.LLVMGenericValueRef;
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;

public class RunProgram {
    private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

    public static void main(String[] args) throws Exception {
        Path source = Path.of("/Users/hoefel/dev/parser2/src/tests/statements");
        Path entry = Path.of("/Users/hoefel/dev/parser2/src/tests/statements/return_statement.ro");
        String s = Files.readString(entry);
        System.out.println(s);
        try {
            File f = (File) fileParser.parse(s);

            SymbolVisitor symbolVistor = new SymbolVisitor(source);
            GlobalSymbols symbols = new GlobalSymbols(symbolVistor, fileParser);
            f.accept(symbolVistor, symbols);
            symbols.resolve();

            LLVMVisitor v = new LLVMVisitor(symbols);
            LLVMVisitor.Value<LLVMMemoryBufferRef> result = new LLVMVisitor.Value<>();
            f.accept(v, result);

            LLVMModuleRef module = decodeModule(result.value, "file");
            applyDefaultPass(module);
            LLVM.LLVMDumpModule(module);

            LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
            BytePointer error = new BytePointer((Pointer) null);
            if (LLVM.LLVMCreateJITCompilerForModule(engine, module, 2, error) != 0) {
                System.err.println(error.getString());
                LLVM.LLVMDisposeMessage(error);
                System.exit(-1);
            }
            LLVM.LLVMDisposeMessage(error);

            LLVMValueRef func = LLVM.LLVMGetNamedFunction(module, "main");
            LLVMGenericValueRef exec_res = LLVM.LLVMRunFunction(engine, func, 0, new LLVMGenericValueRef());
            LLVM.LLVMDisposeExecutionEngine(engine);

            System.err.println("Running main...");
            System.err.println("Result " + LLVM.LLVMGenericValueToInt(exec_res, 0));
        } catch (ParseException e) {
            System.out.println("Ignoring error. " + e);
        }
    }

    private static void applyDefaultPass(LLVMModuleRef module) {
        LLVMPassManagerRef pass = LLVM.LLVMCreatePassManager();
        // LLVM.LLVMAddConstantPropagationPass(pass);
        // LLVM.LLVMAddInstructionCombiningPass(pass);
        // LLVM.LLVMAddPromoteMemoryToRegisterPass(pass);
        //// LLVMAddDemoteMemoryToRegisterPass(pass); // Demotes every possible value to
        //// memory
        // LLVM.LLVMAddGVNPass(pass);
        // LLVM.LLVMAddCFGSimplificationPass(pass);
        LLVM.LLVMRunPassManager(pass, module);
        LLVM.LLVMDisposePassManager(pass);
    }

    private static LLVMModuleRef decodeModule(LLVMMemoryBufferRef encodedModule, String name) {
        LLVMModuleRef module = LLVM.LLVMModuleCreateWithName(name);
        BytePointer message = new BytePointer((Pointer) null);
        if (LLVM.LLVMGetBitcodeModule(encodedModule, module, message) != 0) {
            System.err.println(message.getString());
            LLVM.LLVMDisposeMessage(message);
            System.exit(-1);
        }
        LLVM.LLVMDisposeMessage(message);
        return module;
    }
}
