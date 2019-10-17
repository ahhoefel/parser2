package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ast.VariableDeclaration;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionRules {
  Symbol declaration;
  private Symbol parameterList;
  private Symbol parameterListNonEmpty;
  private Symbol parameter;
  private Symbol optionalType;

  public FunctionRules(SymbolTable.NonTerminalTable nonTerminals) {
    declaration = nonTerminals.newSymbol("functionDeclaration");
    parameterList = nonTerminals.newSymbol("parameterList");
    parameterListNonEmpty = nonTerminals.newSymbol("parameterListNonEmpty");
    parameter = nonTerminals.newSymbol("parameter");
    optionalType = nonTerminals.newSymbol("optionalType");
  }

  public void provideRules(Rule.Builder rules, Language lang) {
    Lexicon lex = lang.lex;
    rules.add(declaration, lex.funcKeyword, lex.identifier, lex.lParen, parameterList, lex.rParen, optionalType, lex.lBrace, lang.statement.statementList, lex.rBrace)
        .setAction(e -> new FunctionDeclaration((Token) e[1], (List<VariableDeclaration>) e[3], (Optional<Type>) e[5], (Block) e[7]));
    rules.add(parameterList, parameterList, lex.comma, parameter)
        .setAction(e -> {
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
    rules.add(parameter, lex.identifier, lang.type.type).setAction(e -> new VariableDeclaration(((Token) e[0]).getValue(), (Type) e[1]));
    rules.add(optionalType, lang.type.type).setAction(e -> Optional.of(e[0]));
    rules.add(optionalType).setAction(e -> Optional.empty());
  }
}
