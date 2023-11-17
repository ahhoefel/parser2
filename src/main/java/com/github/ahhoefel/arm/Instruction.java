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
            return params.get(0).toString() + ":";
        }
        StringBuilder out = new StringBuilder();
        out.append(type);
        out.append(" ");
        for (int i = 0; i < params.size(); i++) {
            Parameter p = params.get(i);
            out.append(p);
            if (i != params.size() - 1) {
                out.append(", ");
            }
        }
        return out.toString();
    }
}
