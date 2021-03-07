package com.github.ahhoefel.io;

import com.github.ahhoefel.ast.Target;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Iterator;

public class ReaderIterator implements Iterator<Integer> {
  private Reader r;
  private int pos;
  private Integer next;
  private boolean done;

  public ReaderIterator(Target target) throws IOException {
    this(Files.newBufferedReader(target.getFilePath()));
  }

  public ReaderIterator(String s) {
    this(new StringReader(s));
  }

  public ReaderIterator(Reader r) {
    this.r = r;
    this.done = false;
  }

  @Override
  public boolean hasNext() {
    if (done) {
      return false;
    }
    populateNext();
    return next != null;
  }

  @Override
  public Integer next() {
    populateNext();
    Integer tmp = next;
    next = null;
    return tmp;
  }

  private void populateNext() {
    if (next != null) {
      return;
    }
    try {
      next = r.read();
      pos++;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (next == -1) {
      next = null;
      done = true;
    }
  }

  public void close() throws IOException {
    r.close();
  }

  public int position() {
    return pos;
  }
}
