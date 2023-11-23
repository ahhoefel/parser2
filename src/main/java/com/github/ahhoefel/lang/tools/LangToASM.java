package com.github.ahhoefel.lang.tools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.ahhoefel.arm.AssemblyFile;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.visitor.AArch64Visitor;
import com.github.ahhoefel.lang.ast.visitor.RegisterVisitor;
import com.github.ahhoefel.lang.ast.visitor.SymbolVisitor;
import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.parser.LRParser;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParser;

public class LangToASM {
    private static final LRParser fileParser = new LRParser(LanguageRules.getLanguage());

    public static class Options extends OptionsBase {

        @Option(name = "help", abbrev = 'h', help = "Prints usage info.", defaultValue = "false")
        public boolean help;

        @Option(name = "root", abbrev = 'r', help = "Root of the lang source tree.", defaultValue = "")
        public String root;

        @Option(name = "targets", abbrev = 't', help = "Targets to be compiled, comma separated.", defaultValue = "")
        public String targets;

        @Option(name = "input", abbrev = 'i', help = "Single input file to be compiled.", defaultValue = "")
        public String input;

        @Option(name = "output", abbrev = 'o', help = "Output filename.", defaultValue = "")
        public String output;
    }

    public static void main(String[] args) throws Exception {
        OptionsParser parser = OptionsParser.newOptionsParser(Options.class);
        parser.parseAndExitUponError(args);
        Options options = parser.getOptions(Options.class);

        if (options.help) {
            System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
                    OptionsParser.HelpVerbosity.LONG));
            System.exit(0);
        }

        Path source = Path.of(options.root);
        List<String> entries = options.targets.isEmpty() ? List.of() : Arrays.asList(options.targets.split(","));
        Path asmFile = Path.of(options.output);
        SymbolVisitor v = new SymbolVisitor(source);
        GlobalSymbols globals = new GlobalSymbols(v, fileParser);
        for (String entry : entries) {
            System.out.println(source);
            System.out.println(entry);
            Target t = new Target(source, entry);
            System.out.println(t.getFilePath());
            globals.add(t);
        }
        if (!options.input.isEmpty()) {
            Target t = new Target(source, Path.of(options.input));
            System.out.println(t.getFilePath());
            globals.add(t);
        }

        boolean resolved = globals.resolve();
        if (!resolved) {
            System.out.println("Symbols not resolved:");
            System.out.println(globals.toString());
            System.exit(1);
        }

        System.out.println(globals.toString());

        RegisterVisitor registerVisitor = new RegisterVisitor();
        registerVisitor.assignRegisters(globals);

        AArch64Visitor asmVisitor = new AArch64Visitor();
        AssemblyFile asm = asmVisitor.createAssemblyFile(globals);

        // Uncomment to update expected results.
        Files.write(asmFile, asm.toString().getBytes(),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }
}
