package com.github.ahhoefel.io;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.parser.RangeTokenizer;
import com.github.ahhoefel.parser.Token;

import java.io.IOException;
import java.util.Iterator;

public class TokenIterator implements Iterator<Token> {
  private ReaderIterator iter;
  private RangeTokenizer tokenizer;
  private Target target;
  private boolean eofSent;
  private int line = 0;
  private int character = 0;
  private int position = 0;

  public TokenIterator(RangeTokenizer tokenizer, Target target) throws IOException {
    if (target == null) {
      throw new RuntimeException("Target cannot be null");
    }
    this.iter = new ReaderIterator(target);
    this.target = target;
    this.tokenizer = tokenizer;
    if (!this.iter.hasNext()) {
      this.iter.close();
    }
  }

  public TokenIterator(RangeTokenizer tokenizer, String s) {
    this.iter = new ReaderIterator(s);
    this.tokenizer = tokenizer;
    // this.target = new Target(Path.of("/fake"), ":target.ro");
    if (!this.iter.hasNext()) {
      try {
        this.iter.close();
      } catch (IOException e) {
        // ignore.
      }
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
      return new Token(tokenizer.getEof(), "eof", new CodeLocation(target, line, character, position));
    }
    Integer next = iter.next();
    if (!iter.hasNext()) {
      try {
        iter.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    Token token = tokenizer.of(next, new CodeLocation(target, line, character, position));
    character++;
    position++;
    if (token.getSymbol().equals(tokenizer.getNewLine())) {
      character = 0;
      line++;
    }
    return token;
  }
}
