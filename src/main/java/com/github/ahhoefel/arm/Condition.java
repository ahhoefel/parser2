package com.github.ahhoefel.arm;

public class Condition implements Parameter {

    public enum Code {
        EQ, // Equal. Z==1
        NE, // Not equal. Z==0
        CS, // Unsigned higher or same (or carry set). C==1
        HS,
        CC, // Unsigned lower (or carry clear). C==0
        LO,
        MI, // Negative. The mnemonic stands for "minus". N==1
        PL, // Positive or zero. The mnemonic stands for "plus". N==0
        VS, // Signed overflow. The mnemonic stands for "V set". V==1
        VC, // No signed overflow. The mnemonic stands for "V clear". V==0
        HI, // Unsigned higher. (C==1) && (Z==0)
        LS, // Unsigned lower or same. (C==0) || (Z==1)
        GE, // Signed greater than or equal. N==V
        LT, // Signed less than. N!=V
        GT, // Signed greater than. (Z==0) && (N==V)
        LE, // Signed less than or equal. (Z==1) || (N!=V)
        AL, // (or omitted) Always executed. None tested.
    }

    private Code code;

    public Condition(Code code) {
        this.code = code;
    }

    public String toString() {
        return this.code.toString();
    }
}