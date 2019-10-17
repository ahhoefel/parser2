package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.expression.*;
import com.github.ahhoefel.parser.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRules {

  Symbol expression;
  private Symbol functionInvocation;
  private Symbol argList;
  private Symbol argument;
  private StructLiteralRules structLiteral;

  public ExpressionRules(SymbolTable nonTerminals) {
    expression = nonTerminals.newSymbol("expression");
    functionInvocation = nonTerminals.newSymbol("functionInvocation");
    argList = nonTerminals.newSymbol("argList");
    argument = nonTerminals.newSymbol("argument");
    structLiteral = new StructLiteralRules(nonTerminals);
  }

  public void provideRules(Rule.Builder rules, Language lang) {
    structLiteral.provideRules(rules, lang);
    Lexicon lex = lang.lex;
    ShiftReduceResolver resolver = lang.resolver;

    Rule identifierRule = rules.add(expression, lex.identifier)
        .setAction(e -> new VariableExpression((Token) e[0]));
    rules.add(expression, lex.number).setAction(e -> new IntegerLiteralExpression((Token) e[0]));
    rules.add(expression, lex.trueKeyword).setAction(e -> new BooleanLiteralExpression(true));
    rules.add(expression, lex.falseKeyword).setAction(e -> new BooleanLiteralExpression(false));
    rules.add(expression, functionInvocation).setAction(e -> e[0]);
    rules.add(expression, structLiteral.structLiteral).setAction(e -> e[0]);

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
    Rule doubleAmpersand = rules.add(expression, expression, lex.doubleAmpersand, expression)
        .setAction(e -> new AndExpression((Expression) e[0], (Expression) e[2]));
    Rule doublePipe = rules.add(expression, expression, lex.doublePipe, expression)
        .setAction(e -> new OrExpression((Expression) e[0], (Expression) e[2]));
    Rule doubleEquals = rules.add(expression, expression, lex.doubleEquals, expression)
        .setAction(e -> new EqualExpression((Expression) e[0], (Expression) e[2]));
    Rule lessThan = rules.add(expression, expression, lex.lessThan, expression)
        .setAction(e -> new LessThanExpression((Expression) e[0], (Expression) e[2]));
    Rule lessThanOrEqual = rules.add(expression, expression, lex.lessThanOrEqual, expression)
        .setAction(e -> new LessThanOrEqualExpression((Expression) e[0], (Expression) e[2]));
    Rule greaterThan = rules.add(expression, expression, lex.greaterThan, expression)
        .setAction(e -> new LessThanExpression((Expression) e[2], (Expression) e[0]));
    Rule greaterThanOrEqual = rules.add(expression, expression, lex.greaterThanOrEqual, expression)
        .setAction(e -> new LessThanOrEqualExpression((Expression) e[2], (Expression) e[0]));
    Rule notEqual = rules.add(expression, expression, lex.notEqual, expression)
        .setAction(e -> new NotExpression(new EqualExpression((Expression) e[2], (Expression) e[0])));

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
                new Pair<>(doubleEquals, lex.doubleEquals),
                new Pair<>(notEqual, lex.notEqual)
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

    resolver.addShiftPreference(identifierRule, lex.lParen);
    resolver.addShiftPreference(memberAccessRule, lex.lParen);
    resolver.addReducePreference(identifierRule, lex.period);
  }
}
