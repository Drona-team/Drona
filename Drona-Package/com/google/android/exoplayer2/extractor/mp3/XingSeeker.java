package com.google.android.exoplayer2.extractor.mp3;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class XingSeeker
  implements Mp3Extractor.Seeker
{
  private static final String TAG = "XingSeeker";
  private final long dataEndPosition;
  private final long dataSize;
  private final long dataStartPosition;
  private final long durationUs;
  @Nullable
  private final long[] tableOfContents;
  private final int xingFrameSize;
  
  private XingSeeker(long paramLong1, int paramInt, long paramLong2)
  {
    this(paramLong1, paramInt, paramLong2, -1L, null);
  }
  
  private XingSeeker(long paramLong1, int paramInt, long paramLong2, long paramLong3, long[] paramArrayOfLong)
  {
    dataStartPosition = paramLong1;
    xingFrameSize = paramInt;
    durationUs = paramLong2;
    tableOfContents = paramArrayOfLong;
    dataSize = paramLong3;
    paramLong2 = -1L;
    if (paramLong3 == -1L) {
      paramLong1 = paramLong2;
    } else {
      paramLong1 += paramLong3;
    }
    dataEndPosition = paramLong1;
  }
  
  public static XingSeeker create(long paramLong1, long paramLong2, MpegAudioHeader paramMpegAudioHeader, ParsableByteArray paramParsableByteArray)
  {
    int i = samplesPerFrame;
    int j = sampleRate;
    int k = paramParsableByteArray.readInt();
    if ((k & 0x1) == 1)
    {
      int m = paramParsableByteArray.readUnsignedIntToInt();
      if (m != 0)
      {
        long l1 = Util.scaleLargeTimestamp(m, i * 1000000L, j);
        if ((k & 0x6) != 6) {
          return new XingSeeker(paramLong2, frameSize, l1);
        }
        long l2 = paramParsableByteArray.readUnsignedIntToInt();
        long[] arrayOfLong = new long[100];
        i = 0;
        while (i < 100)
        {
          arrayOfLong[i] = paramParsableByteArray.readUnsignedByte();
          i += 1;
        }
        if (paramLong1 != -1L)
        {
          long l3 = paramLong2 + l2;
          if (paramLong1 != l3)
          {
            paramParsableByteArray = new StringBuilder();
            paramParsableByteArray.append("XING data size mismatch: ");
            paramParsableByteArray.append(paramLong1);
            paramParsableByteArray.append(", ");
            paramParsableByteArray.append(l3);
            Log.w("XingSeeker", paramParsableByteArray.toString());
          }
        }
        return new XingSeeker(paramLong2, frameSize, l1, l2, arrayOfLong);
      }
    }
    return null;
  }
  
  private long getTimeUsForTableIndex(int paramInt)
  {
    return durationUs * paramInt / 100L;
  }
  
  public long getDataEndPosition()
  {
    return dataEndPosition;
  }
  
  public long getDurationUs()
  {
    return durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    if (!isSeekable()) {
      return new SeekMap.SeekPoints(new SeekPoint(0L, dataStartPosition + xingFrameSize));
    }
    paramLong = Util.constrainValue(paramLong, 0L, durationUs);
    double d2 = paramLong * 100.0D / durationUs;
    double d1 = 0.0D;
    if (d2 > 0.0D) {
      if (d2 >= 100.0D)
      {
        d1 = 256.0D;
      }
      else
      {
        int i = (int)d2;
        long[] arrayOfLong = (long[])Assertions.checkNotNull(tableOfContents);
        double d3 = arrayOfLong[i];
        if (i == 99) {
          d1 = 256.0D;
        } else {
          d1 = arrayOfLong[(i + 1)];
        }
        d1 = d3 + (d2 - i) * (d1 - d3);
      }
    }
    long l = Util.constrainValue(Math.round(d1 / 256.0D * dataSize), xingFrameSize, dataSize - 1L);
    return new SeekMap.SeekPoints(new SeekPoint(paramLong, dataStartPosition + l));
  }
  
  public long getTimeUs(long paramLong)
  {
    paramLong -= dataStartPosition;
    if ((isSeekable()) && (paramLong > xingFrameSize))
    {
      long[] arrayOfLong = (long[])Assertions.checkNotNull(tableOfContents);
      double d = paramLong * 256.0D / dataSize;
      int i = Util.binarySearchFloor(arrayOfLong, d, true, true);
      long l1 = getTimeUsForTableIndex(i);
      long l2 = arrayOfLong[i];
      int j = i + 1;
      long l3 = getTimeUsForTableIndex(j);
      if (i == 99) {
        paramLong = 256L;
      } else {
        paramLong = arrayOfLong[j];
      }
      if (l2 == paramLong) {
        d = 0.0D;
      } else {
        d = (d - l2) / (paramLong - l2);
      }
      return l1 + Math.round(d * (l3 - l1));
    }
    return 0L;
  }
  
  public boolean isSeekable()
  {
    return tableOfContents != null;
  }
}
