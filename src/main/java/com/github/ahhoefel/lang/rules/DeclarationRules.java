package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.Declaration;
import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;

public class DeclarationRules implements LanguageComponent {

    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
        Symbol declarationList = provider.createAndExport("declarationList");
        Symbol declaration = provider.create("declaration");

        Symbol functionDeclaration = provider.require("functionDeclaration");
        Symbol imp0rt = provider.require("import");
        Symbol typeDeclaration = provider.require("typeDeclaration");

        rules.emit(declarationList, declarationList, declaration).setAction(e -> {
            File f = (File) e[0];
            ((Declaration) e[1]).addToFile(f);
            return f;
        });
        rules.emit(declarationList).setAction(e -> new File());
        rules.emit(declaration, functionDeclaration).setAction(e -> e[0]);
        rules.emit(declaration, imp0rt).setAction(e -> e[0]);
        rules.emit(declaration, typeDeclaration).setAction(e -> e[0]);
    }
}
