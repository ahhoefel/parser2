package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.visitor.FormatVisitor;
import com.github.ahhoefel.parser.LayeredParser;
import com.github.ahhoefel.parser.io.RelativeTarget;
import com.github.ahhoefel.parser.io.Target;

import java.nio.file.Files;
import java.util.stream.Stream;
import java.util.Optional;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// Tests that files parse correctly and produce the desired AST.

public class LanguageRulesTest {
    private static final String BASE_PATH = "src/test/java/com/github/ahhoefel/lang/rules";
    private static final String TEST_PATH = "language_rules_tests";
    private static final LayeredParser<File> PARSER = LanguageRules.getParser();

    public static Stream<Arguments> testTargets() throws IOException {
        Path root = Path.of(".").toAbsolutePath();
        return Files.walk(root.resolve(BASE_PATH).resolve(TEST_PATH))
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().endsWith(".ro"))
                .map(LanguageRulesTest::filenameToTarget)
                .map(t -> Arguments.of(t, t.getPath().getFileName().toString()));
    }

    public static Target filenameToTarget(Path path) {
        Path rootPath = Path.of(".").resolve(BASE_PATH).resolve(TEST_PATH).toAbsolutePath();
        String filename = path.toString();
        String relativeFilename = filename.substring(rootPath.toString().length()).substring(1);
        int i = relativeFilename.lastIndexOf("/");
        String relativeBase = relativeFilename.substring(0, i);
        String name = relativeFilename.substring(i + 1);
        return new RelativeTarget(rootPath, relativeBase + "/" + name);
    }

    @ParameterizedTest(name = "{index} {1}")
    @MethodSource("testTargets")
    public void testTarget(Target target, String name) throws Exception {

        Optional<String> expectedError = Optional.empty();
        Path errorPath = target.getPath()
                .resolveSibling(target.getPath().getFileName().toString().replace(".ro", ".err"));
        if (Files.exists(errorPath)) {
            expectedError = Optional.of(Files.readString(errorPath));
        }

        File file;
        try {
            file = PARSER.parse(target, Files.readString(target.getPath()));
        } catch (Exception e) {
            if (!expectedError.isPresent()) {
                throw e;
            }
            assertEquals(expectedError.get(), e.toString());
            return;
        }

        if (expectedError.isPresent()) {
            Assertions.fail("An error was expected, but not produced: " + expectedError.get());
        }

        FormatVisitor visitor = new FormatVisitor();
        visitor.visit(file);
        String result = visitor.toString();
        String expected = Files.readString(target.getPath());
        if (!expected.equals(result)) {
            int diffIndex = 0;
            boolean sizeDifference = expected.length() != result.length();
            if (!sizeDifference) {
                for (int i = 0; i < expected.length(); i++) {
                    if (expected.charAt(i) != result.charAt(i)) {
                        diffIndex = i;
                        break;
                    }
                }
            }
            String reason;
            if (sizeDifference) {
                reason = "different sizes";
            } else {
                reason = "difference at character " + diffIndex;
            }

            throw new RuntimeException(
                    "Parsed and formatted target does not match original (" + reason + "): " + target
                            + ".\nExpected: " + expected.length() + " length\n"
                            + expected + "\nResults: " + result.length() + " length\n" + result);
        }
    }

    // // @Test
    // public void testAllFiles() throws IOException {
    // Files.walk(Paths.get(BASE_PATH))
    // .filter(Files::isRegularFile)
    // .filter(f -> f.toString().endsWith(".ro"))
    // .map(LanguageRulesTest::filenameToTarget)
    // .forEach(LanguageRulesTest::testTarget);
    // }
}
