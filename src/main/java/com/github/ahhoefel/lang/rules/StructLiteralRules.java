package com.github.ahhoefel.lang.rules;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.LanguageBuilder;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.LanguageComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructLiteralRules implements LanguageComponent {

  // Provides
  private Symbol structLiteral;

  // Requires
  private Symbol expression;
  private Symbol type;

  // Internal
  private Symbol structLiteralArgs;
  private Symbol structLiteralArg;

  private class Pair {
    public String identifier;
    public Expression expression;

    public Pair(String identifier, Expression expression) {
      this.identifier = identifier;
      this.expression = expression;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void provideRules(LanguageBuilder lang) {
    Rule.Builder rules = lang.getRules();

    // Do we need the new keyword here? Why is grammar not LR(1)?
    rules.add(structLiteral, lang.getLexicon().newKeyword, type, lang.getLexicon().lBrace, structLiteralArgs,
        lang.getLexicon().rBrace).setAction(e -> {
          StructLiteralExpression expr = new StructLiteralExpression((Type) e[1]);
          for (Pair p : (List<Pair>) e[3]) {
            expr.add(p.identifier, p.expression);
          }
          return expr;
        });
    rules.add(structLiteralArgs).setAction(e -> new ArrayList<Pair>());
    rules.add(structLiteralArgs, structLiteralArgs, structLiteralArg).setAction(e -> {
      List<Pair> args = (List<Pair>) e[0];
      args.add((Pair) e[1]);
      return args;
    });
    rules.add(structLiteralArg, lang.getLexicon().identifier, lang.getLexicon().colon, expression,
        lang.getLexicon().comma).setAction(e -> new Pair(((Token) e[0]).getValue(), (Expression) e[2]));
  }

  @Override
  public List<Symbol> provides(SymbolTable nonTerminals) {
    structLiteral = nonTerminals.newSymbol("structLiteral");
    structLiteralArgs = nonTerminals.newSymbol("structLiteralArgs");
    structLiteralArg = nonTerminals.newSymbol("structLiteralArg");
    return List.of(structLiteral);
  }

  @Override
  public List<String> requires() {
    return List.of("expression", "type");
  }

  @Override
  public void acceptExternalSymbols(Map<String, Symbol> external) {
    this.expression = external.get("expression");
    this.type = external.get("type");
  }
}
