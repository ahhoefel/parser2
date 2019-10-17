package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.*;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;

public class StatementRules {

  Symbol statementList;
  private Symbol statement;
  private Symbol lvalue;

  public StatementRules(SymbolTable.NonTerminalTable nonTerminals) {
    statementList = nonTerminals.newSymbol("statementList");
    statement = nonTerminals.newSymbol("statement");
    lvalue = nonTerminals.newSymbol("lvalue");
  }

  public void provideRules(Rule.Builder rules, Language lang) {
    Lexicon lex = lang.lex;

    // statement list
    rules.add(statementList, statementList, statement)
        .setAction(s -> {
          Block block = (Block) s[0];
          block.add((Statement) s[1]);
          return block;
        });
    rules.add(statementList, statement)
        .setAction(s -> {
          Block block = new Block();
          block.add((Statement) s[0]);
          return block;
        });

    // If statement
    rules.add(statement, lex.ifKeyword, lang.expression.expression, lex.lBrace, statementList, lex.rBrace)
        .setAction(e -> new IfStatement((Expression) e[1], (Block) e[3], ((Token) e[0]).getLocation()));

    // For statement
    rules.add(statement, lex.forKeyword, lang.expression.expression, lex.lBrace, statementList, lex.rBrace)
        .setAction(e -> new ForStatement((Expression) e[1], (Block) e[3]));

    // Assignment statement
    Rule statementToAssignment = rules.add(statement, lvalue, lex.equals, lang.expression.expression)
        .setAction(e -> new AssignmentStatement((LValue) e[0], (Expression) e[2]));
    rules.add(lvalue, lex.varKeyword, lex.identifier, lang.type.type).setAction(e -> LValue.withDeclaration((Token) e[1], (Type) e[2]));
    rules.add(lvalue, lang.expression.expression).setAction(e -> LValue.fromExpression((Expression) e[0], null));

    // Return statement
    Rule statementToReturn = rules.add(statement, lex.returnKeyword, lang.expression.expression)
        .setAction(e -> new ReturnStatement((Expression) e[1]));

    // Expression statement
    Rule statementToExpression = rules.add(statement, lang.expression.expression)
        .setAction(e -> new ExpressionStatement((Expression) e[0]));

    lang.resolver.addShiftPreference(statementToExpression, lex.hyphen);
    lang.resolver.addShiftPreference(statementToReturn, lex.hyphen);
    lang.resolver.addShiftPreference(statementToAssignment, lex.hyphen);
  }
}
