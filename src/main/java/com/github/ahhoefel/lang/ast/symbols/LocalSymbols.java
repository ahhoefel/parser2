package com.github.ahhoefel.lang.ast.symbols;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.util.IndentedString;

// let x = 3;
// let y = 4;
// if (foo) {
//     let a = 5;
//     let b = 6;
// } else {
//     let c = 7;
// }
// let z = 5;

// <- x
// <- y (
//     <- a
//     <- b
//    ), (
//     <- c
//    ) 
// <- z

// 0 x -1
// 1 y 0
// 2 a 1
// 3 b 2
// 4 c 1
// 5 z 1

public class LocalSymbols {

    public static class LocalSymbol {
        public VariableDeclaration declaration;
        public SymbolIndex prevIndex;
        public LocalSymbol(VariableDeclaration declaration, SymbolIndex prevIndex) {
            this.declaration = declaration;
            this.prevIndex = prevIndex;
        }

        public String toString() {
            return declaration.toString() + ", " + prevIndex.value;
        }
    }

    public static class SymbolIndex {
        public int value;
        public SymbolIndex(int value) {
            this.value = value;
        }
    }
    private List<LocalSymbol> symbols;
    

    public LocalSymbols() {
        this.symbols = new ArrayList<>();
    }

    public Optional<LocalSymbol> get(String name, SymbolIndex index) {
        if (index.value < 0) return Optional.empty();
        LocalSymbol symbol = symbols.get(index.value);
        while (true) {
            if (symbol.declaration.getName().equals(name)) {
                return Optional.of(symbol);
            }
            if (symbol.prevIndex.value < 0) {
                return Optional.empty();
            }
            symbol = symbols.get(symbol.prevIndex.value);
        }
    }

    public SymbolIndex put(VariableDeclaration declaration, SymbolIndex prevIndex) {
        LocalSymbol s = new LocalSymbol(declaration, prevIndex);
        symbols.add(s);
        return new SymbolIndex(symbols.size() - 1);
    }

    public int getNumberOfSymbols() {
        return symbols.size();
    }

    public String toString() {
        String out = "";
        for (int i = 0; i < symbols.size(); i++) {
          out += symbols.get(i) + "\n";
        }
        return out;
    }

    public void toIndentedString(IndentedString out) {
        for (int i = 0; i < symbols.size(); i++) {
          out.add(Integer.toString(i));
          out.add(", ");
          out.add(symbols.get(i).toString());
          out.endLine();
        }
    }
}
