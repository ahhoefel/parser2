package com.github.ahhoefel.rules;

import org.junit.Assert;
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
    Reader r = new CharArrayReader("10+2*3".toCharArray());
    Assert.assertEquals(16, rae.parse(r).eval());

    // Failed. Plus has higher precedence than times.
    // r = new CharArrayReader("2*3+10".toCharArray());
    // Assert.assertEquals(16, rae.parse(r).eval());

  }

}
