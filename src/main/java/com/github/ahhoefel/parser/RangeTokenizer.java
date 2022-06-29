package com.github.ahhoefel.parser;

import com.github.ahhoefel.lang.ast.CodeLocation;

public class RangeTokenizer {

  private Symbol[] charTerminalMap;
  private Symbol unknown;
  private Symbol eof;
  private Symbol newLine;

  public RangeTokenizer(CharacterSet chars) {
    this.charTerminalMap = new Symbol[256];
    this.unknown = chars.unknown;
    this.eof = chars.eof;
    this.newLine = chars.newline;
    for (int i = 0; i < 256; i++) {
      charTerminalMap[i] = chars.unknown;
    }

    for (int i = 48; i < 58; i++) {
      charTerminalMap[i] = chars.number;
    }
    for (int i = 65; i < 91; i++) {
      charTerminalMap[i] = chars.letter;
    }
    for (int i = 97; i < 123; i++) {
      charTerminalMap[i] = chars.letter;
    }
    charTerminalMap[91] = chars.lbracket;
    charTerminalMap[93] = chars.rbracket;
    charTerminalMap[95] = chars.underscore;
    charTerminalMap[123] = chars.lbrace;
    charTerminalMap[125] = chars.rbrace;
    charTerminalMap[32] = chars.space;
    charTerminalMap[9] = chars.tab;
    charTerminalMap[10] = chars.newline;
    charTerminalMap[40] = chars.lparen;
    charTerminalMap[41] = chars.rparen;
    charTerminalMap[42] = chars.times;
    charTerminalMap[43] = chars.plus;
    charTerminalMap[44] = chars.comma;
    charTerminalMap[45] = chars.hypen;
    charTerminalMap[46] = chars.period;
    charTerminalMap[47] = chars.forwardSlash;
    charTerminalMap[58] = chars.colon;
    charTerminalMap[60] = chars.lessThan;
    charTerminalMap[61] = chars.eq;
    charTerminalMap[62] = chars.greaterThan;
    charTerminalMap[33] = chars.bang;
    charTerminalMap[38] = chars.ampersand;
    charTerminalMap[124] = chars.pipe;
  }

  public Token of(int c, CodeLocation location) {
    if (c < 0) {
      return new Token(eof, "eof", location);
    }
    if (c >= charTerminalMap.length) {
      return new Token(unknown, Character.toString((char) c), location);
    }
    return new Token(charTerminalMap[c], Character.toString((char) c), location);
  }

  public Symbol getEof() {
    return eof;
  }

  public Symbol getNewLine() {
    return newLine;
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
    System.out.println(String.format("%c %d", '\t', (int) '\t'));
    System.out.println(String.format("%c %d", '[', (int) '['));
    System.out.println(String.format("%c %d", ']', (int) ']'));
    System.out.println(String.format("%c %d", (char) 92, (int) ']'));
  }
}
