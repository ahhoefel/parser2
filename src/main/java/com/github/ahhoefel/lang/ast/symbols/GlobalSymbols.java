package com.github.ahhoefel.lang.ast.symbols;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.visitor.SymbolVisitor;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.util.IndentedString;

public class GlobalSymbols {

    private SymbolVisitor symbolVisitor;
    private LRParser fileParser;

    private Map<Target, FilePair> files;
    private TypeTable typeTable;

    private class FilePair {
        private File file;
        private FileSymbols symbols;

        public FilePair(File file, FileSymbols symbols) {
            this.file = file;
            this.symbols = symbols;
        }
    }

    public GlobalSymbols(SymbolVisitor v, LRParser fileParser) {
        files = new HashMap<>();
        this.symbolVisitor = v;
        this.fileParser = fileParser;
        this.typeTable = new TypeTable();
    }

    public boolean containsTarget(Target t) {
        return files.containsKey(t);
    }

    public FileSymbols get(Target t) {
        return files.get(t).symbols;
    }

    public Optional<FileSymbols> add(Target t) {
        try {
            String s = Files.readString(t.getFilePath());
            File file = (File) fileParser.parse(s);
            file.setTarget(t);
            FileSymbols symbols = new FileSymbols(t);
            this.files.put(t, new FilePair(file, symbols));
            file.accept(symbolVisitor, this, symbols);
            return Optional.of(symbols);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + t.getFilePath(), e);
        } catch (ParseException e) {
            throw new RuntimeException("Parsing error on " + t, e);
        }
        // return Optional.empty();
    }

    public boolean resolve() {
        boolean resolved = true;
        for (FilePair f : files.values()) {
            resolved = resolved && f.symbols.resolve();
        }
        return resolved;
    }

    public boolean transitivelyLoadImports() {
        List<Target> queue = new ArrayList<>();
        for (FilePair f : files.values()) {
            for (Target t : f.symbols.getImports()) {
                if (!files.containsKey(t)) {
                    queue.add(t);
                }
            }
        }
        for (int i = 0; i < queue.size(); i++) {
            Target t = queue.get(i);
            if (!files.containsKey(t)) {
                Optional<FileSymbols> f = this.add(t);
                if (!f.isPresent()) {
                    return false;
                }
                for (Target q : f.get().getImports()) {
                    if (!files.containsKey(q)) {
                        queue.add(q);
                    }
                }
            }
        }
        return true;
    }

    public String toString() {
        IndentedString s = new IndentedString();
        s.addLine("Global Symbols:").indent();
        List<Target> targets = new ArrayList<>(files.keySet());
        targets.sort((o1, o2) -> {
            Target t1 = (Target) o1;
            Target t2 = (Target) o2;
            return t1.getFilePath().compareTo(t2.getFilePath());
        });
        for (Target t : targets) {
            FileSymbols symbols = files.get(t).symbols;
            s.endLine();
            symbols.toIndentedString(s);
        }
        s.unindent();
        return s.toString();
    }

    public List<FileSymbols> getFiles() {
        return files.values().stream().map(p -> p.symbols).collect(Collectors.toList());
    }

    public TypeTable getTypeTable() {
        return typeTable;
    }
}
