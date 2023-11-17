package com.github.ahhoefel;

import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.LRTable;
import org.junit.jupiter.api.Test;

public class ExampleFTest {
  @Test
  public void test() {
    ExampleF e = new ExampleF();
    LRTable table = LRParser.getCanonicalLRTable(e.grammar);
    System.out.println(table);
  }
}
