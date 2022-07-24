package com.github.ahhoefel.lang.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportCatalog implements Visitable {

    private List<Import> imports;
    private Map<String, Import> importsByName;

    public ImportCatalog() {
        imports = new ArrayList<>();
        importsByName = new HashMap<>();
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public void add(Import imp0rt) {
        imports.add(imp0rt);
        importsByName.put(imp0rt.getShortName(), imp0rt);
    }

    public List<Import> getImports() {
        return imports;
    }

    public Import get(String shortName) {
        return importsByName.get(shortName);
    }
}
