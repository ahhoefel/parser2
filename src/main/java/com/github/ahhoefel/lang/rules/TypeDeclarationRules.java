package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.lang.ast.TypeDeclaration;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.ShiftReduceResolver;

public class TypeDeclarationRules implements LanguageComponent {
    @SuppressWarnings("unchecked")
    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
        Symbol typeDeclaration = provider.createAndExport("typeDeclaration");
        rules
                .emit(typeDeclaration, provider.requireTerminal("type"), provider.requireTerminal("identifier"),
                        provider.require("type"))
                .setAction(e -> new TypeDeclaration(((Token<String>) e[1]).getValue(), (Type) e[2]));
    }
}
