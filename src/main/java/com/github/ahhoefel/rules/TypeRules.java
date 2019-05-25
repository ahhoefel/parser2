package com.github.ahhoefel.rules;

import com.github.ahhoefel.Rule;
import com.github.ahhoefel.Symbol;
import com.github.ahhoefel.SymbolTable;

public class TypeRules {

  public Symbol type;

  public TypeRules(Rule.Builder rules, SymbolTable.NonTerminalTable nonTerminals, Lexicon lex) {
    type = nonTerminals.newSymbol("type");
    rules.add(type, lex.intKeyword);
    rules.add(type, lex.boolKeyword);
    rules.add(type, lex.stringKeyword);
  }
}
