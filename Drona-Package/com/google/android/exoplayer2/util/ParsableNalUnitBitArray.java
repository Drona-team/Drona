package com.google.android.exoplayer2.util;

public final class ParsableNalUnitBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  private byte[] data;
  
  public ParsableNalUnitBitArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    reset(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void assertValidOffset()
  {
    boolean bool;
    if ((byteOffset >= 0) && ((byteOffset < byteLimit) || ((byteOffset == byteLimit) && (bitOffset == 0)))) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
  }
  
  private int readExpGolombCodeNum()
  {
    int j = 0;
    int i = 0;
    while (!readBit()) {
      i += 1;
    }
    if (i > 0) {
      j = readBits(i);
    }
    return (1 << i) - 1 + j;
  }
  
  private boolean shouldSkipByte(int paramInt)
  {
    return (2 <= paramInt) && (paramInt < byteLimit) && (data[paramInt] == 3) && (data[(paramInt - 2)] == 0) && (data[(paramInt - 1)] == 0);
  }
  
  public boolean canReadBits(int paramInt)
  {
    int k = byteOffset;
    int i = paramInt / 8;
    int m = byteOffset + i;
    int n = bitOffset + paramInt - i * 8;
    paramInt = k;
    i = m;
    int j = n;
    if (n > 7)
    {
      i = m + 1;
      j = n - 8;
      paramInt = k;
    }
    for (;;)
    {
      k = paramInt + 1;
      if ((k > i) || (i >= byteLimit)) {
        break;
      }
      paramInt = k;
      if (shouldSkipByte(k))
      {
        i += 1;
        paramInt = k + 2;
      }
    }
    if (i >= byteLimit) {
      return (i == byteLimit) && (j == 0);
    }
    return true;
  }
  
  public boolean canReadExpGolombCodedNum()
  {
    int k = byteOffset;
    int m = bitOffset;
    int i = 0;
    while ((byteOffset < byteLimit) && (!readBit())) {
      i += 1;
    }
    int j;
    if (byteOffset == byteLimit) {
      j = 1;
    } else {
      j = 0;
    }
    byteOffset = k;
    bitOffset = m;
    return (j == 0) && (canReadBits(i * 2 + 1));
  }
  
  public boolean readBit()
  {
    boolean bool;
    if ((data[byteOffset] & 128 >> bitOffset) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    skipBit();
    return bool;
  }
  
  public int readBits(int paramInt)
  {
    bitOffset += paramInt;
    int i = 0;
    int j;
    for (;;)
    {
      k = bitOffset;
      j = 2;
      if (k <= 8) {
        break;
      }
      bitOffset -= 8;
      i |= (data[byteOffset] & 0xFF) << bitOffset;
      k = byteOffset;
      if (!shouldSkipByte(byteOffset + 1)) {
        j = 1;
      }
      byteOffset = (k + j);
    }
    int k = data[byteOffset];
    int m = bitOffset;
    if (bitOffset == 8)
    {
      bitOffset = 0;
      int n = byteOffset;
      if (!shouldSkipByte(byteOffset + 1)) {
        j = 1;
      }
      byteOffset = (n + j);
    }
    assertValidOffset();
    return -1 >>> 32 - paramInt & (i | (k & 0xFF) >> 8 - m);
  }
  
  public int readSignedExpGolombCodedInt()
  {
    int j = readExpGolombCodeNum();
    int i;
    if (j % 2 == 0) {
      i = -1;
    } else {
      i = 1;
    }
    return i * ((j + 1) / 2);
  }
  
  public int readUnsignedExpGolombCodedInt()
  {
    return readExpGolombCodeNum();
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    data = paramArrayOfByte;
    byteOffset = paramInt1;
    byteLimit = paramInt2;
    bitOffset = 0;
    assertValidOffset();
  }
  
  public void skipBit()
  {
    int j = bitOffset;
    int i = 1;
    j += 1;
    bitOffset = j;
    if (j == 8)
    {
      bitOffset = 0;
      j = byteOffset;
      if (shouldSkipByte(byteOffset + 1)) {
        i = 2;
      }
      byteOffset = (j + i);
    }
    assertValidOffset();
  }
  
  public void skipBits(int paramInt)
  {
    int i = byteOffset;
    int j = paramInt / 8;
    byteOffset += j;
    bitOffset += paramInt - j * 8;
    paramInt = i;
    if (bitOffset > 7)
    {
      byteOffset += 1;
      bitOffset -= 8;
      paramInt = i;
    }
    for (;;)
    {
      i = paramInt + 1;
      if (i > byteOffset) {
        break;
      }
      paramInt = i;
      if (shouldSkipByte(i))
      {
        byteOffset += 1;
        paramInt = i + 2;
      }
    }
    assertValidOffset();
  }
}
