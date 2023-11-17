package com.github.ahhoefel;

import com.github.ahhoefel.parser.LRParser;
import org.junit.jupiter.api.Test;


public class ExampleETest {
  @Test
  public void testNoShiftReduceError() {
    ExampleE example = new ExampleE();
    LRParser.getCanonicalLRTable(example.grammar); // no shift/reduce error.
  }
}
