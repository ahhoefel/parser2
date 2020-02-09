package com.github.ahhoefel.io;

import com.github.ahhoefel.ast.CodeLocation;
import com.github.ahhoefel.ast.Target;
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
    this.iter = new ReaderIterator(target);
    this.target = target;
    this.tokenizer = tokenizer;
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
