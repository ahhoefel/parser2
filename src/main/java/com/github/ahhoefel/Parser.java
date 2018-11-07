package com.github.ahhoefel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

  public static <T> ParseTree parse(LRTable<T> table, Iterator<T> iter, NonTerminalSymbol start) {
    Stack<SymbolState> stack = new Stack<>();
    Stack<ParseTree> result = new Stack<>();
    T next = iter.next();
    TerminalSymbol nextSymbol = new TerminalSymbol(next);
    SymbolState symbolState = new SymbolState(start, 0);
    while (true) {
      LRTable.State<T> state = table.state.get(symbolState.stateIndex);
      System.out.println(String.format("next: %s, state: %d, stack: %s", next.toString(), symbolState.stateIndex, stack.toString()));
      if (state.reduce.containsKey(nextSymbol)) {
        Rule rule = state.reduce.get(nextSymbol);
        List<ParseTree> children = new ArrayList<>();
        for (int i = 0; i < rule.getSymbols().size(); i++) {
          stack.pop();
          children.add(result.pop());
        }
        stack.push(new SymbolState(rule.getSource(), table.state.get(stack.isEmpty() ? 0 : stack.peek().stateIndex).state.get(rule.getSource())));
        result.push(new ParseTree(rule, children));
      } else if (state.shift.containsKey(nextSymbol)) {
        if (state.shift.get(nextSymbol) == -1) {
          break;
        }
        stack.push(new SymbolState(nextSymbol, state.shift.get(nextSymbol)));
        result.push(new ParseTree(next));
        if (iter.hasNext()) {
          next = iter.next();
          nextSymbol = new TerminalSymbol(next);
        }
      } else {
        throw new RuntimeException("Parse error");
      }
      symbolState = stack.peek();
    }
    return result.pop();
  }

}
