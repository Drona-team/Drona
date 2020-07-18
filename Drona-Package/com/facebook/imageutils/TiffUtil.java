package com.facebook.imageutils;

import com.facebook.common.logging.FLog;
import java.io.IOException;
import java.io.InputStream;

class TiffUtil
{
  private static final Class<?> TAG = TiffUtil.class;
  public static final int TIFF_BYTE_ORDER_BIG_END = 1296891946;
  public static final int TIFF_BYTE_ORDER_LITTLE_END = 1229531648;
  public static final int TIFF_TAG_ORIENTATION = 274;
  public static final int TIFF_TYPE_SHORT = 3;
  
  TiffUtil() {}
  
  public static int getAutoRotateAngleFromOrientation(int paramInt)
  {
    if (paramInt != 3)
    {
      if (paramInt != 6)
      {
        if (paramInt != 8)
        {
          switch (paramInt)
          {
          default: 
            break;
          }
          return 0;
        }
        return 270;
      }
      return 90;
    }
    return 180;
  }
  
  private static int getOrientationFromTiffEntry(InputStream paramInputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (paramInt < 10) {
      return 0;
    }
    if (StreamProcessor.readPackedInt(paramInputStream, 2, paramBoolean) != 3) {
      return 0;
    }
    if (StreamProcessor.readPackedInt(paramInputStream, 4, paramBoolean) != 1) {
      return 0;
    }
    paramInt = StreamProcessor.readPackedInt(paramInputStream, 2, paramBoolean);
    StreamProcessor.readPackedInt(paramInputStream, 2, paramBoolean);
    return paramInt;
  }
  
  private static int moveToTiffEntryWithTag(InputStream paramInputStream, int paramInt1, boolean paramBoolean, int paramInt2)
    throws IOException
  {
    if (paramInt1 < 14) {
      return 0;
    }
    int j = StreamProcessor.readPackedInt(paramInputStream, 2, paramBoolean);
    int i = paramInt1 - 2;
    paramInt1 = j;
    while ((paramInt1 > 0) && (i >= 12))
    {
      j = StreamProcessor.readPackedInt(paramInputStream, 2, paramBoolean);
      i -= 2;
      if (j == paramInt2) {
        return i;
      }
      paramInputStream.skip(10L);
      i -= 10;
      paramInt1 -= 1;
    }
    return 0;
  }
  
  public static int readOrientationFromTIFF(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    TiffHeader localTiffHeader = new TiffHeader(null);
    paramInt = readTiffHeader(paramInputStream, paramInt, localTiffHeader);
    int i = firstIfdOffset - 8;
    if ((paramInt != 0) && (i <= paramInt))
    {
      paramInputStream.skip(i);
      return getOrientationFromTiffEntry(paramInputStream, moveToTiffEntryWithTag(paramInputStream, paramInt - i, isLittleEndian, 274), isLittleEndian);
    }
    return 0;
  }
  
  private static int readTiffHeader(InputStream paramInputStream, int paramInt, TiffHeader paramTiffHeader)
    throws IOException
  {
    if (paramInt <= 8) {
      return 0;
    }
    byteOrder = StreamProcessor.readPackedInt(paramInputStream, 4, false);
    if ((byteOrder != 1229531648) && (byteOrder != 1296891946))
    {
      FLog.e(TAG, "Invalid TIFF header");
      return 0;
    }
    boolean bool;
    if (byteOrder == 1229531648) {
      bool = true;
    } else {
      bool = false;
    }
    isLittleEndian = bool;
    firstIfdOffset = StreamProcessor.readPackedInt(paramInputStream, 4, isLittleEndian);
    paramInt = paramInt - 4 - 4;
    if ((firstIfdOffset >= 8) && (firstIfdOffset - 8 <= paramInt)) {
      return paramInt;
    }
    FLog.e(TAG, "Invalid offset");
    return 0;
  }
  
  private static class TiffHeader
  {
    int byteOrder;
    int firstIfdOffset;
    boolean isLittleEndian;
    
    private TiffHeader() {}
  }
}