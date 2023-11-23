package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.parser.*;

import java.io.IOException;

public class LanguageRules {

  LanguageBuilder lang;
  private Grammar grammar;
  private LRTable table;

  public static LanguageBuilder getLanguage() {
    return LanguageBuilder.build("declarationList", new DeclarationRules(), new ExpressionRules(), new StatementRules(),
        new FunctionRules(), new TypeRules(), new TypeDeclarationRules(), new ImportRules(), new StructLiteralRules());
  }

  public LanguageRules() {
    lang = getLanguage();
    grammar = new Grammar(lang.getLexicon().getTerminals(), lang.getNonTerminals(), lang.getRules().build());
    table = LRParser.getCanonicalLRTable(grammar, lang.getResolver());
  }

  public File parse(Target target, ErrorLog log) throws IOException {
    if (target == null) {
      throw new RuntimeException("Target should not be null");
    }
    LocateableList<Token> tokens = lang.getLexicon().parse(target, log);
    if (lang == null) {
      throw new RuntimeException("a");
    }
    if (tokens == null) {
      throw new RuntimeException(log.toString());
    }
    tokens.add(new Token(lang.getLexicon().getTerminals().getEof(), "eof", null));
    System.out.println("Parsing target: " + target);
    File f = (File) Parser.parseTokens(table, tokens.getList().iterator(), grammar.getAugmentedStartRule().getSource(),
        log);
    if (f != null) {
      f.setTarget(target);
    }
    return f;
  }
}
