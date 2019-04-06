package com.github.ahhoefel.rules;

import com.github.ahhoefel.Token;
import org.junit.Assert;
import org.junit.Test;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class LexiconTest {
  @Test
  public void test() throws IOException {
    Lexicon lex = new Lexicon();
    Reader r = new CharArrayReader("foc 123 d12".toCharArray());
    List<Token> list = lex.getTokens(r);
    Assert.assertEquals(list.get(0), new Token(lex.identifierTerminal, "foc"));
    Assert.assertEquals(list.get(1), new Token(lex.whitespaceTerminal, " "));
    Assert.assertEquals(list.get(2), new Token(lex.numberTerminal, "123"));
    Assert.assertEquals(list.get(3), new Token(lex.whitespaceTerminal, " "));
    Assert.assertEquals(list.get(4), new Token(lex.identifierTerminal, "d12"));
    Assert.assertEquals(list.size(), 5);
  }
}
