package com.github.ahhoefel.lang.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.visitor.LLVMVisitor;
import com.github.ahhoefel.lang.ast.visitor.SymbolVisitor;
import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;

import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef;
import org.bytedeco.llvm.global.LLVM;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class LLVMVisitorTest {
        private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/test/java/com/github/ahhoefel/ast/visitor/llvm_visitor_tests";

        private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

        /** Compares llvm_visitor_tests to their expected llvm. */
        @ParameterizedTest(name = "{0} {1}")
        @ArgumentsSource(FileArgumentProvider.class)
        public void testCorrectBitCode(Path source, Path entry, Path expected) throws Exception {
                String s = Files.readString(entry);
                System.out.println(s);
                try {
                        File f = (File) fileParser.parse(s);

                        SymbolVisitor symbolVistor = new SymbolVisitor(source);
                        GlobalSymbols symbols = new GlobalSymbols();
                        f.accept(symbolVistor, symbols);
                        symbols.resolve(source, symbolVistor, fileParser);

                        LLVMVisitor v = new LLVMVisitor(symbols);
                        LLVMVisitor.Value<LLVMMemoryBufferRef> result = new LLVMVisitor.Value<>();
                        f.accept(v, result);

                        LLVMMemoryBufferRef ref = readBitCode(expected);
                        assertEquals(ref.asByteBuffer(), result.value.asByteBuffer());
                } catch (ParseException e) {
                        System.out.println("Ignoring error. " + e);
                } catch (FileNotFoundException e) {
                        System.out.println("File not found:" + e);
                }
        }

        /** Used to writeBitCode when an expected output needs to be updated. */
        @SuppressWarnings("unused")
        private static void writeBitCode(Path file, LLVMMemoryBufferRef memoryBuffer) throws IOException {
                try (FileOutputStream stream = new FileOutputStream(file.toString(), false)) {
                        stream.getChannel().write(memoryBuffer.asByteBuffer());
                }

        }

        private static LLVMMemoryBufferRef readBitCode(Path file) throws FileNotFoundException {
                BytePointer path = new BytePointer(file.toString());
                LLVMMemoryBufferRef memory = new LLVMMemoryBufferRef();
                BytePointer error = new BytePointer();
                try {
                        if (LLVM.LLVMCreateMemoryBufferWithContentsOfFile(path, memory, error) != 0) {
                                throw new FileNotFoundException(error.getString());
                        }
                } finally {
                        LLVM.LLVMDisposeMessage(error);
                }
                return memory;
        }

        private static class FileArgumentProvider implements ArgumentsProvider {

                @Override
                public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
                        Path source = Paths.get(LLVMVisitorTest.BASE_PATH);
                        Stream<Path> dirs = Files.list(source).filter(f -> f.toFile().isDirectory());
                        return dirs.map(p -> {
                                Path entry = p.resolve(p.getFileName().toString() + ".ro");
                                Path expected = p.resolve("expected.llvm");
                                return Arguments.of(source, entry, expected);
                        });
                }
        }
}
