package com.google.android.exoplayer2.video;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Collections;
import java.util.List;

public final class HevcConfig
{
  @Nullable
  public final List<byte[]> initializationData;
  public final int nalUnitLengthFieldLength;
  
  private HevcConfig(List paramList, int paramInt)
  {
    initializationData = paramList;
    nalUnitLengthFieldLength = paramInt;
  }
  
  public static HevcConfig parse(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    try
    {
      paramParsableByteArray.skipBytes(21);
      int n = paramParsableByteArray.readUnsignedByte();
      int i1 = paramParsableByteArray.readUnsignedByte();
      int m = paramParsableByteArray.getPosition();
      int i = 0;
      int j = 0;
      int i2;
      int i3;
      while (i < i1)
      {
        paramParsableByteArray.skipBytes(1);
        i2 = paramParsableByteArray.readUnsignedShort();
        k = 0;
        while (k < i2)
        {
          i3 = paramParsableByteArray.readUnsignedShort();
          j += i3 + 4;
          paramParsableByteArray.skipBytes(i3);
          k += 1;
        }
        i += 1;
      }
      paramParsableByteArray.setPosition(m);
      byte[] arrayOfByte1 = new byte[j];
      int k = 0;
      i = 0;
      while (k < i1)
      {
        paramParsableByteArray.skipBytes(1);
        i2 = paramParsableByteArray.readUnsignedShort();
        m = 0;
        while (m < i2)
        {
          i3 = paramParsableByteArray.readUnsignedShort();
          byte[] arrayOfByte2 = NalUnitUtil.NAL_START_CODE;
          int i4 = NalUnitUtil.NAL_START_CODE.length;
          System.arraycopy(arrayOfByte2, 0, arrayOfByte1, i, i4);
          i += NalUnitUtil.NAL_START_CODE.length;
          arrayOfByte2 = data;
          System.arraycopy(arrayOfByte2, paramParsableByteArray.getPosition(), arrayOfByte1, i, i3);
          i += i3;
          paramParsableByteArray.skipBytes(i3);
          m += 1;
        }
        k += 1;
      }
      if (j == 0) {
        paramParsableByteArray = null;
      } else {
        paramParsableByteArray = Collections.singletonList(arrayOfByte1);
      }
      paramParsableByteArray = new HevcConfig(paramParsableByteArray, (n & 0x3) + 1);
      return paramParsableByteArray;
    }
    catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
    {
      throw new ParserException("Error parsing HEVC config", paramParsableByteArray);
    }
  }
}
