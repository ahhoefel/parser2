package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;
import com.github.ahhoefel.ast.*;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRules {

  public Symbol expression;
  public Symbol functionInvocation;
  public Symbol argList;
  public Symbol argument;

  public ExpressionRules(Rule.Builder rules, SymbolTable nonTerminals, Lexicon lex, ShiftReduceResolver resolver) {
    expression = nonTerminals.newSymbol("expression");
    functionInvocation = nonTerminals.newSymbol("functionInvocation");
    argList = nonTerminals.newSymbol("argList");
    argument = nonTerminals.newSymbol("argument");

    rules.add(expression, lex.identifier)
        .setAction(e -> new VariableExpression((Token) e[0]));
    rules.add(expression, lex.number).setAction(e -> new LiteralExpression((Token) e[0]));
    rules.add(expression, functionInvocation).setAction(e -> e[0]);
    rules.add(expression, expression, lex.period, lex.identifier)
        .setAction(e -> new MemberAccessExpression((Expression) e[0], (Token) e[2]));
    rules.add(expression, expression, lex.period, functionInvocation)
        .setAction(e -> new MethodInvocationExpression((Expression) e[0], (FunctionInvocationExpression) e[2]));
    Rule plus = rules.add(expression, expression, lex.plus, expression)
        .setAction(e -> new SumExpression((Expression) e[0], (Expression) e[2]));
    Rule times = rules.add(expression, expression, lex.times, expression)
        .setAction(e -> new ProductExpression((Expression) e[0], (Expression) e[2]));
    rules.add(functionInvocation, lex.identifier, lex.lParen, lex.rParen)
        .setAction(e -> new FunctionInvocationExpression((Token) e[0], new ArrayList<Expression>()));
    rules.add(functionInvocation, lex.identifier, lex.lParen, argList, lex.rParen)
        .setAction(e -> new FunctionInvocationExpression((Token) e[0], (List<Expression>) e[2]));
    rules.add(argList, argument)
        .setAction(e -> {
          List<Expression> args = new ArrayList<>();
          args.add((Expression) e[0]);
          return args;
        });
    rules.add(argList, argList, lex.comma, argument)
        .setAction(e -> {
          List<Expression> args = (List<Expression>) e[0];
          args.add((Expression) e[2]);
          return args;
        });
    rules.add(argument, expression).setAction(e -> e[0]);

    resolver.addShiftPreference(times, lex.period);
    resolver.addReducePreference(times, lex.plus);
    resolver.addReducePreference(times, lex.times);
    resolver.addShiftPreference(plus, lex.period);
    resolver.addReducePreference(plus, lex.plus);
    resolver.addShiftPreference(plus, lex.times);

  }
}
