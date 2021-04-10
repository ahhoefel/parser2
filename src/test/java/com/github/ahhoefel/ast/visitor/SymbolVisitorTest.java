package com.github.ahhoefel.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.rules.LanguageRules;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class SymbolVisitorTest {
        private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

        @ParameterizedTest(name = "{0} {1}")
        @ArgumentsSource(FileArgumentProvider.class)
        public void testSymbols(Path source, Path entry, Path symbolsFile) throws IOException {
                String s = Files.readString(entry);
                String expected = Files.readString(symbolsFile);
                try {
                        File f = (File) fileParser.parse(s);
                        f.setTarget(new Target(source, entry));
                        SymbolVisitor v = new SymbolVisitor(source);
                        GlobalSymbols symbols = new GlobalSymbols();
                        f.accept(v, symbols);
                        symbols.resolve(source, v, fileParser);
                        assertEquals(expected, symbols.toString());
                } catch (ParseException e) {
                        System.out.println("Ignoring error: " + e);
                }
        }

        private static class FileArgumentProvider implements ArgumentsProvider {
                private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/test/java/com/github/ahhoefel/ast/visitor/symbol_visitor_tests";

                @Override
                public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
                        Path source = Paths.get(BASE_PATH);
                        Stream<Path> dirs = Files.list(source).filter(f -> f.toFile().isDirectory());
                        return dirs.map(p -> {
                                Path entry = p.resolve(p.getFileName().toString() + ".ro");
                                Path symbols = p.resolve("symbols.txt");
                                return Arguments.of(source, entry, symbols);
                        });
                }
        }
}
