package com.github.ahhoefel;

import com.github.ahhoefel.parser.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExampleA {
  public static void main(String[] args) {
    SymbolTable.TerminalTable terminals = new SymbolTable.TerminalTable();
    Symbol times = terminals.newSymbol("*");
    Symbol plus = terminals.newSymbol("+");
    Symbol id = terminals.newSymbol("id");
    Symbol num = terminals.newSymbol("num");
    Symbol eof = terminals.getEof();

    SymbolTable.NonTerminalTable nonTerminals = new SymbolTable.NonTerminalTable();
    Symbol value = nonTerminals.newSymbol("value");
    Symbol products = nonTerminals.newSymbol("products");
    Symbol sums = nonTerminals.getStart();

    Rule r1 = new Rule(sums, List.of(sums, plus, products));
    Rule r2 = new Rule(sums, List.of(products));
    Rule r3 = new Rule(products, List.of(products, times, value));
    Rule r4 = new Rule(products, List.of(value));
    Rule r5 = new Rule(value, List.of(num));
    Rule r6 = new Rule(value, List.of(id));
    List<Rule> ruleList = List.of(r1, r2, r3, r4, r5, r6);
    Grammar grammar = new Grammar(terminals, nonTerminals, ruleList);

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

    List<Symbol> input = Arrays.asList(id, times, id, plus, num, eof);
    System.out.println(table);
    //Object tree = Parser.parseTerminals(table, input.iterator(), nonTerminals.getAugmentedStart());
    //System.out.println(tree);

    table = LRParser.getSLRTable(grammar);
    System.out.println(table);
    //tree = Parser.parseTerminals(table, input.iterator(), nonTerminals.getAugmentedStart());
    //System.out.println(tree);
  }
}
