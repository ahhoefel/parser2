package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.LocateableList;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.LocateableContainer;

public class StructLiteralRules implements LanguageComponent {

    private class Pair {
        public String identifier;
        public Expression expression;

        public Pair(String identifier, Expression expression) {
            this.identifier = identifier;
            this.expression = expression;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
        Symbol structLiteral = provider.createAndExport("structLiteral");
        Symbol structLiteralArgs = provider.create("structLiteralArgs");
        Symbol structLiteralArg = provider.create("structLiteralArg");

        Symbol expression = provider.require("expression");
        Symbol type = provider.require("type");

        // Do we need the new keyword here? Why is grammar not LR(1)?
        rules.emit(structLiteral, provider.requireTerminal("new"), type, provider.requireTerminal("lbrace"),
                structLiteralArgs,
                provider.requireTerminal("rbrace")).setAction(e -> {
                    StructLiteralExpression expr = new StructLiteralExpression((Type) e[1]);
                    for (Pair p : ((LocateableList<Pair>) e[3]).getList()) {
                        expr.add(p.identifier, p.expression);
                    }
                    expr.setLocation(e[0].getLocation());
                    return expr;
                });
        rules.emit(structLiteralArgs).setAction(e -> new LocateableList<Pair>());
        rules.emit(structLiteralArgs, structLiteralArgs, structLiteralArg).setAction(e -> {
            LocateableList<Pair> args = (LocateableList<Pair>) e[0];
            args.add(((LocateableContainer<Pair>) e[1]).get());
            return args;
        });
        rules.emit(structLiteralArg, provider.requireTerminal("identifier"), provider.requireTerminal("colon"),
                expression,
                provider.requireTerminal("comma"))
                .setAction(
                        e -> new LocateableContainer<>(new Pair(((Token<String>) e[0]).getValue(), (Expression) e[2])));
    }
}
