package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.statements.AssignmentStatement;
import com.github.ahhoefel.lang.ast.statements.ExpressionStatement;
import com.github.ahhoefel.lang.ast.statements.ForStatement;
import com.github.ahhoefel.lang.ast.statements.IfStatement;
import com.github.ahhoefel.lang.ast.statements.ReturnStatement;
import com.github.ahhoefel.parser.lang.LanguageComponent;

public class StatementRules implements LanguageComponent {

    @SuppressWarnings("unchecked")
    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
        Symbol statementList = provider.createAndExport("statementList");
        Symbol statement = provider.create("statement");
        Symbol lvalue = provider.create("lvalue");
        Symbol variableDeclaration = provider.create("variableDeclaration");

        Symbol expression = provider.require("expression");

        Symbol ifKeyword = provider.requireTerminal("if");
        Symbol lBrace = provider.requireTerminal("lbrace");
        Symbol rBrace = provider.requireTerminal("rbrace");
        Symbol forKeyword = provider.requireTerminal("for");
        Symbol equals = provider.requireTerminal("equals");
        Symbol varKeyword = provider.requireTerminal("var");
        Symbol identifier = provider.requireTerminal("identifier");
        Symbol returnKeyword = provider.requireTerminal("return");
        Symbol hyphen = provider.requireTerminal("hyphen");

        // statement list
        rules.emit(statementList, statementList, statement).setAction(s -> {
            Block block = (Block) s[0];
            block.add((Visitable) s[1]);
            return block;
        });
        rules.emit(statementList, statement).setAction(s -> {
            Block block = new Block();
            block.add((Visitable) s[0]);
            return block;
        });

        // If statement
        rules.emit(statement, ifKeyword, expression, lBrace, statementList, rBrace)
                .setAction(e -> new IfStatement((Expression) e[1], (Block) e[3], ((Token) e[0]).getLocation()));

        // For statement
        rules.emit(statement, forKeyword, expression, lBrace, statementList, rBrace)
                .setAction(e -> new ForStatement((Expression) e[1], (Block) e[3]));

        // Assignment to LValue statement
        Rule statementToLValueAssignment = rules.emit(statement, lvalue, equals, expression)
                .setAction(e -> new AssignmentStatement((LValue) e[0], (Expression) e[2], new CodeLocation(e)));
        rules.emit(lvalue, expression).setAction(e -> new LValue((Expression) e[0], new CodeLocation(e)));

        // Assign to variable declaration statement
        Rule statementToVarDeclAssignment = rules.emit(statement, variableDeclaration, equals, expression)
                .setAction(e -> new AssignmentStatement((VariableDeclaration) e[0], (Expression) e[2],
                        new CodeLocation(e)));
        rules.emit(variableDeclaration, varKeyword, identifier, expression)
                .setAction(e -> new VariableDeclaration(((Token<String>) e[1]).getValue(), (Expression) e[2],
                        new CodeLocation(e)));

        // Return statement
        Rule statementToReturn = rules.emit(statement, returnKeyword, expression)
                .setAction(e -> new ReturnStatement((Expression) e[1]));

        // Expression statement
        Rule statementToExpression = rules.emit(statement, expression)
                .setAction(e -> new ExpressionStatement((Expression) e[0]));

        resolver.addShiftPreference(statementToExpression, hyphen);
        resolver.addShiftPreference(statementToReturn, hyphen);
        resolver.addShiftPreference(statementToLValueAssignment, hyphen);
        resolver.addShiftPreference(statementToVarDeclAssignment, hyphen);
    }
}
