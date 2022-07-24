package com.github.ahhoefel.lang.rules;

import java.util.List;
import java.util.Map;

import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.statements.AssignmentStatement;
import com.github.ahhoefel.lang.ast.statements.ExpressionStatement;
import com.github.ahhoefel.lang.ast.statements.ForStatement;
import com.github.ahhoefel.lang.ast.statements.IfStatement;
import com.github.ahhoefel.lang.ast.statements.ReturnStatement;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;

public class StatementRules implements LanguageComponent {

    // Provides
    private Symbol statementList;

    // Requires
    private Symbol expression;
    private Symbol type;

    // Internal
    private Symbol statement;
    private Symbol lvalue;

    @Override
    public void provideRules(LanguageBuilder lang) {
        Rule.Builder rules = lang.getRules();
        Lexicon lex = lang.getLexicon();

        // statement list
        rules.add(statementList, statementList, statement).setAction(s -> {
            Block block = (Block) s[0];
            block.add((Visitable) s[1]);
            return block;
        });
        rules.add(statementList, statement).setAction(s -> {
            Block block = new Block();
            block.add((Visitable) s[0]);
            return block;
        });

        // If statement
        rules.add(statement, lex.ifKeyword, expression, lex.lBrace, statementList, lex.rBrace)
                .setAction(e -> new IfStatement((Expression) e[1], (Block) e[3], ((Token) e[0]).getLocation()));

        // For statement
        rules.add(statement, lex.forKeyword, expression, lex.lBrace, statementList, lex.rBrace)
                .setAction(e -> new ForStatement((Expression) e[1], (Block) e[3]));

        // Assignment statement
        Rule statementToAssignment = rules.add(statement, lvalue, lex.equals, expression)
                .setAction(e -> new AssignmentStatement((LValue) e[0], (Expression) e[2]));
        rules.add(lvalue, lex.varKeyword, lex.identifier, type)
                .setAction(e -> new VariableDeclaration(((Token) e[1]).getValue(), (Type) e[2]));
        rules.add(lvalue, expression).setAction(e -> new LValue((Expression) e[0], null));

        // Return statement
        Rule statementToReturn = rules.add(statement, lex.returnKeyword, expression)
                .setAction(e -> new ReturnStatement((Expression) e[1]));

        // Expression statement
        Rule statementToExpression = rules.add(statement, expression)
                .setAction(e -> new ExpressionStatement((Expression) e[0]));

        lang.getResolver().addShiftPreference(statementToExpression, lex.hyphen);
        lang.getResolver().addShiftPreference(statementToReturn, lex.hyphen);
        lang.getResolver().addShiftPreference(statementToAssignment, lex.hyphen);
    }

    @Override
    public List<Symbol> provides(SymbolTable nonTerminals) {
        statementList = nonTerminals.newSymbol("statementList");
        statement = nonTerminals.newSymbol("statement");
        lvalue = nonTerminals.newSymbol("lvalue");
        return List.of(statementList);
    }

    @Override
    public List<String> requires() {
        return List.of("expression", "type");
    }

    @Override
    public void acceptExternalSymbols(Map<String, Symbol> external) {
        this.expression = external.get("expression");
        this.type = external.get("type");
    }
}
