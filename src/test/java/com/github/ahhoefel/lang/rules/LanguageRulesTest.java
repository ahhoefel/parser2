package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.visitor.FormatVisitor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.Collection;

import java.nio.file.Path;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.ahhoefel.parser.ErrorLog;

// Tests that files parse correctly and produce the desired AST.
@RunWith(Parameterized.class)
public class LanguageRulesTest {
    private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/tests/";
    private static final LanguageRules RULES = new LanguageRules();

    @Parameters
    public static Collection<Object[]> testTargets() throws IOException {
        return Files.walk(Paths.get(BASE_PATH))
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().endsWith(".ro"))
                .map(LanguageRulesTest::filenameToTarget)
                .map(t -> new Object[] { t })
                .collect(Collectors.toList());
    }

    public static Target filenameToTarget(Path path) {
        String filename = path.toString();
        String relativeFilenameNoSuffix = filename.substring(BASE_PATH.length(), filename.length() - 3);
        int i = relativeFilenameNoSuffix.lastIndexOf("/");
        String relativeBase = relativeFilenameNoSuffix.substring(0, i);
        String name = relativeFilenameNoSuffix.substring(i + 1);
        return new Target(Path.of(BASE_PATH), relativeBase, name);
    }

    private Target target;

    public LanguageRulesTest(Target target) {
        this.target = target;
    }

    @Test
    public void testTarget() {
        ErrorLog log = new ErrorLog();
        try {
            File file = RULES.parse(target, log);
            ErrorLog expectedError = ErrorLog.readErrors(target);
            if (file == null && !expectedError.isEmpty()) {
                System.out.println("Skipping target with expected error: " + target);
                return;
            }
            FormatVisitor visitor = new FormatVisitor();
            visitor.visit(file);
            String result = visitor.toString();
            String expected = Files.readString(target.getFilePath());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
