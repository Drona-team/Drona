package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.NalUnitUtil.SpsData;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.List;

public final class AvcConfig
{
  public final int height;
  public final List<byte[]> initializationData;
  public final int nalUnitLengthFieldLength;
  public final float pixelWidthAspectRatio;
  public final int width;
  
  private AvcConfig(List paramList, int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    initializationData = paramList;
    nalUnitLengthFieldLength = paramInt1;
    width = paramInt2;
    height = paramInt3;
    pixelWidthAspectRatio = paramFloat;
  }
  
  private static byte[] buildNalUnitForChild(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readUnsignedShort();
    int j = paramParsableByteArray.getPosition();
    paramParsableByteArray.skipBytes(i);
    return CodecSpecificDataUtil.buildNalUnit(data, j, i);
  }
  
  public static AvcConfig parse(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    try
    {
      paramParsableByteArray.skipBytes(4);
      int i = paramParsableByteArray.readUnsignedByte();
      int k = (i & 0x3) + 1;
      if (k != 3)
      {
        ArrayList localArrayList = new ArrayList();
        i = paramParsableByteArray.readUnsignedByte();
        int j = i & 0x1F;
        i = 0;
        while (i < j)
        {
          localArrayList.add(buildNalUnitForChild(paramParsableByteArray));
          i += 1;
        }
        int m = paramParsableByteArray.readUnsignedByte();
        i = 0;
        while (i < m)
        {
          localArrayList.add(buildNalUnitForChild(paramParsableByteArray));
          i += 1;
        }
        float f;
        if (j > 0)
        {
          paramParsableByteArray = localArrayList.get(0);
          paramParsableByteArray = (byte[])paramParsableByteArray;
          Object localObject = localArrayList.get(0);
          localObject = (byte[])localObject;
          i = paramParsableByteArray.length;
          paramParsableByteArray = NalUnitUtil.parseSpsNalUnit((byte[])localObject, k, i);
          f = pixelWidthAspectRatio;
          j = width;
          i = height;
        }
        else
        {
          j = -1;
          i = -1;
          f = 1.0F;
        }
        paramParsableByteArray = new AvcConfig(localArrayList, k, j, i, f);
        return paramParsableByteArray;
      }
      paramParsableByteArray = new IllegalStateException();
      throw paramParsableByteArray;
    }
    catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
    {
      throw new ParserException("Error parsing AVC config", paramParsableByteArray);
    }
  }
}
