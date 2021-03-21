package com.github.ahhoefel.rules;

import java.util.List;
import java.util.Map;

import com.github.ahhoefel.ast.TypeDeclaration;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;

public class TypeDeclarationRules implements LanguageComponent {
  // Provides
  private Symbol typeDeclaration;

  // Requires
  private Symbol type;

  @Override
  public void provideRules(LanguageBuilder lang) {
    Lexicon lex = lang.getLexicon();
    lang.getRules().add(typeDeclaration, lex.typeKeyword, lex.identifier, type)
        .setAction(e -> new TypeDeclaration(((Token) e[1]).getValue(), (Type) e[2]));
  }

  @Override
  public List<Symbol> provides(SymbolTable nonTerminals) {
    typeDeclaration = nonTerminals.newSymbol("typeDeclaration");
    return List.of(typeDeclaration);
  }

  @Override
  public List<String> requires() {
    return List.of("type");
  }

  @Override
  public void acceptExternalSymbols(Map<String, Symbol> external) {
    type = external.get("type");
  }
}
