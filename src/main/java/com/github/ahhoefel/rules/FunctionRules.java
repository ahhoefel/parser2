package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.VariableDeclaration;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionRules implements LanguageComponent {

  // Provides
  private Symbol declaration;

  // Requires
  private Symbol statementList;
  private Symbol type;

  // Internal
  private Symbol parameterList;
  private Symbol parameter;
  private Symbol optionalType;

  @Override
  @SuppressWarnings("unchecked")
  public void provideRules(LanguageBuilder lang) {
    Lexicon lex = lang.getLexicon();
    Rule.Builder rules = lang.getRules();

    rules
        .add(declaration, lex.funcKeyword, lex.identifier, lex.lParen, parameterList, lex.rParen, optionalType,
            lex.lBrace, statementList, lex.rBrace)
        .setAction(e -> new FunctionDeclaration((Token) e[1], (List<VariableDeclaration>) e[3], (Optional<Type>) e[5],
            (Block) e[7]));
    rules.add(parameterList, parameterList, lex.comma, parameter).setAction(e -> {
      ArrayList<VariableDeclaration> a = (ArrayList<VariableDeclaration>) e[0];
      a.add((VariableDeclaration) e[2]);
      return a;
    });
    rules.add(parameterList, parameter).setAction(e -> {
      ArrayList<VariableDeclaration> a = new ArrayList<>();
      a.add((VariableDeclaration) e[0]);
      return a;
    });
    rules.add(parameterList).setAction(e -> new ArrayList<VariableDeclaration>());
    rules.add(parameter, lex.identifier, type)
        .setAction(e -> new VariableDeclaration(((Token) e[0]).getValue(), (Type) e[1]));
    rules.add(optionalType, type).setAction(e -> Optional.of(e[0]));
    rules.add(optionalType).setAction(e -> Optional.empty());
  }

  @Override
  public List<Symbol> provides(SymbolTable nonTerminals) {
    declaration = nonTerminals.newSymbol("functionDeclaration");
    parameterList = nonTerminals.newSymbol("parameterList");
    parameter = nonTerminals.newSymbol("parameter");
    optionalType = nonTerminals.newSymbol("optionalType");
    return List.of(declaration);
  }

  @Override
  public List<String> requires() {
    return List.of("statementList", "type");
  }

  @Override
  public void acceptExternalSymbols(Map<String, Symbol> external) {
    this.statementList = external.get("statementList");
    this.type = external.get("type");
  }
}
