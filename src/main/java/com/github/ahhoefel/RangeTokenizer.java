package com.github.ahhoefel;

public class RangeTokenizer implements Tokenizer {

  private TerminalSymbol[] charTerminalMap;
  private TerminalSymbol unknown;
  private TerminalSymbol eof;

  public RangeTokenizer(CharRange chars, TerminalSymbol eof) {
    this.charTerminalMap = new TerminalSymbol[256];
    this.unknown = chars.unknown;
    this.eof = eof;
    for (int i = 0; i < 256; i++) {
      charTerminalMap[i] = chars.unknown;
    }

    for (int i = 48; i < 58; i++) {
      charTerminalMap[i] = chars.number;
    }
    for (int i = 65; i < 91; i++) {
      charTerminalMap[i] = chars.letter;
    }
    for (int i = 97; i < 122; i++) {
      charTerminalMap[i] = chars.letter;
    }
    charTerminalMap[40] = chars.lparen;
    charTerminalMap[41] = chars.rparen;
    charTerminalMap[45] = chars.hypen;
    charTerminalMap[32] = chars.space;
    charTerminalMap[10] = chars.newline;
  }

  public Token of(int c) {
    if (c < 0) {
      return new Token(eof, "eof");
    }
    if (c >= charTerminalMap.length) {
      return new Token(unknown, Character.toString((char) c));
    }
    return new Token(charTerminalMap[c], Character.toString((char) c));
  }

  public static void main(String[] arg) {
    System.out.println(String.format("%c %d", 'a', (int) 'a'));
    System.out.println(String.format("%c %d", 'z', (int) 'z'));
    System.out.println(String.format("%c %d", 'A', (int) 'A'));
    System.out.println(String.format("%c %d", 'Z', (int) 'Z'));
    System.out.println(String.format("%c %d", '0', (int) '0'));
    System.out.println(String.format("%c %d", '9', (int) '9'));
    System.out.println(String.format("%c %d", '-', (int) '-'));
    System.out.println(String.format("%c %d", '(', (int) '('));
    System.out.println(String.format("%c %d", ')', (int) ')'));
    System.out.println(String.format("%c %d", ' ', (int) ' '));
    System.out.println(String.format("%c %d", '\n', (int) '\n'));
  }
}
