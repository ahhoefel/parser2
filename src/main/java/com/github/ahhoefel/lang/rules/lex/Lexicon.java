package com.github.ahhoefel.lang.rules.lex;

import com.github.ahhoefel.parser.*;
import com.github.ahhoefel.parser.action.TokenAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class Lexicon extends LayeredParser.Layer<Iterator<Token<String>>, Iterator> {
        public Lexicon() {
                super("LexiconLayer", Iterator.class, new TerminalLayeredParser(new CharacterSet()), "start",
                                new Component(),
                                new Identifier(),
                                new Whitespace(), new Number());
        }

        private static class Component implements LanguageComponent {
                @Override
                public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
                        Symbol balanced = provider.createAndExport("balanced");
                        Rule concat = rules.emit(balanced, balanced, balanced);

                        // Symbol alphanumeric = provider.requireTerminal("alphanumeric");
                        Symbol lparen = provider.require("lparen");
                        // resolver.addShiftPreference(concat, alphanumeric);
                        resolver.addShiftPreference(concat, lparen);

                        Symbol period = provider.createAndExport("period");
                        Symbol lParen = provider.createAndExport("lparen");
                        Symbol rParen = provider.createAndExport("rparen");
                        Symbol lBrace = provider.createAndExport("lbrace");
                        Symbol rBrace = provider.createAndExport("rbrace");
                        Symbol rBracket = provider.createAndExport("rbracket");
                        Symbol lBracket = provider.createAndExport("lbracket");
                        Symbol comma = provider.createAndExport("comma");
                        Symbol colon = provider.createAndExport("colon");
                        Symbol equals = provider.createAndExport("equals");
                        Symbol plus = provider.createAndExport("plus");
                        Symbol times = provider.createAndExport("times");
                        Symbol hyphen = provider.createAndExport("hyphen");
                        Symbol forwardSlash = provider.createAndExport("forwardSlash");
                        Symbol bang = provider.createAndExport("bang");
                        Symbol doubleAmpersand = provider.createAndExport("doubleAmpersand");
                        Symbol doublePipe = provider.createAndExport("doublePipe");
                        Symbol greaterThan = provider.createAndExport("greaterThan");
                        Symbol greaterThanOrEqual = provider.createAndExport("greaterThanOrEqual");
                        Symbol lessThan = provider.createAndExport("lessThan");
                        Symbol lessThanOrEqual = provider.createAndExport("lessThanOrEqual");
                        Symbol doubleEquals = provider.createAndExport("doubleEquals");
                        Symbol notEqual = provider.createAndExport("notEqual");

                        Symbol identifier = provider.require("identifier");
                        Symbol whitespace = provider.require("whitespace");
                        Symbol number = provider.require("number");

                        List<Symbol> keywords = List.of(
                                        provider.createAndExport("for"),
                                        provider.createAndExport("if"),
                                        provider.createAndExport("func"),
                                        provider.createAndExport("var"),
                                        provider.createAndExport("int"),
                                        provider.createAndExport("bool"),
                                        provider.createAndExport("string"),
                                        provider.createAndExport("return"),
                                        provider.createAndExport("import"),
                                        provider.createAndExport("type"),
                                        provider.createAndExport("struct"),
                                        provider.createAndExport("union"),
                                        provider.createAndExport("new"),
                                        provider.createAndExport("true"),
                                        provider.createAndExport("false"));

                        Symbol start = provider.createAndExport("start");
                        Symbol word = provider.create("word");
                        final Function<Locateable[], Locateable> appendAction = new Function<Locateable[], Locateable>() {
                                @SuppressWarnings("unchecked")
                                @Override
                                public Locateable apply(Locateable[] objects) {
                                        LocateableList list;
                                        if (objects.length == 0) {
                                                list = new LocateableList();
                                        } else if (objects[0] instanceof LocateableList) {
                                                list = (LocateableList) objects[0];
                                        } else {
                                                list = new LocateableList();
                                                if (((Token<String>) objects[0]).getSymbol() != whitespace) {
                                                        list.add(objects[0]);
                                                }
                                        }
                                        if (objects.length > 1 && objects[1] != null
                                                        && ((Token<String>) objects[1]).getSymbol() != whitespace) {
                                                list.add(objects[1]);
                                        }
                                        return list;
                                }

                        };
                        rules.emit(start).setAction(appendAction);
                        rules.emit(start, start, word).setAction(appendAction);
                        rules.emit(word, identifier).setAction(new TokenAction(identifier, keywords));
                        rules.emit(word, whitespace).setAction(new TokenAction(whitespace));
                        Rule wordIsNumber = rules.emit(word, number).setAction(new TokenAction(number));
                        rules.emit(word, provider.requireTerminal("period")).setAction(new TokenAction(period));
                        rules.emit(word, provider.requireTerminal("lparen")).setAction(new TokenAction(lParen));
                        rules.emit(word, provider.requireTerminal("rparen")).setAction(new TokenAction(rParen));
                        rules.emit(word, provider.requireTerminal("lbrace")).setAction(new TokenAction(lBrace));
                        rules.emit(word, provider.requireTerminal("rbrace")).setAction(new TokenAction(rBrace));
                        rules.emit(word, provider.requireTerminal("lbracket")).setAction(new TokenAction(lBracket));
                        rules.emit(word, provider.requireTerminal("rbracket")).setAction(new TokenAction(rBracket));
                        rules.emit(word, provider.requireTerminal("comma")).setAction(new TokenAction(comma));
                        rules.emit(word, provider.requireTerminal("colon")).setAction(new TokenAction(colon));
                        Rule wordIsSingleEqual = rules.emit(word, provider.requireTerminal("eq"))
                                        .setAction(new TokenAction(equals));
                        rules.emit(word, provider.requireTerminal("times")).setAction(new TokenAction(times));
                        rules.emit(word, provider.requireTerminal("plus")).setAction(new TokenAction(plus));
                        Rule wordIsHyphen = rules.emit(word, provider.requireTerminal("hyphen"))
                                        .setAction(new TokenAction(hyphen));
                        rules.emit(word, provider.requireTerminal("forwardSlash"))
                                        .setAction(new TokenAction(forwardSlash));
                        Rule wordIsBang = rules.emit(word, provider.requireTerminal("bang"))
                                        .setAction(new TokenAction(bang));
                        rules.emit(word, provider.requireTerminal("ampersand"), provider.requireTerminal("ampersand"))
                                        .setAction(new TokenAction(doubleAmpersand));
                        rules.emit(word, provider.requireTerminal("pipe"), provider.requireTerminal("pipe"))
                                        .setAction(new TokenAction(doublePipe));
                        Rule wordIsGreaterThan = rules.emit(word, provider.requireTerminal("greaterThan"))
                                        .setAction(new TokenAction(greaterThan));
                        rules.emit(word, provider.requireTerminal("greaterThan"), provider.requireTerminal("eq"))
                                        .setAction(new TokenAction(greaterThanOrEqual));
                        Rule wordIsLessThan = rules.emit(word, provider.requireTerminal("lessThan"))
                                        .setAction(new TokenAction(lessThan));
                        rules.emit(word, provider.requireTerminal("lessThan"), provider.requireTerminal("eq"))
                                        .setAction(new TokenAction(lessThanOrEqual));
                        rules.emit(word, provider.requireTerminal("eq"), provider.requireTerminal("eq"))
                                        .setAction(new TokenAction(doubleEquals));
                        rules.emit(word, provider.requireTerminal("bang"), provider.requireTerminal("eq"))
                                        .setAction(new TokenAction(notEqual));

                        resolver.addShiftPreference(wordIsNumber, provider.requireTerminal("number"));
                        resolver.addShiftPreference(wordIsHyphen, provider.requireTerminal("number"));
                        resolver.addShiftPreference(wordIsGreaterThan, provider.requireTerminal("eq"));
                        resolver.addShiftPreference(wordIsLessThan, provider.requireTerminal("eq"));
                        resolver.addShiftPreference(wordIsSingleEqual, provider.requireTerminal("eq"));
                        resolver.addShiftPreference(wordIsBang, provider.requireTerminal("eq"));
                }
        }
}
