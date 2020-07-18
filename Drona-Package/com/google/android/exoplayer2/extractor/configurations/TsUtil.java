package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.util.ParsableByteArray;

public final class TsUtil
{
  private TsUtil() {}
  
  public static int findSyncBytePosition(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    while ((paramInt1 < paramInt2) && (paramArrayOfByte[paramInt1] != 71)) {
      paramInt1 += 1;
    }
    return paramInt1;
  }
  
  public static long readPcrFromPacket(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    paramParsableByteArray.setPosition(paramInt1);
    if (paramParsableByteArray.bytesLeft() < 5) {
      return -9223372036854775807L;
    }
    paramInt1 = paramParsableByteArray.readInt();
    if ((0x800000 & paramInt1) != 0) {
      return -9223372036854775807L;
    }
    if ((0x1FFF00 & paramInt1) >> 8 != paramInt2) {
      return -9223372036854775807L;
    }
    paramInt2 = 1;
    if ((paramInt1 & 0x20) != 0) {
      paramInt1 = 1;
    } else {
      paramInt1 = 0;
    }
    if (paramInt1 == 0) {
      return -9223372036854775807L;
    }
    if ((paramParsableByteArray.readUnsignedByte() >= 7) && (paramParsableByteArray.bytesLeft() >= 7))
    {
      if ((paramParsableByteArray.readUnsignedByte() & 0x10) == 16) {
        paramInt1 = paramInt2;
      } else {
        paramInt1 = 0;
      }
      if (paramInt1 != 0)
      {
        byte[] arrayOfByte = new byte[6];
        paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
        return readPcrValueFromPcrBytes(arrayOfByte);
      }
    }
    return -9223372036854775807L;
  }
  
  private static long readPcrValueFromPcrBytes(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] & 0xFF) << 25 | (paramArrayOfByte[1] & 0xFF) << 17 | (paramArrayOfByte[2] & 0xFF) << 9 | (paramArrayOfByte[3] & 0xFF) << 1 | (0xFF & paramArrayOfByte[4]) >> 7;
  }
}
