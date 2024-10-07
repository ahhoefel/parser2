package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.ShiftReduceResolver;

public class TypeRules implements LanguageComponent {

    // Internal
    // private Symbol typeParams;
    // private Symbol members;

    @Override
    public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
        Symbol type = provider.createAndExport("type");

        rules.emit(type, provider.requireTerminal("int")).setAction(e -> new IntType(new CodeLocation(e)));
        rules.emit(type, provider.requireTerminal("bool")).setAction(e -> new BooleanType(new CodeLocation(e)));
        rules.emit(type, provider.requireTerminal("string")).setAction(e -> new StringType(new CodeLocation(e)));
        // rules.add(type, lex.identifier).setAction(e -> new NamedType(((Token)
        // e[0]).getValue(), new CodeLocation(e)));
        // rules.add(type, lex.identifier, lex.period, lex.identifier).setAction(e -> {
        // NamedType type = new NamedType(((Token) e[0]).getValue(), ((Token)
        // e[2]).getValue(), new CodeLocation(e));
        // // ((File) e[3]).deferResolution(type);
        // return type;
        // });
        // rules.add(concreteType, lex.structKeyword, lex.lBrace, members, lex.rBrace)
        // .setAction(e -> new StructType((LocateableList<Member>) e[2]));
        // rules.add(concreteType, lex.unionKeyword, lex.lBrace, members, lex.rBrace)
        // .setAction(e -> new UnionType((List<Member>) e[2]));

        // rules.add(concreteType, type, lex.lBracket, lex.rBracket).setAction(e -> new
        // ArrayType((Type) e[0]));
        // rules.add(concreteType, type, lex.lBracket, typeParams, lex.rBracket)
        // .setAction(e -> new ParameterizedType((Type) e[0], (LocateableList<Type>)
        // e[2], new CodeLocation(e)));
        // rules.add(typeParams, typeParams, lex.comma, type).setAction(e -> {
        // LocateableList<Type> params = (LocateableList<Type>) e[0];
        // params.add((Type) e[2]);
        // params.setLocation(new CodeLocation(e));
        // return params;
        // });
        // rules.add(typeParams, type).setAction(e -> {
        // LocateableList<Type> params = new LocateableList<>();
        // params.add((Type) e[0]);
        // params.setLocation(e[0].getLocation());
        // return params;
        // });

        // rules.add(members, members, lex.identifier, type).setAction(e -> {
        // LocateableList<Member> m = (LocateableList<Member>) e[0];
        // m.add(new Member(((Token) e[1]).getValue(), (Type) e[2]));
        // return m;
        // });
        // rules.add(members).setAction(e -> new LocateableList<>());
    }

}
