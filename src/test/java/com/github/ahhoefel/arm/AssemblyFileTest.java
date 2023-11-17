package com.github.ahhoefel.arm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssemblyFileTest {
    @Test
    public void testValidity() {
        AssemblyFile file = new AssemblyFile();
        file.add(InstructionType.ADD.of(Register.X0, Register.X0, Register.X1));
        Assertions.assertTrue(file.isValid());
        file.add(InstructionType.ADD.of(Register.X0, Register.X0));
        Assertions.assertFalse(file.isValid());
    }
}
