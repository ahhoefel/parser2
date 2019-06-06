package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;
import com.github.ahhoefel.ast.*;
import javafx.util.Pair;

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

    Rule identifierRule = rules.add(expression, lex.identifier)
        .setAction(e -> new VariableExpression((Token) e[0]));
    rules.add(expression, lex.number).setAction(e -> new LiteralExpression((Token) e[0]));
    rules.add(expression, functionInvocation).setAction(e -> e[0]);
    Rule memberAccessRule = rules.add(expression, expression, lex.period, lex.identifier)
        .setAction(e -> new MemberAccessExpression((Expression) e[0], (Token) e[2]));
    rules.add(expression, expression, lex.period, functionInvocation)
        .setAction(e -> {
          FunctionInvocationExpression fn = (FunctionInvocationExpression) e[2];
          fn.setImplicitArg((Expression) e[0]);
          return fn;
        });
    Rule plus = rules.add(expression, expression, lex.plus, expression)
        .setAction(e -> new SumExpression((Expression) e[0], (Expression) e[2]));
    Rule minus = rules.add(expression, expression, lex.hyphen, expression)
        .setAction(e -> new SubtractExpression((Expression) e[0], (Expression) e[2]));
    Rule unitaryMinus = rules.add(expression, lex.hyphen, expression)
        .setAction(e -> new UnaryMinusExpression((Expression) e[1]));
    Rule times = rules.add(expression, expression, lex.times, expression)
        .setAction(e -> new ProductExpression((Expression) e[0], (Expression) e[2]));
    rules.add(expression, lex.lParen, expression, lex.rParen)
        .setAction(e -> e[1]);
    Rule doubleAmpersand = rules.add(expression, expression, lex.doubleAmpersand, expression);
    Rule doublePipe = rules.add(expression, expression, lex.doublePipe, expression);
    Rule doubleEquals = rules.add(expression, expression, lex.doubleEquals, expression);
    Rule lessThan = rules.add(expression, expression, lex.lessThan, expression)
        .setAction(e -> new LessThanExpression((Expression) e[0], (Expression) e[2]));
    Rule lessThanOrEqual = rules.add(expression, expression, lex.lessThanOrEqual, expression)
        .setAction(e -> new LessThanOrEqualExpression((Expression) e[0], (Expression) e[2]));
    Rule greaterThan = rules.add(expression, expression, lex.greaterThan, expression)
        .setAction(e -> new LessThanExpression((Expression) e[2], (Expression) e[0]));
    Rule greaterThanOrEqual = rules.add(expression, expression, lex.greaterThanOrEqual, expression)
        .setAction(e -> new LessThanOrEqualExpression((Expression) e[2], (Expression) e[0]));

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

    List<List<Pair<Rule, Symbol>>> precendence =
        List.of(
            List.of(
                new Pair<>(memberAccessRule, lex.period)
            ),
            List.of(
                new Pair<>(unitaryMinus, lex.hyphen)
            ),
            List.of(
                new Pair<>(times, lex.times)
            ),
            List.of(
                new Pair<>(plus, lex.plus),
                new Pair<>(minus, lex.hyphen)
            ),
            List.of(
                new Pair<>(doubleEquals, lex.doubleEquals)
            ),
            List.of(
                new Pair<>(lessThan, lex.lessThan),
                new Pair<>(lessThanOrEqual, lex.lessThanOrEqual),
                new Pair<>(greaterThan, lex.greaterThan),
                new Pair<>(greaterThanOrEqual, lex.greaterThanOrEqual)
            ),
            List.of(
                new Pair<>(doubleAmpersand, lex.doubleAmpersand),
                new Pair<>(doublePipe, lex.doublePipe)
            )
        );

    for (int i = 0; i < precendence.size(); i++) {
      List<Pair<Rule, Symbol>> group = precendence.get(i);
      for (int j = 0; j < group.size(); j++) {
        Pair<Rule, Symbol> pair = group.get(j);
        for (int x = 0; x < precendence.size(); x++) {
          List<Pair<Rule, Symbol>> groupB = precendence.get(x);
          for (int y = 0; y < groupB.size(); y++) {
            Pair<Rule, Symbol> pairB = groupB.get(y);
            if (i <= x) {
              resolver.addReducePreference(pair.getKey(), pairB.getValue());
            } else {
              resolver.addShiftPreference(pair.getKey(), pairB.getValue());
            }
          }
        }
      }
    }

    /*
    resolver.addShiftPreference(times, lex.period);
    resolver.addReducePreference(times, lex.plus);
    resolver.addReducePreference(times, lex.times);
    resolver.addReducePreference(times, lex.hyphen);
    resolver.addReducePreference(times, lex.doubleAmpersand);
    resolver.addReducePreference(times, lex.doublePipe);
    resolver.addReducePreference(times, lex.greaterThan);
    resolver.addReducePreference(times, lex.greaterThanOrEqual);
    resolver.addReducePreference(times, lex.lessThan);
    resolver.addReducePreference(times, lex.lessThanOrEqual);
    resolver.addReducePreference(times, lex.doubleEquals);

    resolver.addShiftPreference(plus, lex.period);
    resolver.addShiftPreference(plus, lex.times);
    resolver.addReducePreference(plus, lex.plus);
    resolver.addReducePreference(plus, lex.hyphen);
    resolver.addReducePreference(plus, lex.doubleAmpersand);
    resolver.addReducePreference(plus, lex.doublePipe);
    resolver.addReducePreference(plus, lex.greaterThan);
    resolver.addReducePreference(plus, lex.greaterThanOrEqual);
    resolver.addReducePreference(plus, lex.lessThan);
    resolver.addReducePreference(plus, lex.lessThanOrEqual);
    resolver.addReducePreference(plus, lex.doubleEquals);

    resolver.addShiftPreference(minus, lex.period);
    resolver.addShiftPreference(minus, lex.times);
    resolver.addReducePreference(minus, lex.plus);
    resolver.addReducePreference(minus, lex.hyphen);
    resolver.addReducePreference(minus, lex.doubleAmpersand);
    resolver.addReducePreference(minus, lex.doublePipe);
    resolver.addReducePreference(minus, lex.greaterThan);
    resolver.addReducePreference(minus, lex.greaterThanOrEqual);
    resolver.addReducePreference(minus, lex.lessThan);
    resolver.addReducePreference(minus, lex.lessThanOrEqual);
    resolver.addReducePreference(minus, lex.doubleEquals);

    resolver.addShiftPreference(doubleEquals, lex.period);
    resolver.addShiftPreference(doubleEquals, lex.times);
    resolver.addShiftPreference(doubleEquals, lex.plus);
    resolver.addShiftPreference(doubleEquals, lex.plus);
    resolver.addShiftPreference(doubleEquals, lex.hyphen);
    resolver.addReducePreference(doubleEquals, lex.doubleAmpersand);
    resolver.addReducePreference(doubleEquals, lex.doublePipe);
    resolver.addReducePreference(doubleEquals, lex.greaterThan);
    resolver.addReducePreference(doubleEquals, lex.greaterThanOrEqual);
    resolver.addReducePreference(doubleEquals, lex.lessThan);
    resolver.addReducePreference(doubleEquals, lex.lessThanOrEqual);
    resolver.addReducePreference(doubleEquals, lex.doubleEquals);

    resolver.addShiftPreference(unitaryMinus, lex.period);
    resolver.addReducePreference(unitaryMinus, lex.plus);
    resolver.addReducePreference(unitaryMinus, lex.times);
    resolver.addReducePreference(unitaryMinus, lex.hyphen);
    */

    resolver.addShiftPreference(identifierRule, lex.lParen);
    resolver.addShiftPreference(memberAccessRule, lex.lParen);
  }
}
