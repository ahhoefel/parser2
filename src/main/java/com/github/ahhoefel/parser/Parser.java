package com.github.ahhoefel.parser;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.ParseError;
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
      return String.format("(%s, %d, %d)", symbol.toString(), symbol.getIndex(), stateIndex);
    }
  }

  public static <C> Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start, C context, ErrorLog log) {
    return parseTokens(table, iter, start, Optional.of(context), log);
  }

  public static Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start, ErrorLog log) {
    return parseTokens(table, iter, start, Optional.empty(), log);
  }

  private static <C extends Locateable> Object parseTokens(LRTable table, Iterator<Token> iter, Symbol start,
      Optional<C> context,
      ErrorLog log) {
    Stack<SymbolState> stack = new Stack<>();
    Stack<Locateable> result = new Stack<>();
    Token nextToken = iter.next();
    Symbol nextSymbol = nextToken.getSymbol();
    SymbolState symbolState = new SymbolState(start, 0);
    while (true) {
      LRTable.State state = table.state.get(symbolState.stateIndex);
      // System.out.println(String.format("next: %s, state: %d, stack: %s", nextToken,
      // symbolState.stateIndex, stack));
      // System.out.println(result);
      if (state.shift.containsKey(nextSymbol)) {
        stack.push(new SymbolState(nextSymbol, state.shift.get(nextSymbol)));
        result.push(nextToken);
        if (iter.hasNext()) {
          nextToken = iter.next();
          nextSymbol = nextToken.getSymbol();
        } else {
          result.pop(); // Remove eof
          break;
        }
      } else if (state.reduce.containsKey(nextSymbol)) {
        Rule rule = state.reduce.get(nextSymbol);
        int numChildren = rule.getSymbols().size();
        int numParameters = numChildren + (context.isPresent() ? 1 : 0);
        Locateable[] children = new Locateable[numParameters];
        for (int i = 0; i < numChildren; i++) {
          stack.pop();
          children[numChildren - i - 1] = result.pop();
        }
        if (context.isPresent()) {
          children[numParameters - 1] = context.get();
        }
        stack.push(new SymbolState(rule.getSource(),
            table.state.get(stack.isEmpty() ? 0 : stack.peek().stateIndex).state.get(rule.getSource())));
        try {
          Locateable actionResult = rule.getAction().apply(children);
          // if (children.length > 0) {
          // if (actionResult == null) {
          // throw new Exception("Result should not be null:" +
          // rule.getAction().toString());
          // }
          // try {
          // actionResult
          // .setLocation(
          // new CodeLocation(children[0].getLocation(), children[children.length -
          // 1].getLocation()));
          // } catch (RuntimeException e) {
          // throw new RuntimeException("CodeLocation creation failed at rule: " +
          // rule.toString() + " locations "
          // + children[0].getLocation() + ", " + children[children.length -
          // 1].getLocation(), e);
          // }
          // } else {
          // throw new RuntimeException("No child rule: " + rule);
          // // actionResult.setLocation(new CodeLocation(null, 0, 0, 0));
          // }
          result.push(actionResult);
        } catch (Exception e) {
          System.out.println(new ParseError(nextToken.getLocation(), "ParseActionException at rule " + rule));
          e.printStackTrace();
          log.add(new ParseError(nextToken.getLocation(),
              "ParseActionException at rule " + rule, e));
          return null;
        }
      } else {
        String out = "Parsing error.\n";
        out += "Next token: " + nextToken + "\n";
        out += "State: " + symbolState + "\n";
        out += "Stack (top):\n";
        for (int i = 0; stack.deepPeek(i).isPresent(); i++) {
          Optional<SymbolState> s = stack.deepPeek(i);
          out += "\t" + s.get() + "\n";
        }
        out += "(bottom)";
        log.add(new ParseError(nextToken.getLocation(), out));
        return null;
      }
      symbolState = stack.peek();
    }
    // System.out.println("POP! " + result.peek());
    return result.pop();
  }
}
