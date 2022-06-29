package com.github.ahhoefel.lang.rules;

import java.util.List;
import java.util.Map;

import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.lang.ast.Import;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;
import com.github.ahhoefel.parser.Token;

public class ImportRules implements LanguageComponent {

  // Provides
  private Symbol imp0rt;

  // Internal
  private Symbol path;

  @Override
  public void provideRules(LanguageBuilder lang) {
    Lexicon lex = lang.getLexicon();
    Rule.Builder rules = lang.getRules();
    rules.add(imp0rt, lex.importKeyword, path).setAction(e -> new Import((String) e[1]));
    rules.add(imp0rt, lex.importKeyword, lex.identifier, path)
        .setAction(e -> new Import(((Token) e[1]).getValue(), (String) e[2]));
    rules.add(path, lex.identifier).setAction(e -> ((Token) e[0]).getValue());
    rules.add(path, path, lex.forwardSlash, lex.identifier).setAction(e -> e[0] + "/" + ((Token) e[2]).getValue());
  }

  @Override
  public List<Symbol> provides(SymbolTable nonTerminals) {
    imp0rt = nonTerminals.newSymbol("import");
    path = nonTerminals.newSymbol("path");
    return List.of(imp0rt);
  }

  @Override
  public List<String> requires() {
    return List.of();
  }

  @Override
  public void acceptExternalSymbols(Map<String, Symbol> external) {
  }
}
