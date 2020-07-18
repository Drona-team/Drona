package com.google.android.exoplayer2.util;

public final class ParsableBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  public byte[] data;
  
  public ParsableBitArray()
  {
    data = Util.EMPTY_BYTE_ARRAY;
  }
  
  public ParsableBitArray(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public ParsableBitArray(byte[] paramArrayOfByte, int paramInt)
  {
    data = paramArrayOfByte;
    byteLimit = paramInt;
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
  
  public void byteAlign()
  {
    if (bitOffset == 0) {
      return;
    }
    bitOffset = 0;
    byteOffset += 1;
    assertValidOffset();
  }
  
  public int getBytePosition()
  {
    boolean bool;
    if (bitOffset == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    return byteOffset;
  }
  
  public int getPosition()
  {
    return byteOffset * 8 + bitOffset;
  }
  
  public void putInt(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (paramInt2 < 32) {
      i = paramInt1 & (1 << paramInt2) - 1;
    }
    int j = Math.min(8 - bitOffset, paramInt2);
    paramInt1 = 8 - bitOffset - j;
    int k = bitOffset;
    data[byteOffset] = ((byte)((65280 >> k | (1 << paramInt1) - 1) & data[byteOffset]));
    j = paramInt2 - j;
    data[byteOffset] = ((byte)(i >>> j << paramInt1 | data[byteOffset]));
    paramInt1 = byteOffset + 1;
    while (j > 8)
    {
      data[paramInt1] = ((byte)(i >>> j - 8));
      j -= 8;
      paramInt1 += 1;
    }
    k = 8 - j;
    data[paramInt1] = ((byte)(data[paramInt1] & (1 << k) - 1));
    data[paramInt1] = ((byte)((i & (1 << j) - 1) << k | data[paramInt1]));
    skipBits(paramInt2);
    assertValidOffset();
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
    if (paramInt == 0) {
      return 0;
    }
    bitOffset += paramInt;
    int i = 0;
    while (bitOffset > 8)
    {
      bitOffset -= 8;
      byte[] arrayOfByte = data;
      j = byteOffset;
      byteOffset = (j + 1);
      i |= (arrayOfByte[j] & 0xFF) << bitOffset;
    }
    int j = data[byteOffset];
    int k = bitOffset;
    if (bitOffset == 8)
    {
      bitOffset = 0;
      byteOffset += 1;
    }
    assertValidOffset();
    return -1 >>> 32 - paramInt & (i | (j & 0xFF) >> 8 - k);
  }
  
  public void readBits(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = (paramInt2 >> 3) + paramInt1;
    byte[] arrayOfByte;
    while (paramInt1 < i)
    {
      arrayOfByte = data;
      j = byteOffset;
      byteOffset = (j + 1);
      paramArrayOfByte[paramInt1] = ((byte)(arrayOfByte[j] << bitOffset));
      j = paramArrayOfByte[paramInt1];
      paramArrayOfByte[paramInt1] = ((byte)((0xFF & data[byteOffset]) >> 8 - bitOffset | j));
      paramInt1 += 1;
    }
    paramInt1 = paramInt2 & 0x7;
    if (paramInt1 == 0) {
      return;
    }
    paramArrayOfByte[i] = ((byte)(paramArrayOfByte[i] & 255 >> paramInt1));
    if (bitOffset + paramInt1 > 8)
    {
      paramInt2 = paramArrayOfByte[i];
      arrayOfByte = data;
      j = byteOffset;
      byteOffset = (j + 1);
      paramArrayOfByte[i] = ((byte)(paramInt2 | (arrayOfByte[j] & 0xFF) << bitOffset));
      bitOffset -= 8;
    }
    bitOffset += paramInt1;
    paramInt2 = data[byteOffset];
    int j = bitOffset;
    int k = paramArrayOfByte[i];
    paramArrayOfByte[i] = ((byte)((byte)((paramInt2 & 0xFF) >> 8 - j << 8 - paramInt1) | k));
    if (bitOffset == 8)
    {
      bitOffset = 0;
      byteOffset += 1;
    }
    assertValidOffset();
  }
  
  public void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    boolean bool;
    if (bitOffset == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    System.arraycopy(data, byteOffset, paramArrayOfByte, paramInt1, paramInt2);
    byteOffset += paramInt2;
    assertValidOffset();
  }
  
  public void reset(ParsableByteArray paramParsableByteArray)
  {
    reset(data, paramParsableByteArray.limit());
    setPosition(paramParsableByteArray.getPosition() * 8);
  }
  
  public void reset(byte[] paramArrayOfByte)
  {
    reset(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt)
  {
    data = paramArrayOfByte;
    byteOffset = 0;
    bitOffset = 0;
    byteLimit = paramInt;
  }
  
  public void setPosition(int paramInt)
  {
    byteOffset = (paramInt / 8);
    bitOffset = (paramInt - byteOffset * 8);
    assertValidOffset();
  }
  
  public void skipBit()
  {
    int i = bitOffset + 1;
    bitOffset = i;
    if (i == 8)
    {
      bitOffset = 0;
      byteOffset += 1;
    }
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
  
  public void skipBytes(int paramInt)
  {
    boolean bool;
    if (bitOffset == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    byteOffset += paramInt;
    assertValidOffset();
  }
}
