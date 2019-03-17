package com.github.ahhoefel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public interface Tokenizer {
  Token of(int c);

  static TokenIterator fromFile(String filename, Tokenizer t, TerminalSymbol eof) throws Exception {
    FileReader fr = new FileReader(filename);
    BufferedReader br = new BufferedReader(fr);
    return new TokenIterator(t, br, eof);
  }

  class TokenIterator implements Iterator<Token> {
    private ReaderIterator iter;
    private Tokenizer tokenizer;
    private boolean eofSent;
    private Symbol eof;

    public TokenIterator(Tokenizer tokenizer, Reader reader, Symbol eof) throws IOException {
      this.iter = new ReaderIterator(reader);
      this.tokenizer = tokenizer;
      this.eof = eof;
      if (!this.iter.hasNext()) {
        this.iter.close();
      }
    }

    @Override
    public boolean hasNext() {
      return iter.hasNext() || !eofSent;
    }

    @Override
    public Token next() throws RuntimeException {
      if (!iter.hasNext()) {
        if (eofSent) {
          throw new RuntimeException("No next element.");
        }
        eofSent = true;
        return new Token(eof, "eof");
      }
      Integer next = iter.next();
      if (!iter.hasNext()) {
        try {
          iter.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      return tokenizer.of(next);
    }
  }


  class ReaderIterator implements Iterator<Integer> {
    private Reader r;
    private Integer next;
    private boolean done;

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
  }
}
