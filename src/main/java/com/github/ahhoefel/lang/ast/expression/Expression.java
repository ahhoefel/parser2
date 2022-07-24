package com.github.ahhoefel.lang.ast.expression;

import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.type.Type;

public interface Expression extends Visitable {
    boolean isLValue();

    Type getType();
}
