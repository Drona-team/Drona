package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;

final class NalUnitTargetBuffer
{
  private boolean isCompleted;
  private boolean isFilling;
  public byte[] nalData;
  public int nalLength;
  private final int targetType;
  
  public NalUnitTargetBuffer(int paramInt1, int paramInt2)
  {
    targetType = paramInt1;
    nalData = new byte[paramInt2 + 3];
    nalData[2] = 1;
  }
  
  public void appendToNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (!isFilling) {
      return;
    }
    paramInt2 -= paramInt1;
    if (nalData.length < nalLength + paramInt2) {
      nalData = Arrays.copyOf(nalData, (nalLength + paramInt2) * 2);
    }
    System.arraycopy(paramArrayOfByte, paramInt1, nalData, nalLength, paramInt2);
    nalLength += paramInt2;
  }
  
  public boolean endNalUnit(int paramInt)
  {
    if (!isFilling) {
      return false;
    }
    nalLength -= paramInt;
    isFilling = false;
    isCompleted = true;
    return true;
  }
  
  public boolean isCompleted()
  {
    return isCompleted;
  }
  
  public void reset()
  {
    isFilling = false;
    isCompleted = false;
  }
  
  public void startNalUnit(int paramInt)
  {
    boolean bool2 = isFilling;
    boolean bool1 = true;
    Assertions.checkState(bool2 ^ true);
    if (paramInt != targetType) {
      bool1 = false;
    }
    isFilling = bool1;
    if (isFilling)
    {
      nalLength = 3;
      isCompleted = false;
    }
  }
}
