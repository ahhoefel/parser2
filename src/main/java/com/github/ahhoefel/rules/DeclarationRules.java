package com.github.ahhoefel.rules;

import java.util.List;
import java.util.Map;

import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.ast.Declaration;
import com.github.ahhoefel.ast.File;

public class DeclarationRules implements LanguageComponent {

    // Provides
    private Symbol declarationList;

    // Requires
    private Symbol functionDeclaration;
    private Symbol imp0rt;
    private Symbol typeDeclaration;

    // Internal
    private Symbol declaration;

    @Override
    public List<Symbol> provides(SymbolTable nonTerminals) {
        declarationList = nonTerminals.newSymbol("declarationList");
        declaration = nonTerminals.newSymbol("declaration");
        return List.of(declarationList);
    }

    @Override
    public List<String> requires() {
        return List.of("functionDeclaration", "import", "typeDeclaration");
    }

    @Override
    public void acceptExternalSymbols(Map<String, Symbol> external) {
        this.functionDeclaration = external.get("functionDeclaration");
        this.imp0rt = external.get("import");
        this.typeDeclaration = external.get("typeDeclaration");
    }

    @Override
    public void provideRules(LanguageBuilder builder) {
        Rule.Builder rules = builder.getRules();
        rules.add(declarationList, declarationList, declaration).setAction(e -> {
            File f = (File) e[0];
            ((Declaration) e[1]).addToFile(f);
            return f;
        });
        rules.add(declarationList).setAction(e -> new File());
        rules.add(declaration, functionDeclaration).setAction(e -> e[0]);
        rules.add(declaration, imp0rt).setAction(e -> e[0]);
        rules.add(declaration, typeDeclaration).setAction(e -> e[0]);
    }

}
