package com.github.ahhoefel.ast;

import com.github.ahhoefel.util.IndentedString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportCatalog {

  private List<Import> imports;
  private Map<String, Import> importsByName;

  public ImportCatalog() {
    imports = new ArrayList<>();
    importsByName = new HashMap<>();
  }

  public void add(Import imp0rt) {
    imports.add(imp0rt);
    importsByName.put(imp0rt.getShortName(), imp0rt);
  }

  public void toIndentedString(IndentedString s) {
    for (Import i : imports) {
      i.toIndentedString(s);
    }
  }

  public void linkImports(FileTree.TargetMap map) {
    for (Import imp0rt : imports) {
      imp0rt.link(map);
      System.out.println("\t" + imp0rt);
    }
  }

  public List<Import> toList() {
    return imports;
  }

  public Import get(String shortName) {
    return importsByName.get(shortName);
  }
}
