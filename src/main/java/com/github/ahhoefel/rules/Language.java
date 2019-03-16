package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Language {

  private Lexicon lex;
  private SymbolTable symbols;
  private Grammar grammar;
  private LRTable table;

  private NonTerminalSymbol expression;
  private NonTerminalSymbol functionInvocation;
  private NonTerminalSymbol argList;
  private NonTerminalSymbol argument;

  public Language() {
    lex = new Lexicon();
    symbols = lex.getSymbols();
    expression = symbols.newNonTerminal("expression");
    functionInvocation = symbols.newNonTerminal("functionInvocation");
    argList = symbols.newNonTerminal("argList");
    argument = symbols.newNonTerminal("argument");

    List<Rule> rules = new ArrayList<>();
    rules.add(new Rule(symbols.getStart(), List.of(expression)));
    rules.add(new Rule(expression, List.of(lex.identifierTerminal)));
    rules.add(new Rule(expression, List.of(lex.numberTerminal)));
    rules.add(new Rule(expression, List.of(expression, lex.periodTerminal, lex.identifierTerminal)));
    rules.add(new Rule(expression, List.of(expression, lex.periodTerminal, functionInvocation)));
    rules.add(new Rule(functionInvocation, List.of(lex.identifierTerminal, lex.lParenTerminal, lex.rParenTerminal)));
    rules.add(new Rule(functionInvocation, List.of(lex.identifierTerminal, lex.lParenTerminal, argList, lex.rParenTerminal)));
    rules.add(new Rule(argList, List.of(argument)));
    rules.add(new Rule(argList, List.of(argList, lex.commaTerminal, argument)));
    rules.add(new Rule(argument, List.of(expression)));

    grammar = new Grammar(symbols, rules);
    LRParser parser = LRItem.makeItemGraph(grammar);
    System.out.println(parser);
    table = parser.getTable(grammar);
  }

  public void parse(Reader r) throws IOException {
    List<Token> tokens = lex.getTokens(r);
    tokens.add(new Token(symbols.getEof(), "eof"));
    Object o = Parser.parseTokens(table, tokens.iterator(), grammar.getAugmentedStartRule().getSource());
    System.out.println(o);
  }

  public static void main(String[] args) throws IOException {
    Language lang = new Language();
    System.out.println(lang.table);
    Reader r = new CharArrayReader("foo.bar(xx.yy,zz)".toCharArray());
    // I suspect that this is not an LR(0) language, since the
    // parse table doesn't have rule for reducing an argument expression when
    // the next is a comma.
    //lang.parse(r);
  }
}
