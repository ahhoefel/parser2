package com.github.ahhoefel;

import org.junit.Test;

public class ExampleETest {
  @Test
  public void testNoShiftReduceError() {
    ExampleE example = new ExampleE();
    LRParser.getCannonicalLRTable(example.grammar); // no shift/reduce error.
  }
}
