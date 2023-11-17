package com.github.ahhoefel.arm;

import java.util.ArrayList;
import java.util.List;


public class AssemblyFile {
   
    
    private List<Instruction> instructions;

    public AssemblyFile() {
        this.instructions = new ArrayList<>();
    }

    public void add(Instruction instruction) {
        instructions.add(instruction);
    }

    public boolean isValid() {
        for (Instruction i : instructions) {
            if(!i.isValid()) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Instruction i : instructions) {
            b.append(i.toString());
            b.append("\n");
        }
        return b.toString();
    }
}
