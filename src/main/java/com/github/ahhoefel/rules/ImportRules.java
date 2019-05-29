package com.github.ahhoefel.rules;

import com.github.ahhoefel.Rule;
import com.github.ahhoefel.Symbol;
import com.github.ahhoefel.SymbolTable;
import com.github.ahhoefel.Token;
import com.github.ahhoefel.ast.Import;

public class ImportRules {

  public Symbol imp0rt;
  private Symbol path;

  public ImportRules(Rule.Builder rules, Lexicon lex, SymbolTable.NonTerminalTable nonTerminals) {
    imp0rt = nonTerminals.newSymbol("import");
    path = nonTerminals.newSymbol("path");
    rules.add(imp0rt, lex.importKeyword, path).setAction(e -> new Import((String) e[1]));
    rules.add(imp0rt, lex.importKeyword, lex.identifier, path).setAction(e -> new Import(((Token) e[1]).getValue(), (String) e[2]));
    rules.add(path, lex.identifier).setAction(e -> ((Token) e[0]).getValue());
    rules.add(path, path, lex.forwardSlash, lex.identifier).setAction(e -> e[0] + "/" + ((Token) e[0]).getValue());
  }
}
