package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TokenIterator<T, L> {
  private List<LabeledTaker<T, L>> takers;
  private List<LabeledTaker<T, L>> accepting;
  private Iterator<T> tokens;

  public TokenIterator(List<LabeledTaker<T, L>> takers, Iterator<T> tokens) {
    this.takers = takers;
    this.accepting = new ArrayList<>(takers);
    this.tokens = tokens;
  }

  public boolean hasNext() {
    return tokens.hasNext();
  }

  public Word<T, L> next() {
    accepting.clear();
    accepting.addAll(takers);
    for (LabeledTaker<T, L> taker : takers) {
      taker.reset();
    }
    List<T> matched = new ArrayList();
    List<T> mightMatch = new ArrayList();
    LabeledTaker<T, L> lastMatchedTaker = null;
    while (tokens.hasNext() && !accepting.isEmpty()) {
      T token = tokens.next();
      //System.out.println(token);
      mightMatch.add(token);
      boolean foundMatch = false;
      Iterator<LabeledTaker<T, L>> iter = accepting.iterator();
      while (iter.hasNext()) {
        LabeledTaker<T, L> taker = iter.next();
        taker.add(token);
        TokenTaker.State s = taker.getState();
        //System.out.println(taker.getLabel() + " " + s);
        if (s == TokenTaker.State.MATCH && !foundMatch) {
          foundMatch = true;
          lastMatchedTaker = taker;
          matched.addAll(mightMatch);
          mightMatch.clear();
        } else if (s == TokenTaker.State.ERROR) {
          iter.remove();
        }
      }
    }
    tokens = new PrependIterator(mightMatch.iterator(), tokens);
    if (lastMatchedTaker == null) {
      return null;
    }
    return new Word(matched, lastMatchedTaker.getLabel());
  }

  private class PrependIterator<T> implements Iterator<T> {
    private Iterator<T> first;
    private Iterator<T> second;

    public PrependIterator(Iterator<T> first, Iterator<T> second) {
      this.first = first;
      this.second = second;
    }

    public boolean hasNext() {
      return this.first.hasNext() || this.second.hasNext();
    }

    public T next() {
      return this.first.hasNext() ? this.first.next() : this.second.next();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }


  }

  public class Word<T, L> {
    public List<T> tokens;
    public L label;

    public Word(List<T> tokens, L label) {

      this.tokens = tokens;
      this.label = label;
    }

    public String toString() {
      String out = label.toString() + ": ";
      for (T t : tokens) {
        out += t.toString();
      }
      return out;
    }
  }

  public static void main(String[] args) {
    List<Character> chars = new ArrayList();
    for (char c : "foobarfoo".toCharArray()) {
      chars.add(c);
    }
    TokenIterator<Character, String> iter =
        new TokenIterator<>(Arrays.asList(
            new LabeledTakerAdapter<>("fooLabel", WordTaker.newStringTaker("foo")),
            new LabeledTakerAdapter<>("barLabel", WordTaker.newStringTaker("bar"))
        ), chars.iterator()
        );
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }

    /*
    WordTaker<Character> taker = WordTaker.newStringTaker("foo");
    for (int i =0; i < 4; i++) {
      taker.add("foot".charAt(i));
      System.out.println(taker.getState());
    }
    */

    /*
    LabeledTakerAdapter<Character, String> taker = new LabeledTakerAdapter<>("fooLabel", WordTaker.newStringTaker("foo"));
    for (int i =0; i < 4; i++) {
      taker.add("foot".charAt(i));
      System.out.println(taker.getState());
    }
    */

    /*
    Iterator<Character> chars = Arrays.<Character>asList("foobarfoo".toCharArray()).iterator();
    while (chars.hasNext()) {
      System.out.println(iter.next());
    }
    */
  }
}

