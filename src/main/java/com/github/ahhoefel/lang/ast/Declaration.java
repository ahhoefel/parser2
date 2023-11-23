package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.parser.Locateable;

public interface Declaration extends Visitable, Locateable {
  File addToFile(File file);
}
