package com.github.ahhoefel;

import org.junit.Test;

import java.util.List;

public class ExampleFTest {
  @Test
  public void test() {
    ExampleF e = new ExampleF();
    List<Symbol> input = List.of(e.digit, e.digit, e.digit, e.terminals.getEof());
    LRTable table = LRParser.getCannonicalLRTable(e.grammar);
    System.out.println(table);
    ParseTree out = (ParseTree) Parser.parseTerminals(table, input.iterator(), e.start);
    System.out.println(out);
  }
}
