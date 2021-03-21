package com.github.ahhoefel;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileArgumentProvider implements ArgumentsProvider {
        private static final String BASE_PATH = "/Users/hoefel/dev/parser2/src/tests/";

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
                Stream<Arguments> files = Files.walk(Paths.get(BASE_PATH)).filter(Files::isRegularFile)
                                .filter(f -> f.toString().endsWith(".ro")).map(f -> Arguments.of(f));
                return files;
        }
}