package com.github.ahhoefel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExampleA {
  public static void main(String[] args) {
    NonTerminalSymbol start = new NonTerminalSymbol("start");
    NonTerminalSymbol value = new NonTerminalSymbol("value");
    NonTerminalSymbol products = new NonTerminalSymbol("products");
    NonTerminalSymbol sums = new NonTerminalSymbol("sums");
    TerminalSymbol<String> times = new TerminalSymbol<>("*");
    TerminalSymbol<String> plus = new TerminalSymbol<>("+");
    TerminalSymbol<String> id = new TerminalSymbol<>("id");
    TerminalSymbol<String> num = new TerminalSymbol<>("num");
    TerminalSymbol<String> eof = new TerminalSymbol<>("eof");
    Rule r0 = new Rule(start, List.of(sums));
    Rule r1 = new Rule(sums, List.of(sums, plus, products));
    Rule r2 = new Rule(sums, List.of(products));
    Rule r3 = new Rule(products, List.of(products, times, value));
    Rule r4 = new Rule(products, List.of(value));
    Rule r5 = new Rule(value, List.of(num));
    Rule r6 = new Rule(value, List.of(id));
    List<Rule> ruleList = List.of(r0, r1, r2, r3, r4, r5, r6);
    Rules rules = new Rules(ruleList, eof);

    LRTable table = new LRTable(
        Arrays.asList(
            new LRTable.State<>(
                Map.of(),
                Map.of(num, 8, id, 9),
                Map.of(sums, 1, products, 4, value, 7)
            ),
            new LRTable.State<>(
                Map.of(),
                Map.of(plus, 2, eof, -1),
                Map.of()
            ),
            new LRTable.State<>(
                Map.of(),
                Map.of(num, 8, id, 9),
                Map.of(products, 3, value, 7)
            ),
            new LRTable.State<>(
                Map.of(plus, r1, eof, r1),
                Map.of(times, 5),
                Map.of()
            ),
            new LRTable.State<>(
                Map.of(plus, r2, eof, r2),
                Map.of(times, 5),
                Map.of()
            ),
            new LRTable.State<>(
                Map.of(),
                Map.of(num, 8, id, 9),
                Map.of(value, 6)
            ),
            new LRTable.State<>(
                Map.of(times, r3, plus, r3, eof, r3),
                Map.of(),
                Map.of()
            ),
            new LRTable.State<>(
                Map.of(times, r4, plus, r4, eof, r4),
                Map.of(),
                Map.of()
            ),
            new LRTable.State<>(
                Map.of(times, r5, plus, r5, eof, r5),
                Map.of(),
                Map.of()
            ),
            new LRTable.State<>(
                Map.of(times, r6, plus, r6, eof, r6),
                Map.of(),
                Map.of()
            )
        )
    );

    List<String> input = Arrays.asList("id", "*", "id", "+", "num", "eof");
    System.out.println(table);
    ParseTree tree = Parser.parse(table, input.iterator(), start);
    System.out.println(tree);

    LRParser parser = LRItem.makeItemGraph(rules);
    table = parser.getTable(rules);
    System.out.println(table);
    tree = Parser.parse(parser.getTable(rules), input.iterator(), start);
    System.out.println(tree);
  }
}
