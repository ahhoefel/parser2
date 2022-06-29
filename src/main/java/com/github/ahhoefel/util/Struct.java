package com.github.ahhoefel.util;

import java.util.ArrayList;
import java.util.List;

public class Struct {

    private class Entry {
        String key;
        Struct struct;
        String value;

        public Entry(String key, Struct struct) {
            this.key = key;
            this.struct = struct;
        }

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private List<Entry> entries;

    public Struct() {
        entries = new ArrayList<>();
    }

    public Struct add(String key, Struct struct) {
        entries.add(new Entry(key, struct));
        return this;
    }

    public Struct addStruct(String key) {
        Struct struct = new Struct();
        entries.add(new Entry(key, struct));
        return struct;
    }

    public Struct add(String key, String value) {
        entries.add(new Entry(key, value));
        return this;
    }

    public void toIndentedString(IndentedString string) {
        for (Entry entry : entries) {
            if (entry.value != null) {
                string.addLine(entry.key + ": " + entry.value);
            } else {
                string.addLine(entry.key + " {");
                string.indent();
                entry.struct.toIndentedString(string);
                string.unindent();
            }
        }
    }

    public String toString() {
        IndentedString out = new IndentedString();
        toIndentedString(out);
        return out.toString();
    }
}
