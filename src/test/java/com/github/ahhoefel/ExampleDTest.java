package com.github.ahhoefel;

import com.github.ahhoefel.parser.LRParser;
import org.junit.jupiter.api.Test;

public class ExampleDTest {

    /*
     * @Test(expected = RuntimeException.class) public void testShiftReduceError() {
     * ExampleD example = new ExampleD(); LRParser.getSLRTable(example.grammar); //
     * Shift/reduce error for SLR table. }
     */

    @Test
    public void testNoShiftReduceError() {
        ExampleD example = new ExampleD();
        LRParser.getCanonicalLRTable(example.grammar); // no shift/reduce error.
    }

    @Test
    public void test() {
        // ExampleD e = new ExampleD();
        // LRTable p = LRParser.getCanonicalLRTable(e.grammar); // no shift/reduce
        // error.

        // List<Symbol> input = List.of(e.identifier, e.equal, e.contents, e.identifier,
        // e.grammar.getTerminals().getEof());
        /*
         * ParseTree out = (ParseTree) Parser.parseTerminals(p, input.iterator(),
         * e.start); ParseTree expected = new ParseTree( e.startToLeftEqualsRight,
         * List.of( new ParseTree( e.leftToIdentifier, List.of( new ParseTree(new
         * Token(e.identifier, "id")) ) ), new ParseTree(new Token(e.equal, "=")), new
         * ParseTree( e.rightToLeft, List.of( new ParseTree( e.leftToContentsRight,
         * List.of( new ParseTree(new Token(e.contents, "*")), new ParseTree(
         * e.rightToLeft, List.of( new ParseTree( e.leftToIdentifier, List.of( new
         * ParseTree(new Token(e.identifier, "id")) ) ) ) ) ) ) ) ) ) );
         * Assert.assertEquals(out, expected);
         */
    }
}
