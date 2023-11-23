package com.github.ahhoefel.lang.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.LocateableList;
import com.github.ahhoefel.parser.Token;

public class LexiconTest {

    @ParameterizedTest
    @ArgumentsSource(FileArgumentProvider.class)
    public void testLexicon(Path input, Path expected) throws Exception {
        Lexicon lex = new Lexicon();
        ErrorLog log = new ErrorLog();
        LocateableList<Token> tokens = lex.parse(Files.readString(input), log);
        if (!log.isEmpty()) {
            // System.out.print(log.toString());
            assertEquals("", log.toString());
        }

        StringBuilder out = new StringBuilder();
        for (Token token : tokens.getList()) {
            out.append(token.toString());
            out.append("\n");
        }
        String expectedString = Files.readString(expected);
        assertEquals(expectedString, out.toString());
    }

    private static class FileArgumentProvider implements ArgumentsProvider {
        private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/test/java/com/github/ahhoefel/lang/rules/lexicon_tests";

        @Override
        public Stream<Arguments> provideArguments(ExtensionContext arg0) throws Exception {
            Path source = Paths.get(BASE_PATH);
            Stream<Path> inputs = Files.list(source).filter(f -> f.toFile().getName().endsWith(".ro"));
            return inputs.map(input -> {
                String testName = input.getFileName().toString().replaceFirst("\\.ro", "");
                Path expected = input.getParent().resolve(testName + ".out");
                return Arguments.of(Named.of(testName, input),
                        Named.of(".out", expected));
            });
        }
    }
}
