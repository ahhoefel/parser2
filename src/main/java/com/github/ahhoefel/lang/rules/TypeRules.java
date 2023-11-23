package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.lang.ast.*;
import com.github.ahhoefel.lang.ast.type.ArrayType;
import com.github.ahhoefel.lang.ast.type.NamedType;
import com.github.ahhoefel.lang.ast.type.ParameterizedType;
import com.github.ahhoefel.lang.ast.type.StructType;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.lang.ast.type.UnionType;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.LanguageComponent;
import com.github.ahhoefel.parser.LocateableList;

import java.util.List;
import java.util.Map;

public class TypeRules implements LanguageComponent {

    // Provides
    private Symbol type;

    // Internal
    private Symbol typeParams;
    private Symbol members;

    @Override
    @SuppressWarnings("unchecked")
    public void provideRules(LanguageBuilder lang) {
        Lexicon lex = lang.getLexicon();
        Rule.Builder rules = lang.getRules();

        rules.add(type, lex.intKeyword).setAction(e -> new IntType(new CodeLocation(e)));
        rules.add(type, lex.boolKeyword).setAction(e -> new BooleanType(new CodeLocation(e)));
        rules.add(type, lex.stringKeyword).setAction(e -> new StringType(new CodeLocation(e)));
        rules.add(type, lex.structKeyword, lex.lBrace, members, lex.rBrace)
                .setAction(e -> new StructType((LocateableList<Member>) e[2]));
        rules.add(type, lex.unionKeyword, lex.lBrace, members, lex.rBrace)
                .setAction(e -> new UnionType((List<Member>) e[2]));
        rules.add(type, lex.identifier).setAction(e -> new NamedType(((Token) e[0]).getValue()));
        rules.add(type, lex.identifier, lex.period, lex.identifier).setAction(e -> {
            NamedType type = new NamedType(((Token) e[0]).getValue(), ((Token) e[2]).getValue());
            // ((File) e[3]).deferResolution(type);
            return type;
        });
        rules.add(type, type, lex.lBracket, lex.rBracket).setAction(e -> new ArrayType((Type) e[0]));
        rules.add(type, type, lex.lBracket, typeParams, lex.rBracket)
                .setAction(e -> new ParameterizedType((List<Type>) e[2]));
        rules.add(typeParams, typeParams, lex.comma, type).setAction(e -> {
            LocateableList<Type> params = (LocateableList<Type>) e[0];
            params.add((Type) e[2]);
            return params;
        });
        rules.add(typeParams, type).setAction(e -> {
            LocateableList<Type> params = new LocateableList<>();
            params.add((Type) e[0]);
            return params;
        });

        rules.add(members, members, lex.identifier, type).setAction(e -> {
            LocateableList<Member> m = (LocateableList<Member>) e[0];
            m.add(new Member(((Token) e[1]).getValue(), (Type) e[2]));
            return m;
        });
        rules.add(members).setAction(e -> new LocateableList<>());
    }

    @Override
    public List<Symbol> provides(SymbolTable nonTerminals) {
        type = nonTerminals.newSymbol("type");
        typeParams = nonTerminals.newSymbol("typeParams");
        members = nonTerminals.newSymbol("members");
        return List.of(type);
    }

    @Override
    public List<String> requires() {
        return List.of();
    }

    @Override
    public void acceptExternalSymbols(Map<String, Symbol> external) {
    }
}
