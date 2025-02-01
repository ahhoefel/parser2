package com.github.ahhoefel.lang.rules.lex;

import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;

public class CharacterSet implements LexicalMapping {

    public Symbol letter;
    public Symbol number;
    public Symbol hyphen;
    public Symbol underscore;
    public Symbol space;
    public Symbol tab;
    public Symbol lparen;
    public Symbol rparen;
    public Symbol lbrace;
    public Symbol rbrace;
    public Symbol lbracket;
    public Symbol rbracket;
    public Symbol newline;
    public Symbol period;
    public Symbol comma;
    public Symbol colon;
    public Symbol eq;
    public Symbol plus;
    public Symbol times;
    public Symbol forwardSlash;
    public Symbol bang;
    public Symbol ampersand;
    public Symbol pipe;
    public Symbol unknown;
    public Symbol greaterThan;
    public Symbol lessThan;
    public Symbol eof;

    @Override
    public void provideRanges(RangeEmitter emit) {
        this.number = emit.map('0', '9', "number");
        this.letter = emit.map('a', 'z', "letter");
        this.letter = emit.map('A', 'Z', "letter");
        this.lbracket = emit.map('[', "lbracket");
        this.rbracket = emit.map(']', "rbracket");
        this.underscore = emit.map('_', "underscore");
        this.lbrace = emit.map('{', "lbrace");
        this.rbrace = emit.map('}', "rbrace");
        this.space = emit.map(' ', "space");
        this.tab = emit.map('\t', "tab");
        this.newline = emit.map('\n', "newline");
        this.lparen = emit.map('(', "lparen");
        this.rparen = emit.map(')', "rparen");
        this.times = emit.map('*', "times");
        this.plus = emit.map('+', "plus");
        this.comma = emit.map(',', "comma");
        this.hyphen = emit.map('-', "hyphen");
        this.period = emit.map('.', "period");
        this.forwardSlash = emit.map('/', "forwardSlash");
        this.colon = emit.map(':', "colon");
        this.lessThan = emit.map('<', "lessThan");
        this.eq = emit.map('=', "eq");
        this.greaterThan = emit.map('>', "greaterThan");
        this.bang = emit.map('!', "bang");
        this.ampersand = emit.map('&', "ampersand");
        this.pipe = emit.map('|', "pipe");
    }
}
