package com.github.ahhoefel.lang.ast.symbols;

import java.util.ArrayList;
import java.util.List;

public class RegisterScope {
    
    private int registerCount;
    private int totalWidthBits;
    private List<RegisterTracker> registers;
    private List<List<RegisterTracker>> dependents;


    public RegisterScope() {
        this.registerCount = 0;
        this.totalWidthBits = 0;
        registers = new ArrayList<>();
        dependents = new ArrayList<>();
    }

    public RegisterTracker createRegister(int widthBits) {
      RegisterTracker r = new RegisterTracker(widthBits, totalWidthBits/8);
      totalWidthBits += widthBits;
      return r;
    }

    public int getTotalWidthBits() {
        return totalWidthBits;
    }

    public class RegisterTracker {    
        private int register;
        private int widthBits; 
        private int stackPositionBytes;

        public RegisterTracker(int widthBits, int stackPositionBytes) {
            this.widthBits = widthBits;
            this.register = RegisterScope.this.registerCount++;
            this.stackPositionBytes = stackPositionBytes;
            RegisterScope.this.registers.add(this);
        }

        public int getValue() {
            return register;
        }

        public int getWidthBits() {
            return widthBits;
        }

        public int getStackPositionBytes() {
            return stackPositionBytes;
        }
    }
}
