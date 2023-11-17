package com.github.ahhoefel.arm;

import java.math.BigInteger;

public class UInt64 implements Parameter {
    private static BigInteger MAX_UINT64 = new BigInteger("18446744073709551615");
    private static BigInteger MIN_UINT64 = new BigInteger("0");

    private BigInteger value;
    public UInt64(BigInteger value) {
        this.value = value;
        if (value.compareTo(MAX_UINT64) > 0 || value.compareTo(MIN_UINT64) < 0 ) {
              throw new RuntimeException("UInt64 value out of range [0,18446744073709551615]: " + value);
        }
    }

    public String toString() {
        return "#0x" + value.toString(16);
    }
}
