package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.Declaration;
import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.RaeFile;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.parser.*;

import java.io.IOException;
import java.util.List;

public class Language {

  Lexicon lex;
  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private Grammar grammar;
  private LRTable table;

  private FunctionRules function;
  ExpressionRules expression;
  StatementRules statement;
  TypeRules type;
  private TypeDeclarationRules typeDeclaration;
  private ImportRules imp0rt;

  ShiftReduceResolver resolver;
  private Symbol declarationList;
  private Symbol declaration;

  public Language() {
    lex = new Lexicon();
    terminals = lex.getTerminals();
    nonTerminals = new SymbolTable.NonTerminalTable();
    resolver = new ShiftReduceResolver();

    declarationList = nonTerminals.newSymbol("declarationList");
    declaration = nonTerminals.newSymbol("declaration");

    Rule.Builder rules = new Rule.Builder();
    type = new TypeRules(nonTerminals);
    expression = new ExpressionRules(nonTerminals);
    statement = new StatementRules(nonTerminals);
    function = new FunctionRules(nonTerminals);
    typeDeclaration = new TypeDeclarationRules(nonTerminals);
    imp0rt = new ImportRules(rules, lex, nonTerminals);

    rules.add(nonTerminals.getStart(), declarationList).setAction(e -> e[1]);

    rules.add(declarationList, declarationList, declaration)
        .setAction(e -> ((Declaration) e[1]).addToFile((RaeFile) e[2]));
    rules.add(declarationList).setAction(e -> e[0]);
    rules.add(declaration, function.declaration).setAction(e -> e[0]);
    rules.add(declaration, imp0rt.imp0rt).setAction(e -> e[0]);
    rules.add(declaration, typeDeclaration.typeDeclaration).setAction(e -> e[0]);

    type.provideRules(rules, this);
    expression.provideRules(rules, this);
    statement.provideRules(rules, this);
    function.provideRules(rules, this);
    typeDeclaration.provideRules(rules, this);
    imp0rt.provideRules(rules, this);

    grammar = new Grammar(terminals, nonTerminals, rules.build());
    table = LRParser.getCanonicalLRTable(grammar, resolver);
  }

  public RaeFile parse(Target target, ErrorLog log) throws IOException {
    RaeFile file = new RaeFile();
    List<Token> tokens = lex.parse(target, log);
    tokens.add(new Token(terminals.getEof(), "eof", null));
    // System.out.println("Parsing...");
    Parser.parseTokens(table, tokens.iterator(), grammar.getAugmentedStartRule().getSource(), file, log);
    return file;
  }
}
