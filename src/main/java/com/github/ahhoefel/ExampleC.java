package com.github.ahhoefel;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ExampleC {

  SymbolFactory.Builder sb;
  SymbolFactory sf;
  CharRange ch;
  NonTerminalSymbol start;
  NonTerminalSymbol statement;
  NonTerminalSymbol number;
  NonTerminalSymbol identifier;
  NonTerminalSymbol identifierSuffix;
  NonTerminalSymbol whitespace;
  Grammar grammar;

  Rule statements;
  Rule noStatements;


  public ExampleC() {
    sb = SymbolFactory.newBuilder();
    ch = new CharRange(sb);
    statement = sb.newNonTerminal("statement");
    number = sb.newNonTerminal("number");
    identifier = sb.newNonTerminal("identifier");
    identifierSuffix = sb.newNonTerminal("identifier_suffix");
    whitespace = sb.newNonTerminal("whitespace");

    sf = sb.build();
    start = sf.getStart();
    List<Rule> rules = new ArrayList<>();
    statements = new Rule(start, List.<Symbol>of(statement, start));
    noStatements = new Rule(start, List.<Symbol>of());
    rules.add(statements);
    rules.add(noStatements);

    rules.add(new Rule(statement, List.<Symbol>of(number)));
    rules.add(new Rule(statement, List.<Symbol>of(identifier)));
    rules.add(new Rule(statement, List.<Symbol>of(whitespace)));

    rules.add(new Rule(whitespace, List.<Symbol>of(ch.space, whitespace)));
    rules.add(new Rule(whitespace, List.<Symbol>of(ch.space)));
    rules.add(new Rule(number, List.<Symbol>of(ch.number)));
    rules.add(new Rule(number, List.<Symbol>of(number, ch.number)));

    rules.add(new Rule(identifier, List.<Symbol>of(ch.letter, identifierSuffix)));
    rules.add(new Rule(identifierSuffix, List.<Symbol>of(ch.letter, identifierSuffix)));
    rules.add(new Rule(identifierSuffix, List.<Symbol>of(ch.number, identifierSuffix)));
    rules.add(new Rule(identifierSuffix, List.<Symbol>of()));

    grammar = new Grammar(sf, rules);
  }

  public static void main(String[] args) throws IOException {
    ExampleC c = new ExampleC();
    LRParser parser = LRItem.makeItemGraph(c.grammar);
    Reader r = new CharArrayReader("foc 123 d12".toCharArray());

    Tokenizer.TokenIterator tokens = new Tokenizer.TokenIterator(new RangeTokenizer(c.ch, c.sf.getEof()), r, c.sf.getEof());

    LRTable table = parser.getTable(c.grammar);
    System.out.println(table);
    ParseTree tree = Parser.parseTokens(parser.getTable(c.grammar), tokens, c.grammar.getAugmentedStartRule().getSource());
    System.out.println(tree);

    for (String out : c.toTokens(tree)) {
      System.out.println(out);
    }
  }


  public List<String> toTokens(ParseTree tree) {
    List<String> tokens = new ArrayList<>();
    toTokensRecursive(tree, tokens);
    return tokens;
  }

  public void toTokensRecursive(ParseTree tree, List<String> tokens) {
    if (tree.getRule() == statements) {
      tokens.add(tree.getChildren().get(0).getRule().getSymbols().get(0) + ": " + getStatementToken(tree.getChildren().get(0)));
      toTokensRecursive(tree.getChildren().get(1), tokens);
    }
  }

  public String getStatementToken(ParseTree statement) {
    if (statement.getChildren() == null) {
      return statement.getToken().getValue();
    }
    StringBuilder out = new StringBuilder();
    for (ParseTree child : statement.getChildren()) {
      out.append(getStatementToken(child));
    }
    return out.toString();
  }
}
