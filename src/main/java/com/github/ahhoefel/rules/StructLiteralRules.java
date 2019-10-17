package com.github.ahhoefel.rules;

import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.parser.Rule;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;
import com.github.ahhoefel.parser.Token;

import java.util.ArrayList;
import java.util.List;

public class StructLiteralRules {

  Symbol structLiteral;
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

  public StructLiteralRules(SymbolTable nonTerminals) {
    structLiteral = nonTerminals.newSymbol("structLiteral");
    structLiteralArgs = nonTerminals.newSymbol("structLiteralArgs");
    structLiteralArg = nonTerminals.newSymbol("structLiteralArg");
  }

  public void provideRules(Rule.Builder rules, Language lang) {
    // Do we need the new keyword here? Why is grammar not LR(1)?
    rules.add(structLiteral, lang.lex.newKeyword, lang.type.type, lang.lex.lBrace, structLiteralArgs, lang.lex.rBrace)
        .setAction(e -> {
          StructLiteralExpression expr = new StructLiteralExpression((Type) e[1]);
          for (Pair p : (List<Pair>) e[3]) {
            expr.add(p.identifier, p.expression);
          }
          return expr;
        });
    rules.add(structLiteralArgs)
        .setAction(e -> new ArrayList<Pair>());
    rules.add(structLiteralArgs, structLiteralArgs, structLiteralArg)
        .setAction(e -> {
          List<Pair> args = (List<Pair>) e[0];
          args.add((Pair) e[1]);
          return args;
        });
    rules.add(structLiteralArg, lang.lex.identifier, lang.lex.colon, lang.expression.expression, lang.lex.comma)
        .setAction(e -> new Pair(((Token) e[0]).getValue(), (Expression) e[2]));
  }
}
