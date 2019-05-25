package com.github.ahhoefel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TokenAction implements Function<Object[], Object> {

  private Symbol terminal;
  private Map<String, Symbol> keywordMap;

  public TokenAction(Symbol terminal) {
    this(terminal, List.of());
  }

  public TokenAction(Symbol terminal, List<Symbol> keywords) {
    this.terminal = terminal;
    keywordMap = new HashMap<>();
    for (Symbol keyword : keywords) {
      keywordMap.put(keyword.toString(), keyword);
    }
  }

  @Override
  public Object apply(Object[] objects) {
    Object o = objects[0];
    String value = o instanceof String ? (String) o : ((Token) o).getValue();
    Symbol symbol = keywordMap.get(value);
    if (symbol == null) {
      symbol = terminal;
    }
    return new Token(symbol, value);
  }
}
