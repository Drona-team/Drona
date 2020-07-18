package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.util.Assertions;

final class VorbisBitArray
{
  private int bitOffset;
  private final int byteLimit;
  private int byteOffset;
  private final byte[] data;
  
  public VorbisBitArray(byte[] paramArrayOfByte)
  {
    data = paramArrayOfByte;
    byteLimit = paramArrayOfByte.length;
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
  
  public int bitsLeft()
  {
    return (byteLimit - byteOffset) * 8 - bitOffset;
  }
  
  public int getPosition()
  {
    return byteOffset * 8 + bitOffset;
  }
  
  public boolean readBit()
  {
    boolean bool;
    if (((data[byteOffset] & 0xFF) >> bitOffset & 0x1) == 1) {
      bool = true;
    } else {
      bool = false;
    }
    skipBits(1);
    return bool;
  }
  
  public int readBits(int paramInt)
  {
    int m = byteOffset;
    int k = Math.min(paramInt, 8 - bitOffset);
    int j = k;
    byte[] arrayOfByte = data;
    int i = m + 1;
    k = (arrayOfByte[m] & 0xFF) >> bitOffset & 255 >> 8 - k;
    while (j < paramInt)
    {
      k |= (data[i] & 0xFF) << j;
      j += 8;
      i += 1;
    }
    skipBits(paramInt);
    return k & -1 >>> 32 - paramInt;
  }
  
  public void reset()
  {
    byteOffset = 0;
    bitOffset = 0;
  }
  
  public void setPosition(int paramInt)
  {
    byteOffset = (paramInt / 8);
    bitOffset = (paramInt - byteOffset * 8);
    assertValidOffset();
  }
  
  public void skipBits(int paramInt)
  {
    int i = paramInt / 8;
    byteOffset += i;
    bitOffset += paramInt - i * 8;
    if (bitOffset > 7)
    {
      byteOffset += 1;
      bitOffset -= 8;
    }
    assertValidOffset();
  }
}
