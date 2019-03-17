package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.Iterator;

public class Parser<T> {

  private static class SymbolState {
    public Symbol symbol;
    public int stateIndex;

    public SymbolState(Symbol symbol, int stateIndex) {
      this.symbol = symbol;
      this.stateIndex = stateIndex;
    }

    public String toString() {
      return String.format("(%s, %d)", symbol.toString(), stateIndex);
    }
  }

  private static class Stack<E> extends ArrayList<E> {
    public void push(E e) {
      this.add(e);
    }

    public E pop() {
      return this.remove(this.size() - 1);
    }

    public E peek() {
      return this.get(this.size() - 1);
    }
  }

  private static class TerminalSymbolIterator implements Iterator<Token> {

    private Iterator<Symbol> iter;

    public TerminalSymbolIterator(Iterator<Symbol> iter) {
      this.iter = iter;
    }


    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public Token next() {
      Symbol next = iter.next();
      if (next == null) {
        return null;
      }
      return new Token(next, next.toString());
    }
  }

  public static Object parseTerminals(LRTable table, Iterator<Symbol> iter, Symbol start) {
    return parseTokens(table, new TerminalSymbolIterator(iter), start);
  }

  public static Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start) {
    Stack<SymbolState> stack = new Stack<>();
    Stack<Object> result = new Stack<>();
    Token nextToken = iter.next();
    Symbol nextSymbol = nextToken.getSymbol();
    SymbolState symbolState = new SymbolState(start, 0);
    while (true) {
      LRTable.State state = table.state.get(symbolState.stateIndex);
      System.out.println(String.format("next: %s, state: %d, stack: %s", nextToken, symbolState.stateIndex, stack));
      if (state.shift.containsKey(nextSymbol)) {
        if (state.shift.get(nextSymbol) == -1) {
          break;
        }
        stack.push(new SymbolState(nextSymbol, state.shift.get(nextSymbol)));
        result.push(nextToken);
        if (iter.hasNext()) {
          nextToken = iter.next();
          nextSymbol = nextToken.getSymbol();
        }
      } else if (state.reduce.containsKey(nextSymbol)) {
        Rule rule = state.reduce.get(nextSymbol);
        Object[] children = new Object[rule.getSymbols().size()];
        for (int i = rule.getSymbols().size() - 1; i >= 0; i--) {
          stack.pop();
          children[i] = result.pop();
        }
        stack.push(new SymbolState(rule.getSource(), table.state.get(stack.isEmpty() ? 0 : stack.peek().stateIndex).state.get(rule.getSource())));
        result.push(rule.getAction().apply(children));
      } else {
        throw new RuntimeException("parseTerminals error");
      }
      symbolState = stack.peek();
    }
    return result.pop();
  }
}
