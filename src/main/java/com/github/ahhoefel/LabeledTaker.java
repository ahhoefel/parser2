package com.github.ahhoefel;

public interface LabeledTaker<T, L> extends TokenTaker<T> {
  L getLabel();
}

