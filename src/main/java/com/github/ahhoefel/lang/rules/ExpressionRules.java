package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.lang.ast.expression.*;
import com.github.ahhoefel.parser.*;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRules implements LanguageComponent {

        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {

                Symbol expression = provider.createAndExport("expression");
                Symbol newExpression = provider.create("newExpression");
                Symbol functionInvocation = provider.create("functionInvocation");
                Symbol argList = provider.create("argList");
                Symbol argument = provider.create("argument");

                Symbol structLiteral = provider.require("structLiteral");
                Symbol type = provider.require("type");

                Rule identifierRule = rules.emit(expression, provider.requireTerminal("identifier"))
                                .setAction(e -> new VariableExpression((Token) e[0]));
                rules.emit(expression, provider.requireTerminal("number"))
                                .setAction(e -> new IntegerLiteralExpression((Token) e[0]));
                rules.emit(expression, provider.requireTerminal("true"))
                                .setAction(e -> new BooleanLiteralExpression(true, new CodeLocation(e)));
                rules.emit(expression, provider.requireTerminal("false"))
                                .setAction(e -> new BooleanLiteralExpression(false, new CodeLocation(e)));
                rules.emit(expression, functionInvocation).setAction(e -> e[0]);
                rules.emit(expression, newExpression).setAction(e -> e[0]);
                rules.emit(expression, structLiteral).setAction(e -> e[0]);
                rules.emit(expression, type).setAction(e -> e[0]);

                @SuppressWarnings("unchecked")
                Rule memberAccessRule = rules
                                .emit(expression, expression, provider.requireTerminal("period"),
                                                provider.requireTerminal("identifier"))
                                .setAction(e -> new MemberAccessExpression((Expression) e[0], (Token<String>) e[2],
                                                new CodeLocation(e)));
                rules.emit(expression, expression, provider.requireTerminal("period"), functionInvocation)
                                .setAction(e -> {
                                        FunctionInvocationExpression fn = (FunctionInvocationExpression) e[2];
                                        fn.setImplicitArg((Expression) e[0]);
                                        return fn;
                                });

                Rule indexAccessRule = rules
                                .emit(expression, expression, provider.requireTerminal("lbracket"), expression,
                                                provider.requireTerminal("rbracket"))
                                .setAction(e -> new IndexAccessExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));

                Rule plus = rules.emit(expression, expression, provider.requireTerminal("plus"), expression)
                                .setAction(e -> new SumExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule minus = rules.emit(expression, expression, provider.requireTerminal("hyphen"), expression)
                                .setAction(e -> new SubtractExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule unaryMinus = rules.emit(expression, provider.requireTerminal("hyphen"), expression)
                                .setAction(e -> new UnaryMinusExpression((Expression) e[1], new CodeLocation(e)));
                Rule times = rules.emit(expression, expression, provider.requireTerminal("times"), expression)
                                .setAction(e -> new ProductExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                rules.emit(expression, provider.requireTerminal("lparen"), expression,
                                provider.requireTerminal("rparen"))
                                .setAction(e -> new ParenthesesExpression((Expression) e[1], new CodeLocation(e)));
                Rule doubleAmpersand = rules
                                .emit(expression, expression, provider.requireTerminal("doubleAmpersand"), expression)
                                .setAction(e -> new AndExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule doublePipe = rules.emit(expression, expression, provider.requireTerminal("doublePipe"), expression)
                                .setAction(e -> new OrExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule doubleEquals = rules
                                .emit(expression, expression, provider.requireTerminal("doubleEquals"), expression)
                                .setAction(e -> new EqualExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule lessThan = rules.emit(expression, expression, provider.requireTerminal("lessThan"), expression)
                                .setAction(e -> new LessThanExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule lessThanOrEqual = rules
                                .emit(expression, expression, provider.requireTerminal("lessThanOrEqual"), expression)
                                .setAction(e -> new LessThanOrEqualExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));
                Rule greaterThan = rules
                                .emit(expression, expression, provider.requireTerminal("greaterThan"), expression)
                                .setAction(e -> new LessThanExpression((Expression) e[2], (Expression) e[0],
                                                new CodeLocation(e)));
                Rule greaterThanOrEqual = rules
                                .emit(expression, expression, provider.requireTerminal("greaterThanOrEqual"),
                                                expression)
                                .setAction(e -> new LessThanOrEqualExpression((Expression) e[2], (Expression) e[0],
                                                new CodeLocation(e)));
                Rule notEqual = rules.emit(expression, expression, provider.requireTerminal("notEqual"), expression)
                                .setAction(e -> new NotEqualExpression((Expression) e[0], (Expression) e[2],
                                                new CodeLocation(e)));

                rules.emit(functionInvocation, provider.requireTerminal("identifier"),
                                provider.requireTerminal("lparen"), provider.requireTerminal("rparen")).setAction(
                                                e -> new FunctionInvocationExpression((Token) e[0],
                                                                new ArrayList<Expression>(),
                                                                new CodeLocation(e)));
                rules.emit(functionInvocation, provider.requireTerminal("identifier"),
                                provider.requireTerminal("lparen"), argList, provider.requireTerminal("rparen"))
                                .setAction(
                                                e -> new FunctionInvocationExpression((Token) e[0],
                                                                ((LocateableList<Expression>) e[2]).getList(),
                                                                new CodeLocation(e)));

                rules.emit(newExpression, provider.requireTerminal("new"), expression,
                                provider.requireTerminal("lparen"), provider.requireTerminal("rparen"))
                                .setAction(e -> new NewExpression((Expression) e[1], List.of(), new CodeLocation(e)));
                rules.emit(newExpression, provider.requireTerminal("new"), expression,
                                provider.requireTerminal("lparen"), argList, provider.requireTerminal("rparen"))
                                .setAction(
                                                e -> new NewExpression((Expression) e[1],
                                                                ((LocateableList<Expression>) e[3]).getList(),
                                                                new CodeLocation(e)));

                rules.emit(argList, argument).setAction(e -> {
                        LocateableList<Expression> args = new LocateableList<Expression>();
                        args.add((Expression) e[0]);
                        args.setLocation(e[0].getLocation());
                        return args;
                });
                rules.emit(argList, argList, provider.requireTerminal("comma"), argument).setAction(e -> {
                        LocateableList<Expression> args = (LocateableList<Expression>) e[0];
                        args.add((Expression) e[2]);
                        args.setLocation(new CodeLocation(e));
                        return args;
                });
                rules.emit(argument, expression).setAction(e -> e[0]);

                List<List<Pair<Rule, Symbol>>> precendence = List.of(
                                List.of(new Pair<>(indexAccessRule, provider.requireTerminal("lbracket"))),
                                List.of(new Pair<>(memberAccessRule, provider.requireTerminal("period"))),
                                List.of(new Pair<>(unaryMinus, provider.requireTerminal("hyphen"))),
                                List.of(new Pair<>(times, provider.requireTerminal("times"))),
                                List.of(new Pair<>(plus, provider.requireTerminal("plus")),
                                                new Pair<>(minus, provider.requireTerminal("hyphen"))),
                                List.of(new Pair<>(doubleEquals, provider.requireTerminal("doubleEquals")),
                                                new Pair<>(notEqual, provider.requireTerminal("notEqual"))),
                                List.of(new Pair<>(lessThan, provider.requireTerminal("lessThan")),
                                                new Pair<>(lessThanOrEqual,
                                                                provider.requireTerminal("lessThanOrEqual")),
                                                new Pair<>(greaterThan, provider.requireTerminal("greaterThan")),
                                                new Pair<>(greaterThanOrEqual,
                                                                provider.requireTerminal("greaterThanOrEqual"))),
                                List.of(new Pair<>(doubleAmpersand, provider.requireTerminal("doubleAmpersand")),
                                                new Pair<>(doublePipe, provider.requireTerminal("doublePipe"))));

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

                resolver.addShiftPreference(identifierRule, provider.requireTerminal("lparen"));
                resolver.addShiftPreference(memberAccessRule, provider.requireTerminal("lparen"));
                resolver.addReducePreference(identifierRule, provider.requireTerminal("period"));
        }
}
