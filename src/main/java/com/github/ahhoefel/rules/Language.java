package com.github.ahhoefel.rules;

import com.github.ahhoefel.*;
import com.github.ahhoefel.ast.Declaration;
import com.github.ahhoefel.ast.RaeFile;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class Language {

  private Lexicon lex;
  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private Grammar grammar;
  private LRTable table;

  private FunctionRules function;
  private ExpressionRules expression;
  private StatementRules statement;
  private TypeRules type;
  private ImportRules imp0rt;

  private Symbol declarationList;
  private Symbol declaration;

  public Language() {
    lex = new Lexicon();
    terminals = lex.getTerminals();
    nonTerminals = new SymbolTable.NonTerminalTable();
    ShiftReduceResolver resolver = new ShiftReduceResolver();

    declarationList = nonTerminals.newSymbol("declarationList");
    declaration = nonTerminals.newSymbol("declaration");

    Rule.Builder rules = new Rule.Builder();
    type = new TypeRules(rules, nonTerminals, lex);
    expression = new ExpressionRules(rules, nonTerminals, lex, resolver);
    statement = new StatementRules(rules, lex, nonTerminals, expression.expression, type.type);
    function = new FunctionRules(rules, lex, nonTerminals, statement.statementList, type.type);
    imp0rt = new ImportRules(rules, lex, nonTerminals);

    rules.add(nonTerminals.getStart(), declarationList)
        .setAction(e -> e[0]);

    rules.add(declarationList, declarationList, declaration).setAction(e -> ((Declaration) e[1]).addToFile((RaeFile) e[0]));
    rules.add(declarationList).setAction(e -> new RaeFile());
    rules.add(declaration, function.declaration).setAction(e -> e[0]);
    rules.add(declaration, imp0rt.imp0rt).setAction(e -> e[0]);

    grammar = new Grammar(terminals, nonTerminals, rules.build());
    table = LRParser.getCannonicalLRTable(grammar, resolver);
  }

  public RaeFile parse(Reader r) throws IOException {
    List<Token> tokens = lex.getTokens(r);
    tokens.add(new Token(terminals.getEof(), "eof"));
    return (RaeFile) Parser.parseTokens(table, tokens.iterator(), grammar.getAugmentedStartRule().getSource());
  }

  public static void main(String[] args) throws IOException {
    Language lang = new Language();
    //Reader r = new CharArrayReader("foo.bar(xx.yy,zz)".toCharArray());
    //Interpreter.run(lang.parse(r));
    System.out.println(lang.table);
  }
}
