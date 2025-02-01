package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.LocateableList;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.LocateableContainer;

import java.util.Optional;

public class FunctionRules implements LanguageComponent {

    @SuppressWarnings("unchecked")
    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {

        Symbol declaration = provider.createAndExport("functionDeclaration");
        Symbol parameterList = provider.create("parameterList");
        Symbol parameter = provider.create("parameter");
        Symbol optionalType = provider.create("optionalType");

        Symbol statementList = provider.require("statementList");
        Symbol type = provider.require("type");

        rules
                .emit(declaration,
                        provider.requireTerminal("func"),
                        provider.requireTerminal("identifier"),
                        provider.requireTerminal("lparen"),
                        parameterList,
                        provider.requireTerminal("rparen"),
                        optionalType,
                        provider.requireTerminal("lbrace"),
                        statementList,
                        provider.requireTerminal("rbrace"))
                .setAction(e -> new FunctionDeclaration((Token) e[1], (LocateableList<VariableDeclaration>) e[3],
                        ((LocateableContainer<Optional<Type>>) e[5]).get(),
                        (Block) e[7]));
        rules.emit(parameterList, parameterList, provider.requireTerminal("comma"), parameter).setAction(e -> {
            LocateableList<VariableDeclaration> a = (LocateableList<VariableDeclaration>) e[0];
            a.add((VariableDeclaration) e[2]);
            return a;
        });
        rules.emit(parameterList, parameter).setAction(e -> {
            LocateableList<VariableDeclaration> a = new LocateableList<>();
            a.add((VariableDeclaration) e[0]);
            return a;
        });
        rules.emit(parameterList).setAction(e -> new LocateableList<VariableDeclaration>());
        rules.emit(parameter, provider.requireTerminal("identifier"), type)
                .setAction(e -> new VariableDeclaration(((Token<String>) e[0]).getValue(), (Type) e[1],
                        new CodeLocation(e)));
        rules.emit(optionalType, type).setAction(e -> new LocateableContainer<>(Optional.of(e[0])));
        rules.emit(optionalType).setAction(e -> new LocateableContainer<>(Optional.empty()));
    }
}
