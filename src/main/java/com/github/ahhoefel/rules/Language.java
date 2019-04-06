package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;
import com.github.ahhoefel.ir.Expression;
import com.github.ahhoefel.ir.LiteralExpression;
import com.github.ahhoefel.ir.ProductExpression;
import com.github.ahhoefel.ir.SumExpression;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class Language {

  private Lexicon lex;
  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private Grammar grammar;
  private LRTable table;

  private Symbol expression;
  private Symbol functionInvocation;
  private Symbol argList;
  private Symbol argument;

  public Language() {
    lex = new Lexicon();
    terminals = lex.getTerminals();
    nonTerminals = new SymbolTable.NonTerminalTable();

    expression = nonTerminals.newSymbol("expression");
    functionInvocation = nonTerminals.newSymbol("functionInvocation");
    argList = nonTerminals.newSymbol("argList");
    argument = nonTerminals.newSymbol("argument");

    Rule.Builder rules = new Rule.Builder();
    rules.add(nonTerminals.getStart(), expression).setAction(e -> e[0]);
    rules.add(expression, lex.identifierTerminal);
    rules.add(expression, lex.numberTerminal).setAction(e -> new LiteralExpression(e[0]));
    rules.add(expression, expression, lex.periodTerminal, lex.identifierTerminal);
    rules.add(expression, expression, lex.periodTerminal, functionInvocation);
    rules.add(expression, expression, lex.plusTerminal, expression)
        .setAction(e -> new SumExpression((Expression) e[0], (Expression) e[2]));
    rules.add(expression, expression, lex.timesTerminal, expression)
        .setAction(e -> new ProductExpression((Expression) e[0], (Expression) e[2]));
    rules.add(functionInvocation, lex.identifierTerminal, lex.lParenTerminal, lex.rParenTerminal);
    rules.add(functionInvocation, lex.identifierTerminal, lex.lParenTerminal, argList, lex.rParenTerminal);
    rules.add(argList, argument);
    rules.add(argList, argList, lex.commaTerminal, argument);
    rules.add(argument, expression);

    grammar = new Grammar(terminals, nonTerminals, rules.build());
    table = LRParser.getSLRTable(grammar);
  }

  public Expression parse(Reader r) throws IOException {
    List<Token> tokens = lex.getTokens(r);
    tokens.add(new Token(terminals.getEof(), "eof"));
    Expression t = (Expression) Parser.parseTokens(table, tokens.iterator(), grammar.getAugmentedStartRule().getSource());
    return t;
  }

  public static void main(String[] args) throws IOException {
    Language lang = new Language();
    Reader r = new CharArrayReader("foo.bar(xx.yy,zz)".toCharArray());
    System.out.println(lang.parse(r));
  }
}
