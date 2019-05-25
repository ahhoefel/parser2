package com.github.ahhoefel.rules;

import org.junit.Test;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

public class LanguageTest {

  @Test
  public void test() throws IOException {
    Language rae = new Language();
    Reader r = new CharArrayReader("foo.bar(xx.yy,zz)".toCharArray());
    rae.parse(r);
  }

  @Test
  public void test2() throws IOException {
    Language rae = new Language();
    //Reader r = new CharArrayReader("10+2*3".toCharArray());
    //Assert.assertEquals(16, ((ExpressionStatement) rae.parse(r).get(0)).getExpression().eval(null));

    // Check operator precendence.
    //r = new CharArrayReader("2*3+10".toCharArray());
    //Assert.assertEquals(16, ((ExpressionStatement) rae.parse(r).get(0)).getExpression().eval(null));
  }

}
