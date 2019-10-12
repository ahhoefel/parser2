package com.github.ahhoefel.io;

import com.github.ahhoefel.ast.CodeLocation;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.parser.RangeTokenizer;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;

import java.io.IOException;
import java.util.Iterator;

public class TokenIterator implements Iterator<Token> {
  private ReaderIterator iter;
  private RangeTokenizer tokenizer;
  private Target target;
  private boolean eofSent;
  private Symbol eof;

  public TokenIterator(RangeTokenizer tokenizer, Target target) throws IOException {
    this.iter = new ReaderIterator(target);
    this.target = target;
    this.tokenizer = tokenizer;
    this.eof = tokenizer.getEof();
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
      return new Token(eof, "eof", new CodeLocation(target, 0, iter.position()));
    }
    Integer next = iter.next();
    if (!iter.hasNext()) {
      try {
        iter.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return tokenizer.of(next, new CodeLocation(target, 0, iter.position()));
  }
}
