package com.github.ahhoefel;

import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.LRTable;
import org.junit.Test;

public class ExampleFTest {
  @Test
  public void test() {
    ExampleF e = new ExampleF();
    // List<Symbol> input = List.of(e.digit, e.digit, e.digit,
    // e.terminals.getEof());
    LRTable table = LRParser.getCanonicalLRTable(e.grammar);
    System.out.println(table);
    // ParseTree out = (ParseTree) Parser.parseTerminals(table, input.iterator(),
    // e.start);
    // System.out.println(out);
  }
}
