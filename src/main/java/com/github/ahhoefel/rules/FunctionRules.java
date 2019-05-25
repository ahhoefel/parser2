package com.github.ahhoefel.rules;

import com.github.ahhoefel.Rule;
import com.github.ahhoefel.Symbol;
import com.github.ahhoefel.SymbolTable;
import com.github.ahhoefel.Token;
import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.Parameter;
import com.github.ahhoefel.ast.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionRules {
  public Symbol declaration;
  public Symbol parameterList;
  public Symbol parameterListNonEmpty;
  public Symbol parameter;
  public Symbol optionalType;

  public FunctionRules(Rule.Builder rules, Lexicon lex, SymbolTable.NonTerminalTable nonTerminals, Symbol statementList, Symbol type) {
    declaration = nonTerminals.newSymbol("functionDeclaration");
    parameterList = nonTerminals.newSymbol("parameterList");
    parameterListNonEmpty = nonTerminals.newSymbol("parameterListNonEmpty");
    parameter = nonTerminals.newSymbol("parameter");
    optionalType = nonTerminals.newSymbol("optionalType");

    rules.add(declaration, lex.funcKeyword, lex.identifier, lex.lParen, parameterList, lex.rParen, optionalType, lex.lBrace, statementList, lex.rBrace)
        .setAction(e -> new FunctionDeclaration((Token) e[1], (List<Parameter>) e[3], (Optional<Type>) e[5], (Block) e[7]));
    rules.add(parameterList, parameter, lex.comma, parameterList)
        .setAction(e -> {
          ArrayList<Parameter> a = (ArrayList<Parameter>) e[2];
          a.add((Parameter) e[0]);
          return a;
        });
    rules.add(parameterList, parameter).setAction(e -> {
      ArrayList<Parameter> a = new ArrayList<>();
      a.add((Parameter) e[0]);
      return a;
    });
    rules.add(parameterList).setAction(e -> new ArrayList<Parameter>());
    rules.add(parameter, lex.identifier, lex.identifier).setAction(e -> new Parameter((Token) e[0], (Token) e[1]));
    rules.add(optionalType, type).setAction(e -> Optional.of(e[0]));
    rules.add(optionalType).setAction(e -> Optional.empty());

  }
}
