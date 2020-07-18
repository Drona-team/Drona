package com.google.android.exoplayer2.text.ClickListeners;

import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

public final class CeaUtil
{
  private static final int COUNTRY_CODE = 181;
  private static final String PAGE_KEY = "CeaUtil";
  private static final int PAYLOAD_TYPE_CC = 4;
  private static final int PROVIDER_CODE_ATSC = 49;
  private static final int PROVIDER_CODE_DIRECTV = 47;
  public static final int USER_DATA_IDENTIFIER_GA94 = Util.getIntegerCodeForString("GA94");
  public static final int USER_DATA_TYPE_CODE_MPEG_CC = 3;
  
  private CeaUtil() {}
  
  public static void consume(long paramLong, ParsableByteArray paramParsableByteArray, TrackOutput[] paramArrayOfTrackOutput)
  {
    for (;;)
    {
      int i = paramParsableByteArray.bytesLeft();
      int n = 1;
      if (i <= 1) {
        break;
      }
      int j = readNon255TerminatedValue(paramParsableByteArray);
      int k = readNon255TerminatedValue(paramParsableByteArray);
      int m = paramParsableByteArray.getPosition() + k;
      if ((k != -1) && (k <= paramParsableByteArray.bytesLeft()))
      {
        i = m;
        if (j == 4)
        {
          i = m;
          if (k >= 8)
          {
            i = paramParsableByteArray.readUnsignedByte();
            int i1 = paramParsableByteArray.readUnsignedShort();
            if (i1 == 49) {
              j = paramParsableByteArray.readInt();
            } else {
              j = 0;
            }
            k = paramParsableByteArray.readUnsignedByte();
            if (i1 == 47) {
              paramParsableByteArray.skipBytes(1);
            }
            if ((i == 181) && ((i1 == 49) || (i1 == 47)) && (k == 3)) {
              i = 1;
            } else {
              i = 0;
            }
            k = i;
            if (i1 == 49)
            {
              if (j == USER_DATA_IDENTIFIER_GA94) {
                j = n;
              } else {
                j = 0;
              }
              k = i & j;
            }
            i = m;
            if (k != 0)
            {
              consumeCcData(paramLong, paramParsableByteArray, paramArrayOfTrackOutput);
              i = m;
            }
          }
        }
      }
      else
      {
        Log.w("CeaUtil", "Skipping remainder of malformed SEI NAL unit.");
        i = paramParsableByteArray.limit();
      }
      paramParsableByteArray.setPosition(i);
    }
  }
  
  public static void consumeCcData(long paramLong, ParsableByteArray paramParsableByteArray, TrackOutput[] paramArrayOfTrackOutput)
  {
    int k = paramParsableByteArray.readUnsignedByte();
    int j = 0;
    if ((k & 0x40) != 0) {
      i = 1;
    } else {
      i = 0;
    }
    if (i == 0) {
      return;
    }
    paramParsableByteArray.skipBytes(1);
    k = (k & 0x1F) * 3;
    int m = paramParsableByteArray.getPosition();
    int n = paramArrayOfTrackOutput.length;
    int i = j;
    while (i < n)
    {
      TrackOutput localTrackOutput = paramArrayOfTrackOutput[i];
      paramParsableByteArray.setPosition(m);
      localTrackOutput.sampleData(paramParsableByteArray, k);
      localTrackOutput.sampleMetadata(paramLong, 1, k, 0, null);
      i += 1;
    }
  }
  
  private static int readNon255TerminatedValue(ParsableByteArray paramParsableByteArray)
  {
    int i = 0;
    int k;
    int j;
    do
    {
      if (paramParsableByteArray.bytesLeft() == 0) {
        return -1;
      }
      k = paramParsableByteArray.readUnsignedByte();
      j = i + k;
      i = j;
    } while (k == 255);
    return j;
  }
}
