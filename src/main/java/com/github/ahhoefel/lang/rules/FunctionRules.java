package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;
import com.github.ahhoefel.parser.LocateableList;
import com.github.ahhoefel.parser.LocateableContainer;

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
        .setAction(e -> new FunctionDeclaration((Token) e[1], (LocateableList<VariableDeclaration>) e[3],
            ((LocateableContainer<Optional<Type>>) e[5]).get(),
            (Block) e[7]));
    rules.add(parameterList, parameterList, lex.comma, parameter).setAction(e -> {
      LocateableList<VariableDeclaration> a = (LocateableList<VariableDeclaration>) e[0];
      a.add((VariableDeclaration) e[2]);
      return a;
    });
    rules.add(parameterList, parameter).setAction(e -> {
      LocateableList<VariableDeclaration> a = new LocateableList<>();
      a.add((VariableDeclaration) e[0]);
      return a;
    });
    rules.add(parameterList).setAction(e -> new LocateableList<VariableDeclaration>());
    rules.add(parameter, lex.identifier, type)
        .setAction(e -> new VariableDeclaration(((Token) e[0]).getValue(), (Type) e[1], new CodeLocation(e)));
    rules.add(optionalType, type).setAction(e -> new LocateableContainer<>(Optional.of(e[0])));
    rules.add(optionalType).setAction(e -> new LocateableContainer<>(Optional.empty()));
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
