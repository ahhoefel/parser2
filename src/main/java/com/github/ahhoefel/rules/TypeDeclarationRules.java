package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ast.TypeDeclaration;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;

class TypeDeclarationRules {
  Symbol typeDeclaration;

  public TypeDeclarationRules(SymbolTable.NonTerminalTable nonTerminals) {
    typeDeclaration = nonTerminals.newSymbol("typeDeclaration");
  }

  public void provideRules(Rule.Builder rules, Language lang) {
    rules.add(typeDeclaration, lang.lex.typeKeyword, lang.lex.identifier, lang.type.type)
        .setAction(e -> new TypeDeclaration(((Token) e[1]).getValue(), (Type) e[2]));
  }
}
