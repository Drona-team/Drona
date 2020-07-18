package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ChunkIndex
  implements SeekMap
{
  private final long durationUs;
  public final long[] durationsUs;
  public final int length;
  public final long[] offsets;
  public final int[] sizes;
  public final long[] timesUs;
  
  public ChunkIndex(int[] paramArrayOfInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
  {
    sizes = paramArrayOfInt;
    offsets = paramArrayOfLong1;
    durationsUs = paramArrayOfLong2;
    timesUs = paramArrayOfLong3;
    length = paramArrayOfInt.length;
    if (length > 0)
    {
      durationUs = (paramArrayOfLong2[(length - 1)] + paramArrayOfLong3[(length - 1)]);
      return;
    }
    durationUs = 0L;
  }
  
  public int getChunkIndex(long paramLong)
  {
    return Util.binarySearchFloor(timesUs, paramLong, true, true);
  }
  
  public long getDurationUs()
  {
    return durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    int i = getChunkIndex(paramLong);
    SeekPoint localSeekPoint = new SeekPoint(timesUs[i], offsets[i]);
    if ((timeUs < paramLong) && (i != length - 1))
    {
      long[] arrayOfLong = timesUs;
      i += 1;
      return new SeekMap.SeekPoints(localSeekPoint, new SeekPoint(arrayOfLong[i], offsets[i]));
    }
    return new SeekMap.SeekPoints(localSeekPoint);
  }
  
  public boolean isSeekable()
  {
    return true;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ChunkIndex(length=");
    localStringBuilder.append(length);
    localStringBuilder.append(", sizes=");
    localStringBuilder.append(Arrays.toString(sizes));
    localStringBuilder.append(", offsets=");
    localStringBuilder.append(Arrays.toString(offsets));
    localStringBuilder.append(", timeUs=");
    localStringBuilder.append(Arrays.toString(timesUs));
    localStringBuilder.append(", durationsUs=");
    localStringBuilder.append(Arrays.toString(durationsUs));
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
}
