package com.github.ahhoefel.lang.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.parser.LRParser;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.api.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymbolVisitorTest {
    private static final Logger logger = LoggerFactory.getLogger(SymbolVisitorTest.class);
    private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

    @ParameterizedTest
    @ArgumentsSource(FileArgumentProvider.class)
    public void testSymbols(Path source, List<Path> entries, Path symbolsFile) throws Exception {
        logger.info("Running test: " + source.toString());
        logger.info("Entries: " + entries.toString());

        SymbolVisitor v = new SymbolVisitor(source);
        GlobalSymbols globals = new GlobalSymbols(v, fileParser);
        for (Path entry : entries) {
            Target t = new Target(source, entry);
            Optional<FileSymbols> fileSymbols = globals.add(t);
            assertTrue(fileSymbols.isPresent());
        }
        globals.resolve();

        // Uncomment to update expected results.
        // Files.write(symbolsFile, globals.toString().getBytes(),
        // StandardOpenOption.WRITE);

        String expected = Files.readString(symbolsFile);
        assertEquals(expected, globals.toString());
    }

    private static class FileArgumentProvider implements ArgumentsProvider {
        private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/test/java/com/github/ahhoefel/lang/ast/visitor/symbol_visitor_tests";

        @Override
        public Stream<Arguments> provideArguments(ExtensionContext arg0) throws Exception {
            Path source = Paths.get(BASE_PATH);
            Stream<Path> dirs = Files.list(source).filter(f -> f.toFile().isDirectory());
            return dirs.map(p -> {
                String testName = p.getFileName().toString();
                List<Path> entries;
                try {
                    entries = Files.list(p).filter(f -> f.toFile().getName().endsWith(".ro"))
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    logger.atError().log(e.toString());
                    entries = List.of();
                }
                Path symbols = p.resolve("symbols.txt");
                return Arguments.of(Named.of(testName, source), Named.of("" + entries.size() + " entries", entries),
                        Named.of("symbols.txt", symbols));
            });
        }
    }
}
