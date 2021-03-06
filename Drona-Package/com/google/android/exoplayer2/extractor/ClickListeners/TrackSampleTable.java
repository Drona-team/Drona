package com.google.android.exoplayer2.extractor.ClickListeners;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

final class TrackSampleTable
{
  public final long durationUs;
  public final int[] flags;
  public final int maximumSize;
  public final long[] offsets;
  public final int sampleCount;
  public final int[] sizes;
  public final long[] timestampsUs;
  public final Track track;
  
  public TrackSampleTable(Track paramTrack, long[] paramArrayOfLong1, int[] paramArrayOfInt1, int paramInt, long[] paramArrayOfLong2, int[] paramArrayOfInt2, long paramLong)
  {
    int i = paramArrayOfInt1.length;
    int j = paramArrayOfLong2.length;
    boolean bool2 = false;
    if (i == j) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    if (paramArrayOfLong1.length == paramArrayOfLong2.length) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramArrayOfInt2.length == paramArrayOfLong2.length) {
      bool1 = true;
    }
    Assertions.checkArgument(bool1);
    track = paramTrack;
    offsets = paramArrayOfLong1;
    sizes = paramArrayOfInt1;
    maximumSize = paramInt;
    timestampsUs = paramArrayOfLong2;
    flags = paramArrayOfInt2;
    durationUs = paramLong;
    sampleCount = paramArrayOfLong1.length;
  }
  
  public int getIndexOfEarlierOrEqualSynchronizationSample(long paramLong)
  {
    int i = Util.binarySearchFloor(timestampsUs, paramLong, true, false);
    while (i >= 0)
    {
      if ((flags[i] & 0x1) != 0) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  public int getIndexOfLaterOrEqualSynchronizationSample(long paramLong)
  {
    int i = Util.binarySearchCeil(timestampsUs, paramLong, true, false);
    while (i < timestampsUs.length)
    {
      if ((flags[i] & 0x1) != 0) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
}
