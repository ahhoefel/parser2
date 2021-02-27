package com.github.ahhoefel.parser;

import com.github.ahhoefel.rules.Lexicon;

import java.util.HashMap;
import java.util.Map;

public class LanguageBuilder {
    private Lexicon lex;
    private Rule.Builder rules;
    private ShiftReduceResolver resolver;
    private SymbolTable.NonTerminalTable nonTerminals;

    public LanguageBuilder() {
        lex = new Lexicon();
        resolver = new ShiftReduceResolver();
        rules = new Rule.Builder();
        nonTerminals = new SymbolTable.NonTerminalTable();
    }

    public Lexicon getLexicon() {
        return lex;
    }

    public Rule.Builder getRules() {
        return rules;
    }

    public ShiftReduceResolver getResolver() {
        return resolver;
    }

    public SymbolTable.NonTerminalTable getNonTerminals() {
        return nonTerminals;
    }

    public static LanguageBuilder build(String start, LanguageComponent... components) {
        LanguageBuilder lang = new LanguageBuilder();
        Map<String, Symbol> symbols = new HashMap<>();
        for (LanguageComponent component : components) {
            for (Symbol symbol : component.provides(lang.nonTerminals)) {
                symbols.put(symbol.toString(), symbol);
            }
        }
        for (LanguageComponent component : components) {
            for (String symbolName : component.requires()) {
                if (!symbols.containsKey(symbolName)) {
                    throw new RuntimeException(String.format("Symbol %s required, but not provided.", symbolName));
                }
            }
            component.acceptExternalSymbols(symbols);
        }
        for (LanguageComponent component : components) {
            component.provideRules(lang);
        }
        Symbol startSymbol = symbols.get(start);
        if (startSymbol == null) {
            throw new RuntimeException(String.format("Start symbol %s not provided.", start));
        }
        lang.rules.add(lang.nonTerminals.getStart(), symbols.get(start)).setAction(e -> e[1]);
        return lang;
    }
}
