package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Util;

public final class WavUtil
{
  public static final int DATA_FOURCC = Util.getIntegerCodeForString("data");
  public static final int FMT_FOURCC;
  public static final int RIFF_FOURCC = Util.getIntegerCodeForString("RIFF");
  private static final int TYPE_A_LAW = 6;
  private static final int TYPE_FLOAT = 3;
  private static final int TYPE_MU_LAW = 7;
  private static final int TYPE_PCM = 1;
  private static final int TYPE_WAVE_FORMAT_EXTENSIBLE = 65534;
  public static final int WAVE_FOURCC = Util.getIntegerCodeForString("WAVE");
  
  static
  {
    FMT_FOURCC = Util.getIntegerCodeForString("fmt ");
  }
  
  private WavUtil() {}
  
  public static int getEncodingForType(int paramInt1, int paramInt2)
  {
    if (paramInt1 != 1) {
      if (paramInt1 != 3)
      {
        if (paramInt1 != 65534)
        {
          switch (paramInt1)
          {
          default: 
            return 0;
          case 7: 
            return 268435456;
          }
          return 536870912;
        }
      }
      else
      {
        if (paramInt2 != 32) {
          break label68;
        }
        return 4;
      }
    }
    return Util.getPcmEncoding(paramInt2);
    label68:
    return 0;
  }
  
  public static int getTypeForEncoding(int paramInt)
  {
    if (paramInt != Integer.MIN_VALUE)
    {
      if (paramInt != 268435456) {
        if (paramInt != 536870912) {
          if (paramInt == 1073741824) {
            break label79;
          }
        }
      }
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException();
      case 4: 
        return 3;
        return 6;
        return 7;
      }
    }
    label79:
    return 1;
  }
}
