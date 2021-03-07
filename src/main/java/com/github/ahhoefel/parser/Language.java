package com.github.ahhoefel.parser;

import java.util.List;

import com.github.ahhoefel.rules.Lexicon;
import com.github.ahhoefel.ast.ErrorLog;

public class Language<T> {

    private Grammar grammar;
    private LRTable table;
    private Lexicon lex;

    public Language(LanguageBuilder builder) {
        grammar = new Grammar(builder.getLexicon().getTerminals(), builder.getNonTerminals(),
                builder.getRules().build());
        table = LRParser.getCanonicalLRTable(grammar, builder.getResolver(),
                builder.getLexicon().getTerminals().getEof());
        lex = builder.getLexicon();
    }

    public void parse(String s, ErrorLog log, T result) {
        List<Token> tokens = lex.parse(s, log);
        tokens.add(new Token(lex.getTerminals().getEof(), "eof", null));
        Parser.parseTokens(table, tokens.iterator(), grammar.getAugmentedStartRule().getSource(), result, log);
    }
}
