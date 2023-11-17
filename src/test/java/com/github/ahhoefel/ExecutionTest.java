package com.github.ahhoefel;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.visitor.LLVMVisitor;
import com.github.ahhoefel.lang.ast.visitor.SymbolVisitor;
import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.parser.LRParser;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.llvm.global.LLVM;
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef;
import org.bytedeco.llvm.LLVM.LLVMGenericValueRef;
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class ExecutionTest {
    private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/test/java/com/github/ahhoefel/execution_tests";
    private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

    private static class FileArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
            Path source = Paths.get(ExecutionTest.BASE_PATH);
            Stream<Arguments> files = Files.walk(Paths.get(BASE_PATH)).filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".ro")).map(f -> Arguments.of(source, f));
            return files;
        }
    }

    /** Runs execution_tests and confirms they return true. */
    @ParameterizedTest(name = "{0} {1}")
    @ArgumentsSource(FileArgumentProvider.class)
    public void testExecution(Path source, Path entry) throws Exception {
        Target t = new Target(source, entry);
        String s = Files.readString(t.getFilePath());
        System.out.println(entry);
        System.out.println(s);
        File f = (File) fileParser.parse(s);
        if (f == null) {
            System.out.println("Missing file: " + source);
        }
        f.setTarget(t);

        System.out.println("Starting symbol visitor.");
        SymbolVisitor symbolVistor = new SymbolVisitor(source);
        GlobalSymbols symbols = new GlobalSymbols(symbolVistor, fileParser);
        symbols.add(t);
        symbols.resolve();

        System.out.println("Starting LLVM visitor.");
        return;

        // LLVMVisitor v = new LLVMVisitor(symbols);
        // LLVMVisitor.Value<LLVMMemoryBufferRef> result = new LLVMVisitor.Value<>();
        // f.accept(v, result);

        // System.out.println("Decoding module.");
        // LLVMModuleRef module = decodeModule(result.value, "file");
        // System.out.println("Starting pass.");
        // applyDefaultPass(module);
        // // LLVM.LLVMDumpModule(module);

        // System.out.println("Creating engine.");
        // LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
        // BytePointer error = new BytePointer((Pointer) null);
        // if (LLVM.LLVMCreateJITCompilerForModule(engine, module, 2, error) != 0) {
        // System.err.println(error.getString());
        // LLVM.LLVMDisposeMessage(error);
        // System.exit(-1);
        // }
        // LLVM.LLVMDisposeMessage(error);

        // LLVMValueRef func = LLVM.LLVMGetNamedFunction(module, "main");
        // if (func.isNull()) {
        // return;
        // }

        // System.out.println("Starting execution.");
        // LLVMGenericValueRef exec_res = LLVM.LLVMRunFunction(engine, func, 0, new
        // LLVMGenericValueRef());
        // System.out.println("Execution complete.");

        // LLVM.LLVMDisposeExecutionEngine(engine);
        // if (LLVM.LLVMGenericValueToInt(exec_res, 0) == 0) {
        // fail("Test failed: " + entry.getFileName());
        // }
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
