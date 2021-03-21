package com.github.ahhoefel.parser;

import com.github.ahhoefel.ast.ErrorLog;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 7660302783847007290L;

    private ErrorLog e;

    public ParseException(ErrorLog e) {
        this.e = e;
    }

    public ErrorLog getErrorLog() {
        return e;
    }

    public String toString() {
        return e.toString();
    }

}
