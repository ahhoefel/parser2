package com.github.ahhoefel.parser;

import java.util.List;
import java.util.Map;

public interface LanguageComponent {

    List<Symbol> provides(SymbolTable nonTerminals);

    // Required symbols will be passed to acceptExternalSymbols by the
    // LanguageBuilder.
    List<String> requires();

    // Required symbols are passed in here.
    void acceptExternalSymbols(Map<String, Symbol> external);

    // Adds rules to the LanguageBuilder
    void provideRules(LanguageBuilder builder);

}