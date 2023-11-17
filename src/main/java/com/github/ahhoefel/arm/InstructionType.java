package com.github.ahhoefel.arm;

import java.util.Arrays;
import java.util.List;

public class InstructionType {

    public static final InstructionType ADD = new InstructionType("ADD", ParameterType.X, ParameterType.X,
            ParameterType.X);
    public static final InstructionType ADD_IMM = new InstructionType("ADD", ParameterType.X, ParameterType.X,
            ParameterType.UINT_12);
    public static final InstructionType SUB = new InstructionType("SUB", ParameterType.X, ParameterType.X,
            ParameterType.X);
    public static final InstructionType SUB_IMM = new InstructionType("SUB", ParameterType.X, ParameterType.X,
            ParameterType.UINT_12);
    public static final InstructionType MUL = new InstructionType("MUL", ParameterType.X, ParameterType.X,
            ParameterType.X);

    public static final InstructionType LDR = new InstructionType("LDR", ParameterType.X, ParameterType.LABEL);
    public static final InstructionType LDR_REGISTER_OFFSET = new InstructionType("LDR", ParameterType.X,
            ParameterType.X_SHIFT_UINT_15_MULTIPLE_OF_8);
    public static final InstructionType STR_REGISTER_OFFSET = new InstructionType("STR", ParameterType.X,
            ParameterType.X_SHIFT_UINT_15_MULTIPLE_OF_8);
    public static final InstructionType MOV = new InstructionType("MOV", ParameterType.X, ParameterType.X);
    public static final InstructionType MOV_WIDE_IMM = new InstructionType("MOV", ParameterType.X,
            ParameterType.UINT_64);
    public static final InstructionType BL = new InstructionType("BL", ParameterType.LABEL);
    public static final InstructionType B = new InstructionType("B", ParameterType.X);
    public static final InstructionType RET = new InstructionType("RET");
    public static final InstructionType LABEL = new InstructionType("LABEL", ParameterType.LABEL);
    public static final InstructionType CMP = new InstructionType("CMP", ParameterType.X, ParameterType.UINT_12);
    public static final InstructionType B_EQ = new InstructionType("B.EQ", ParameterType.LABEL);
    public static final InstructionType B_NE = new InstructionType("B.NE", ParameterType.LABEL);
    public static final InstructionType CSET = new InstructionType("CSET", ParameterType.CONDITION);
    public static final InstructionType GLOBAL = new InstructionType(".global", ParameterType.LABEL);

    private String name;
    private List<ParameterType> paramTypes;

    public InstructionType(String name, ParameterType... paramTypes) {
        this.name = name;
        this.paramTypes = Arrays.asList(paramTypes);
    }

    public Instruction of(Parameter... params) {
        return new Instruction(this, params);
    }

    public boolean validParams(List<Parameter> params) {
        if (params.size() != paramTypes.size()) {
            return false;
        }
        for (int i = 0; i < params.size(); i++) {
            if (!paramTypes.get(i).matches(params.get(i))) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return name;
    }

}
