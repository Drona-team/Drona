package com.google.android.exoplayer2.extractor.mp3;

import android.util.Pair;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.metadata.configurations.MlltFrame;
import com.google.android.exoplayer2.util.Util;

final class MlltSeeker
  implements Mp3Extractor.Seeker
{
  private final long durationUs;
  private final long[] referencePositions;
  private final long[] referenceTimesMs;
  
  private MlltSeeker(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    referencePositions = paramArrayOfLong1;
    referenceTimesMs = paramArrayOfLong2;
    durationUs = IpAddress.msToUs(paramArrayOfLong2[(paramArrayOfLong2.length - 1)]);
  }
  
  public static MlltSeeker create(long paramLong, MlltFrame paramMlltFrame)
  {
    int j = bytesDeviations.length;
    int i = j + 1;
    long[] arrayOfLong1 = new long[i];
    long[] arrayOfLong2 = new long[i];
    arrayOfLong1[0] = paramLong;
    long l2 = 0L;
    arrayOfLong2[0] = 0L;
    i = 1;
    long l1 = paramLong;
    paramLong = l2;
    while (i <= j)
    {
      int k = bytesBetweenReference;
      int[] arrayOfInt = bytesDeviations;
      int m = i - 1;
      l1 += k + arrayOfInt[m];
      paramLong += millisecondsBetweenReference + millisecondsDeviations[m];
      arrayOfLong1[i] = l1;
      arrayOfLong2[i] = paramLong;
      i += 1;
    }
    return new MlltSeeker(arrayOfLong1, arrayOfLong2);
  }
  
  private static Pair linearlyInterpolate(long paramLong, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    int i = Util.binarySearchFloor(paramArrayOfLong1, paramLong, true, true);
    long l1 = paramArrayOfLong1[i];
    long l2 = paramArrayOfLong2[i];
    i += 1;
    if (i == paramArrayOfLong1.length) {
      return Pair.create(Long.valueOf(l1), Long.valueOf(l2));
    }
    long l3 = paramArrayOfLong1[i];
    long l4 = paramArrayOfLong2[i];
    double d;
    if (l3 == l1) {
      d = 0.0D;
    } else {
      d = (paramLong - l1) / (l3 - l1);
    }
    return Pair.create(Long.valueOf(paramLong), Long.valueOf((d * (l4 - l2)) + l2));
  }
  
  public long getDataEndPosition()
  {
    return -1L;
  }
  
  public long getDurationUs()
  {
    return durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    Pair localPair = linearlyInterpolate(IpAddress.usToMs(Util.constrainValue(paramLong, 0L, durationUs)), referenceTimesMs, referencePositions);
    return new SeekMap.SeekPoints(new SeekPoint(IpAddress.msToUs(((Long)first).longValue()), ((Long)second).longValue()));
  }
  
  public long getTimeUs(long paramLong)
  {
    return IpAddress.msToUs(((Long)linearlyInterpolatereferencePositions, referenceTimesMs).second).longValue());
  }
  
  public boolean isSeekable()
  {
    return true;
  }
}
