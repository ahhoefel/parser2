package com.github.ahhoefel;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileArgumentProvider implements ArgumentsProvider {
        private String path;

        public FileArgumentProvider(String path) {
                this.path = path;
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
                Stream<Arguments> files = Files.walk(Paths.get(path)).filter(Files::isRegularFile)
                                .filter(f -> f.toString().endsWith(".ro")).map(f -> Arguments.of(f));
                return files;
        }
}
