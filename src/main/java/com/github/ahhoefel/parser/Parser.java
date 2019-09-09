package com.github.ahhoefel.parser;

import com.github.ahhoefel.util.Stack;

import java.util.Iterator;
import java.util.Optional;

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

  public static <C> Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start, C context) {
    return parseTokens(table, iter, start, Optional.of(context));
  }

  public static Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start) {
    return parseTokens(table, iter, start, Optional.empty());
  }

  private static <C> Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start, Optional<C> context) {
    Stack<SymbolState> stack = new Stack<>();
    Stack<Object> result = new Stack<>();
    Token nextToken = iter.next();
    Symbol nextSymbol = nextToken.getSymbol();
    SymbolState symbolState = new SymbolState(start, 0);
    while (true) {
      LRTable.State state = table.state.get(symbolState.stateIndex);
      //System.out.println(String.format("next: %s, state: %d, stack: %s", nextToken, symbolState.stateIndex, stack));
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
        int numChildren = rule.getSymbols().size();
        int numParameters = numChildren + (context.isPresent() ? 1 : 0);
        Object[] children = new Object[numParameters];
        for (int i = numChildren - 1; i >= 0; i--) {
          stack.pop();
          children[i] = result.pop();
        }
        if (context.isPresent()) {
          children[numParameters - 1] = context.get();
        }
        stack.push(new SymbolState(rule.getSource(), table.state.get(stack.isEmpty() ? 0 : stack.peek().stateIndex).state.get(rule.getSource())));
        result.push(rule.getAction().apply(children));
      } else {
        String out = "Parsing error.\n";
        out += "Next token: " + nextToken + "\n";
        out += "State: " + symbolState + "\n";
        out += "Stack: ";
        for (int i = 6; i >= 0; i--) {
          Optional<SymbolState> s = stack.deepPeek(i);
          if (!s.isPresent()) {
            break;
          }
          out += s.get();
          out += " ";
        }
        out += " (top)\n";
        throw new RuntimeException(out);
      }
      symbolState = stack.peek();
    }
    return result.pop();
  }
}
