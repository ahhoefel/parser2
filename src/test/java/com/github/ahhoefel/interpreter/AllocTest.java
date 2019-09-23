package com.github.ahhoefel.interpreter;

import org.junit.Assert;
import org.junit.Test;

public class AllocTest {

  @Test
  public void copyFromTest() {
    Alloc a = new Alloc(128);
    Alloc b = new Alloc(64);
    a.setWord(0, 0xFFFF_FFFF_FFFF_FFFFL);
    a.setWord(1, 0xFFFF_FFFF_FFFF_FFFFL);
    a.copyFrom(b, 0, 16, 64);
    Assert.assertEquals(0x0000_0000_0000_FFFFL, a.getWord(0));
    Assert.assertEquals(0xFFFF_FFFF_FFFF_0000L, a.getWord(1));
    a.setWord(0, 0xFFFF_FFFF_FFFF_FFFFL);
    a.setWord(1, 0xFFFF_FFFF_FFFF_FFFFL);
    b.setWord(0, 0x0FFF_00FF_FFFF_FFF0L);
    a.copyFrom(b, 0, 20, 64);
    Assert.assertEquals(0x0FFF_FFFF_FF0F_FFFFL, a.getWord(0));
    Assert.assertEquals(0xFFFF_FFFF_FFF0_FFF0L, a.getWord(1));

  }
}
