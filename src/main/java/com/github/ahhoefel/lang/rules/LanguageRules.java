package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.rules.lex.Lexicon;
import com.github.ahhoefel.parser.*;
import com.github.ahhoefel.parser.LayeredParser.Layer;

import java.util.Iterator;

public class LanguageRules {

  public static LayeredParser<File> getParser() {
    // TerminalLayeredParser lexer = new TerminalLayeredParser(new CharacterSet());
    Lexicon lexicon = new Lexicon();
    @SuppressWarnings("rawtypes")
    Layer<Iterator, File> layer = new Layer<>("LanguageLayer", File.class, lexicon, "declarationList",
        new DeclarationRules(), new ExpressionRules(),
        new StatementRules(),
        new FunctionRules(), new TypeRules(), new TypeDeclarationRules(), new ImportRules(), new StructLiteralRules());
    return layer;
  }
}