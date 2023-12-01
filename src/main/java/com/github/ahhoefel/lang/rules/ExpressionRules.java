package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.expression.*;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionRules implements LanguageComponent {
        // Provides
        private Symbol expression;

        // Internal
        private Symbol functionInvocation;
        private Symbol newExpression;
        private Symbol argList;
        private Symbol argument;

        // External
        private Symbol structLiteral;
        private Symbol type;

        public List<Symbol> provides(SymbolTable nonTerminals) {
                expression = nonTerminals.newSymbol("expression");
                newExpression = nonTerminals.newSymbol("newExpression");
                functionInvocation = nonTerminals.newSymbol("functionInvocation");
                argList = nonTerminals.newSymbol("argList");
                argument = nonTerminals.newSymbol("argument");
                return List.of(expression);
        }

        public List<String> requires() {
                return List.of("structLiteral", "type");
        }

        public void acceptExternalSymbols(Map<String, Symbol> external) {
                this.structLiteral = external.get("structLiteral");
                this.type = external.get("type");
        }

        @SuppressWarnings("unchecked")
        public void provideRules(LanguageBuilder lang) {
                Rule.Builder rules = lang.getRules();
                Lexicon lex = lang.getLexicon();
                ShiftReduceResolver resolver = lang.getResolver();

                Rule identifierRule = rules.add(expression, lex.identifier)
                                .setAction(e -> new VariableExpression((Token) e[0]));
                rules.add(expression, lex.number).setAction(e -> new IntegerLiteralExpression((Token) e[0]));
                rules.add(expression, lex.trueKeyword)
                                .setAction(e -> new BooleanLiteralExpression(true, new CodeLocation(e)));
                rules.add(expression, lex.falseKeyword)
                                .setAction(e -> new BooleanLiteralExpression(false, new CodeLocation(e)));
                rules.add(expression, functionInvocation).setAction(e -> e[0]);
                rules.add(expression, newExpression).setAction(e -> e[0]);
                rules.add(expression, structLiteral).setAction(e -> e[0]);
                rules.add(expression, type).setAction(e -> e[0]);

                Rule memberAccessRule = rules.add(expression, expression, lex.period, lex.identifier)
                                .setAction(e -> new MemberAccessExpression((Expression) e[0], (Token) e[2]));
                rules.add(expression, expression, lex.period, functionInvocation).setAction(e -> {
                        FunctionInvocationExpression fn = (FunctionInvocationExpression) e[2];
                        fn.setImplicitArg((Expression) e[0]);
                        return fn;
                });

                Rule indexAccessRule = rules.add(expression, expression, lex.lBracket, expression, lex.rBracket)
                                .setAction(e -> new IndexAccessExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));

                Rule plus = rules.add(expression, expression, lex.plus, expression)
                                .setAction(e -> new SumExpression((Expression) e[0], (Expression) e[2]));
                Rule minus = rules.add(expression, expression, lex.hyphen, expression)
                                .setAction(e -> new SubtractExpression((Expression) e[0], (Expression) e[2]));
                Rule unaryMinus = rules.add(expression, lex.hyphen, expression)
                                .setAction(e -> new UnaryMinusExpression((Expression) e[1]));
                Rule times = rules.add(expression, expression, lex.times, expression)
                                .setAction(e -> new ProductExpression((Expression) e[0], (Expression) e[2]));
                rules.add(expression, lex.lParen, expression, lex.rParen)
                                .setAction(e -> new ParenthesesExpression((Expression) e[1]));
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
                                .setAction(e -> new NotEqualExpression((Expression) e[0], (Expression) e[2]));

                rules.add(functionInvocation, lex.identifier, lex.lParen, lex.rParen).setAction(
                                e -> new FunctionInvocationExpression((Token) e[0], new ArrayList<Expression>()));
                rules.add(functionInvocation, lex.identifier, lex.lParen, argList, lex.rParen).setAction(
                                e -> new FunctionInvocationExpression((Token) e[0],
                                                ((LocateableList<Expression>) e[2]).getList()));

                rules.add(newExpression, lex.newKeyword, expression, lex.lParen, lex.rParen)
                                .setAction(e -> new NewExpression((Expression) e[1], List.of(), new CodeLocation(e)));
                rules.add(newExpression, lex.newKeyword, expression, lex.lParen, argList, lex.rParen).setAction(
                                e -> new NewExpression((Expression) e[1],
                                                ((LocateableList<Expression>) e[3]).getList(), new CodeLocation(e)));

                rules.add(argList, argument).setAction(e -> {
                        LocateableList<Expression> args = new LocateableList<Expression>();
                        args.add((Expression) e[0]);
                        args.setLocation(e[0].getLocation());
                        return args;
                });
                rules.add(argList, argList, lex.comma, argument).setAction(e -> {
                        LocateableList<Expression> args = (LocateableList<Expression>) e[0];
                        args.add((Expression) e[2]);
                        args.setLocation(new CodeLocation(e));
                        return args;
                });
                rules.add(argument, expression).setAction(e -> e[0]);

                List<List<Pair<Rule, Symbol>>> precendence = List.of(
                                List.of(new Pair<>(indexAccessRule, lex.lBracket)),
                                List.of(new Pair<>(memberAccessRule, lex.period)),
                                List.of(new Pair<>(unaryMinus, lex.hyphen)),
                                List.of(new Pair<>(times, lex.times)),
                                List.of(new Pair<>(plus, lex.plus), new Pair<>(minus, lex.hyphen)),
                                List.of(new Pair<>(doubleEquals, lex.doubleEquals), new Pair<>(notEqual, lex.notEqual)),
                                List.of(new Pair<>(lessThan, lex.lessThan),
                                                new Pair<>(lessThanOrEqual, lex.lessThanOrEqual),
                                                new Pair<>(greaterThan, lex.greaterThan),
                                                new Pair<>(greaterThanOrEqual, lex.greaterThanOrEqual)),
                                List.of(new Pair<>(doubleAmpersand, lex.doubleAmpersand),
                                                new Pair<>(doublePipe, lex.doublePipe)));

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
