package com.github.ahhoefel.arm;

import java.util.Arrays;
import java.util.List;

public class Instruction {

    private InstructionType type;
    private List<Parameter> params;

    public Instruction(InstructionType type, Parameter... params) {
        this.type = type;
        this.params = Arrays.asList(params);
    }

    public boolean isValid() {
        return type.validParams(this.params);
    }

    public String toString() {
        if (this.type == InstructionType.LABEL) {
            return params.get(0).toString() + ":" + (params.size() == 2 ? " // " + params.get(1) : "");
        }
        if (this.type == InstructionType.COMMENT) {
            return "// " + params.get(0).toString();
        }
        StringBuilder out = new StringBuilder();
        out.append(type);
        out.append(" ");
        int n = params.size();
        if (params.size() > 0 && ParameterType.COMMENT.matches(params.get(params.size() - 1))) {
            n = params.size() - 1;
        }
        for (int i = 0; i < n; i++) {
            Parameter p = params.get(i);
            out.append(p);
            if (i != n - 1) {
                out.append(", ");
            }
        }
        if (n != params.size()) {
            out.append(" // ");
            out.append(params.get(n));
        }
        return out.toString();
    }
}
