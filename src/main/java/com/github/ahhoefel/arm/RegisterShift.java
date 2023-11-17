package com.github.ahhoefel.arm;

public class RegisterShift<T extends Parameter> implements Parameter {

    private Register register;
    private T shift;


    public RegisterShift(Register register, T shift) {
        this.register = register;
        this.shift = shift;
    }

    public Register getRegister() {
        return register;
    }

    public T getShift() {
        return shift;
    }

    public String toString() {
        return "[" + register +", " + shift +  "]";
    }
}
