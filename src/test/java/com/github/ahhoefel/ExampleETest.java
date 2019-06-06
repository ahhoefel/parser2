package com.github.ahhoefel;

import com.github.ahhoefel.parser.LRParser;
import org.junit.Test;

public class ExampleETest {
  @Test
  public void testNoShiftReduceError() {
    ExampleE example = new ExampleE();
    LRParser.getCannonicalLRTable(example.grammar); // no shift/reduce error.
  }
}
