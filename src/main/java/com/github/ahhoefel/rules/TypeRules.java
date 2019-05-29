package com.github.ahhoefel.rules;

import com.github.ahhoefel.Rule;
import com.github.ahhoefel.Symbol;
import com.github.ahhoefel.SymbolTable;
import com.github.ahhoefel.ast.Type;

public class TypeRules {

  public Symbol type;

  public TypeRules(Rule.Builder rules, SymbolTable.NonTerminalTable nonTerminals, Lexicon lex) {
    type = nonTerminals.newSymbol("type");
    rules.add(type, lex.intKeyword).setAction(e -> Type.INT);
    rules.add(type, lex.boolKeyword).setAction(e -> Type.BOOL);
    rules.add(type, lex.stringKeyword).setAction(e -> Type.STRING);
  }
}
