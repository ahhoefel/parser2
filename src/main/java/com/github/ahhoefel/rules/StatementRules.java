package com.github.ahhoefel.rules;

import com.github.ahhoefel.Rule;
import com.github.ahhoefel.Symbol;
import com.github.ahhoefel.SymbolTable;
import com.github.ahhoefel.Token;
import com.github.ahhoefel.ast.*;

public class StatementRules {

  public Symbol statementList;

  public StatementRules(Rule.Builder rules, Lexicon lex, SymbolTable.NonTerminalTable nonTerminals, Symbol expression, Symbol type) {

    statementList = nonTerminals.newSymbol("statementList");
    Symbol statement = nonTerminals.newSymbol("statement");
    Symbol lvalue = nonTerminals.newSymbol("lvalue");

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
    rules.add(statement, lex.ifKeyword, expression, lex.lBrace, statementList, lex.rBrace)
        .setAction(e -> new IfStatement((Expression) e[1], (Block) e[3]));

    // For statement
    rules.add(statement, lex.forKeyword, expression, lex.lBrace, statementList, lex.rBrace)
        .setAction(e -> new ForStatement((Expression) e[1], (Block) e[3]));

    // Assignment statement
    rules.add(statement, lvalue, lex.equals, expression)
        .setAction(e -> new AssignmentStatement((LValue) e[0], (Expression) e[2]));
    rules.add(lvalue, lex.varKeyword, lex.identifier, type).setAction(e -> LValue.withDeclaration((Token) e[1], (Type) e[2]));
    rules.add(lvalue, lex.identifier).setAction(e -> new LValue((Token) e[0]));

    // Return statement
    rules.add(statement, lex.returnKeyword, expression)
        .setAction(e -> new ReturnStatement((Expression) e[1]));
    rules.add(statement, expression)
        .setAction(e -> new ExpressionStatement((Expression) e[0]));
  }


}
