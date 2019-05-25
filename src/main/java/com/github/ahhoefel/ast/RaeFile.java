package com.github.ahhoefel.ast;

import com.github.ahhoefel.ir.Representation;
import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;
import java.util.List;

public class RaeFile {

  private SymbolCatalog symbols;
  private List<Declaration> declarations;

  public RaeFile() {
    declarations = new ArrayList<>();
    symbols = new SymbolCatalog();
  }

  public RaeFile add(Declaration d) {
    declarations.add(d);
    if (d.isFunction()) {
      symbols.addFunction(d.getFunction());
    }
    return this;
  }

  public Representation representation() {
    Representation rep = new Representation();
    for (Declaration d : declarations) {
      if (d.isFunction()) {
        d.getFunction().addToRepresentation(rep);
      }
    }
    return rep;
  }

  public String toString() {
    IndentedString s = new IndentedString();
    toIndentedString(s);
    return s.toString();
  }

  public void toIndentedString(IndentedString s) {
    for (Declaration d : declarations) {
      d.toIndentedString(s);
      s.endLine();
    }
  }
}
