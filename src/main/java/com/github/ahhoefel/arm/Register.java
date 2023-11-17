package com.github.ahhoefel.arm;

import java.util.HashMap;
import java.util.Map;

import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;

public class Register implements Parameter {

    public static final Register X0 = new Register("x0");
    public static final Register X1 = new Register("x1");
    public static final Register SP = new Register("sp");

    private String name;
    Register(String name) {
        this.name = name;
    }

    private static Map<Integer, Register> virtualRegisters = new HashMap<>();
    public static Register virtual(int n) {
        if (virtualRegisters.containsKey(n)) {
            return virtualRegisters.get(n);
        }
        Register v = new Register("V" + n);
        virtualRegisters.put(n, v);
        return v;
    }

    public static Register virtual(RegisterTracker register) {
        return virtual(register.getValue());
    }

    public static Register virtual(Expression expr) {
        return virtual(expr.getRegisterTracker());
    }

    public String toString() {
        return name;
    }

}
