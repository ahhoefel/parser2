package com.github.ahhoefel.ast.symbols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.ast.visitor.SymbolVisitor;
import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.util.IndentedString;

public class GlobalSymbols {

    private Map<Target, FileSymbols> symbols;
    private Map<Target, Set<FileSymbols>> unresolvedImports;

    public GlobalSymbols() {
        symbols = new HashMap<>();
        unresolvedImports = new HashMap<>();
    }

    public boolean containsTarget(Target t) {
        return symbols.containsKey(t);
    }

    public FileSymbols get(Target t) {
        return symbols.get(t);
    }

    public FileSymbols add(Target t) {
        FileSymbols symbols = new FileSymbols(t);
        this.symbols.put(t, symbols);
        if (unresolvedImports.containsKey(t)) {
            Set<FileSymbols> set = unresolvedImports.remove(t);
            for (FileSymbols f : set) {
                f.addImport(t, symbols);
            }
        }
        return symbols;
    }

    public void addUnresolvedImport(Target t, FileSymbols symbols) {
        if (unresolvedImports.containsKey(t)) {
            unresolvedImports.get(t).add(symbols);
        } else {
            Set<FileSymbols> set = new HashSet<>();
            set.add(symbols);
            unresolvedImports.put(t, set);
        }
    }

    public String toString() {
        IndentedString s = new IndentedString();
        s.addLine("Global Symbols:").indent();
        List<Target> targets = new ArrayList<>(symbols.keySet());
        targets.sort((o1, o2) -> {
            Target t1 = (Target) o1;
            Target t2 = (Target) o2;
            return t1.getFilePath().compareTo(t2.getFilePath());
        });
        for (Target t : targets) {
            FileSymbols file = symbols.get(t);
            s.endLine();
            file.toIndentedString(s);
        }
        s.unindent();

        targets = new ArrayList<>(unresolvedImports.keySet());
        targets.sort((o1, o2) -> {
            Target t1 = (Target) o1;
            Target t2 = (Target) o2;
            return t1.getFilePath().compareTo(t2.getFilePath());
        });

        s.endLine();
        s.addLine("Unresolved Imports:").indent();
        for (Target t : targets) {
            s.addLine(t.toString()).indent();
            List<FileSymbols> files = new ArrayList<>(unresolvedImports.get(t));
            Comparator<FileSymbols> comp = Comparator.comparing(x -> x.getTarget().toString());
            files.sort(comp);
            s.addLine("Required by:");
            for (FileSymbols file : files) {
                file.toIndentedString(s);
            }
        }
        s.unindent();

        return s.toString();
    }

    public void resolve(Path source, SymbolVisitor v, LRParser fileParser) throws IOException {
        while (!unresolvedImports.isEmpty()) {
            for (Target t : unresolvedImports.keySet()) {
                String s = Files.readString(t.getFilePath());
                try {
                    File f = (File) fileParser.parse(s);
                    f.setTarget(t);
                    f.accept(v, this);
                } catch (ParseException e) {
                    System.out.println("Ignoring error: " + e);
                }
            }
        }
    }
}
