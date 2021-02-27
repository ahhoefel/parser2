package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.ErrorLog;
import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.parser.*;

import java.io.IOException;
import java.util.List;

public class Language {

  LanguageBuilder lang;
  private SymbolTable.TerminalTable terminals;
  private Grammar grammar;
  private LRTable table;

  public Language() {

    lang = LanguageBuilder.build("declarationList", new DeclarationRules(), new ExpressionRules(), new StatementRules(),
        new FunctionRules(), new TypeRules(), new TypeDeclarationRules(), new ImportRules(), new StructLiteralRules());

    grammar = new Grammar(lang.getLexicon().getTerminals(), lang.getNonTerminals(), lang.getRules().build());
    table = LRParser.getCanonicalLRTable(grammar, lang.getResolver());
    terminals = lang.getLexicon().getTerminals();
  }

  public File parse(Target target, ErrorLog log) throws IOException {
    File file = new File();
    List<Token> tokens = lang.getLexicon().parse(target, log);
    tokens.add(new Token(terminals.getEof(), "eof", null));
    // System.out.println("Parsing...");
    Parser.parseTokens(table, tokens.iterator(), grammar.getAugmentedStartRule().getSource(), file, log);
    return file;
  }
}
