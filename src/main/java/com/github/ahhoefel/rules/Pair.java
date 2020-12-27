package com.github.ahhoefel.rules;

public class Pair<S,T> {

    private S first;
    private T second;
    public Pair(S s, T t) {
        first = s;
        second = t;
    }

    public S getKey() {
        return first;
    }

    public T getValue() {
        return second;
    }
}
