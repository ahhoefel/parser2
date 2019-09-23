package com.github.ahhoefel.interpreter;

public class Alloc {

  private long[] data;
  private int width;

  public Alloc(int width) {
    this.width = width;
    int n = width % 64 == 0 ? width / 64 : width / 64 + 1;
    data = new long[n];
  }

  public void setWord(int pos, long value) {
    data[pos] = value;
  }

  public long getWord(int pos) {
    return data[pos];
  }

  public boolean lessThan(Alloc a) {
    for (int i = 0; i < data.length; i++) {
      if (data[i] >= a.data[i]) {
        return false;
      }
    }
    return true;
  }

  public boolean lessThanOrEqual(Alloc a) {
    for (int i = 0; i < data.length; i++) {
      if (data[i] > a.data[i]) {
        return false;
      }
    }
    return true;
  }

  public Alloc minus(Alloc b) {
    Alloc c = new Alloc(width);
    for (int i = 0; i < data.length; i++) {
      c.data[i] = data[i] - b.data[i];
    }
    return c;
  }

  public Alloc add(Alloc b) {
    Alloc c = new Alloc(width);
    for (int i = 0; i < data.length; i++) {
      c.data[i] = data[i] + b.data[i];
    }
    return c;
  }

  public Alloc times(Alloc b) {
    Alloc c = new Alloc(width);
    for (int i = 0; i < data.length; i++) {
      c.data[i] = data[i] * b.data[i];
    }
    return c;
  }

  public boolean equalsZero() {
    for (long n : data) {
      if (n != 0) {
        return false;
      }
    }
    return true;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Alloc)) {
      return false;
    }
    Alloc a = (Alloc) o;
    if (a.width != width) {
      return false;
    }
    for (int i = 0; i < data.length; i++) {
      if (a.data[i] != data[i]) {
        return false;
      }
    }
    return true;
  }

  public Alloc copy() {
    Alloc a = new Alloc(width);
    for (int i = 0; i < this.data.length; i++) {
      a.data[i] = data[i];
    }
    return a;
  }

  public void copyFrom(Alloc other) {
    if (other.width != this.width) {
      throw new RuntimeException(String.format("Misaligned alloc copy from %d bit register to %d bit register", other.width, this.width));
    }
    for (int i = 0; i < data.length; i++) {
      data[i] = other.data[i];
    }
  }

  public void copyFrom(Alloc input, int inputOffsetBits, int outputOffsetBits, int lenBits) {
    if (inputOffsetBits + lenBits > input.width) {
      throw new RuntimeException(String.format("Misaligned alloc copy. Insufficient room in input."
          + "input(%d width, %d offset), output(%d width, %d offset), %d copy len", input.width, inputOffsetBits, this.width, outputOffsetBits, lenBits));
    }
    if (outputOffsetBits + lenBits > this.width) {
      throw new RuntimeException(String.format("Misaligned alloc copy. Insufficient room in output."
          + "input(%d width, %d offset), output(%d width, %d offset), %d copy len", input.width, inputOffsetBits, this.width, outputOffsetBits, lenBits));
    }

    if (inputOffsetBits % 64 != 0) {
      throw new RuntimeException("not supported");
    }
    int inputOffsetWords = inputOffsetBits / 64;

    if (outputOffsetBits % 64 != 0) {
      throw new RuntimeException("not supported");
    }
    int outputOffsetWords = outputOffsetBits / 64;

    if (lenBits % 64 != 0) {
      throw new RuntimeException("not supported");
    }
    int lenWords = lenBits / 64;

    int n = outputOffsetBits % 64;
    int m = 64 - n;
    if (n == 0) {
      this.copyFromWordAligned(input, inputOffsetWords, outputOffsetWords, lenWords);
      return;
    }
    int offsetBytes = outputOffsetBits / 64;
    long mMaskLow = 0xFFFF_FFFF_FFFF_FFFFL >>> m;
    long mMaskHigh = 0xFFFF_FFFF_FFFF_FFFFL << m;
    long nMaskLow = 0xFFFF_FFFF_FFFF_FFFFL >>> n;
    long nMaskHigh = 0xFFFF_FFFF_FFFF_FFFFL << n;
    for (int i = inputOffsetWords; i < lenWords; i++) {
      long lowerN = input.data[i] & nMaskLow;
      long lowerNShiftedHigh = lowerN << n;
      long upperM = input.data[i] & mMaskHigh;
      long upperMShiftedLow = upperM >> m;
      this.data[offsetBytes + i] = (this.data[offsetBytes + i] & mMaskLow) | lowerNShiftedHigh;
      this.data[offsetBytes + i + 1] = (this.data[offsetBytes + i + 1] & nMaskHigh) | upperMShiftedLow;
    }
  }

  private void copyFromWordAligned(Alloc input, int inputOffsetWords, int outputOffsetWords, int lenWords) {
    // System.out.println("CopyFromWordALigned: " + other + " offsetBytes " + offsetBytes);
    for (int i = 0; i < lenWords; i++) {
      this.data[i + outputOffsetWords] = input.data[i + inputOffsetWords];
    }
  }

  public String toString() {
    String out = Long.toString(data[0]);
    for (int i = 1; i < data.length; i++) {
      out += "_" + data[i];
    }
    return out;
  }
}
