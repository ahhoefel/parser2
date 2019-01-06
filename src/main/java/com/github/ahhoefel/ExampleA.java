package com.github.ahhoefel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExampleA {
  public static void main(String[] args) {
    SymbolFactory.Builder sb = SymbolFactory.newBuilder();
    NonTerminalSymbol value = sb.newNonTerminal("value");
    NonTerminalSymbol products = sb.newNonTerminal("products");
    TerminalSymbol times = sb.newTerminal("*");
    TerminalSymbol plus = sb.newTerminal("+");
    TerminalSymbol id = sb.newTerminal("id");
    TerminalSymbol num = sb.newTerminal("num");
    SymbolFactory symbols = sb.build();
    NonTerminalSymbol sums = symbols.getStart();
    TerminalSymbol eof = symbols.getEof();
    Rule r1 = new Rule(sums, List.of(sums, plus, products));
    Rule r2 = new Rule(sums, List.of(products));
    Rule r3 = new Rule(products, List.of(products, times, value));
    Rule r4 = new Rule(products, List.of(value));
    Rule r5 = new Rule(value, List.of(num));
    Rule r6 = new Rule(value, List.of(id));
    List<Rule> ruleList = List.of(r1, r2, r3, r4, r5, r6);
    Rules rules = new Rules(symbols, ruleList);

    LRTable table = new LRTable(
        Arrays.asList(
            new LRTable.State(
                Map.of(),
                Map.of(num, 8, id, 9),
                Map.of(sums, 1, products, 4, value, 7)
            ),
            new LRTable.State(
                Map.of(),
                Map.of(plus, 2, eof, -1),
                Map.of()
            ),
            new LRTable.State(
                Map.of(),
                Map.of(num, 8, id, 9),
                Map.of(products, 3, value, 7)
            ),
            new LRTable.State(
                Map.of(plus, r1, eof, r1),
                Map.of(times, 5),
                Map.of()
            ),
            new LRTable.State(
                Map.of(plus, r2, eof, r2),
                Map.of(times, 5),
                Map.of()
            ),
            new LRTable.State(
                Map.of(),
                Map.of(num, 8, id, 9),
                Map.of(value, 6)
            ),
            new LRTable.State(
                Map.of(times, r3, plus, r3, eof, r3),
                Map.of(),
                Map.of()
            ),
            new LRTable.State(
                Map.of(times, r4, plus, r4, eof, r4),
                Map.of(),
                Map.of()
            ),
            new LRTable.State(
                Map.of(times, r5, plus, r5, eof, r5),
                Map.of(),
                Map.of()
            ),
            new LRTable.State(
                Map.of(times, r6, plus, r6, eof, r6),
                Map.of(),
                Map.of()
            )
        )
    );

    List<TerminalSymbol> input = Arrays.asList(id, times, id, plus, num, eof);
    System.out.println(table);
    ParseTree tree = Parser.parseTerminals(table, input.iterator(), symbols.getAugmentedStart());
    System.out.println(tree);

    LRParser parser = LRItem.makeItemGraph(rules);
    table = parser.getTable(rules);
    System.out.println(table);
    tree = Parser.parseTerminals(parser.getTable(rules), input.iterator(), symbols.getAugmentedStart());
    System.out.println(tree);
  }
}
