package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.lang.ast.Import;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Token;

public class ImportRules implements LanguageComponent {
    @SuppressWarnings("unchecked")
    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
        Symbol imp0rt = provider.createAndExport("import");
        Symbol path = provider.create("path");

        Symbol importKeyword = provider.requireTerminal("import");
        Symbol identifier = provider.requireTerminal("identifier");

        rules.emit(imp0rt, importKeyword, path).setAction(e -> new Import(((Token<String>) e[1]).getValue()));
        rules.emit(imp0rt, importKeyword, identifier, path)
                .setAction(e -> new Import(((Token<String>) e[1]).getValue(), ((Token<String>) e[2]).getValue()));
        rules.emit(path, identifier).setAction(e -> e[0]);
        rules.emit(path, path, provider.requireTerminal("forwardSlash"), identifier).setAction(ConcatAction.SINGLETON);
    }
}
