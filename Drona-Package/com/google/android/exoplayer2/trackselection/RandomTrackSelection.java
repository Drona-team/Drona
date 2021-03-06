package com.google.android.exoplayer2.trackselection;

import android.os.SystemClock;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;
import java.util.Random;

public final class RandomTrackSelection
  extends BaseTrackSelection
{
  private final Random random;
  private int selectedIndex;
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
  {
    super(paramTrackGroup, paramVarArgs);
    random = new Random();
    selectedIndex = random.nextInt(length);
  }
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, long paramLong)
  {
    this(paramTrackGroup, paramArrayOfInt, new Random(paramLong));
  }
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, Random paramRandom)
  {
    super(paramTrackGroup, paramArrayOfInt);
    random = paramRandom;
    selectedIndex = paramRandom.nextInt(length);
  }
  
  public int getSelectedIndex()
  {
    return selectedIndex;
  }
  
  public Object getSelectionData()
  {
    return null;
  }
  
  public int getSelectionReason()
  {
    return 3;
  }
  
  public void updateSelectedTrack(long paramLong1, long paramLong2, long paramLong3, List paramList, MediaChunkIterator[] paramArrayOfMediaChunkIterator)
  {
    paramLong1 = SystemClock.elapsedRealtime();
    int m = 0;
    int i = 0;
    int k;
    for (int j = 0; i < length; j = k)
    {
      k = j;
      if (!isBlacklisted(i, paramLong1)) {
        k = j + 1;
      }
      i += 1;
    }
    selectedIndex = random.nextInt(j);
    if (j != length)
    {
      j = 0;
      i = m;
      while (i < length)
      {
        k = j;
        if (!isBlacklisted(i, paramLong1))
        {
          if (selectedIndex == j)
          {
            selectedIndex = i;
            return;
          }
          k = j + 1;
        }
        i += 1;
        j = k;
      }
    }
  }
  
  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Random random;
    
    public Factory()
    {
      random = new Random();
    }
    
    public Factory(int paramInt)
    {
      random = new Random(paramInt);
    }
    
    public RandomTrackSelection createTrackSelection(TrackGroup paramTrackGroup, BandwidthMeter paramBandwidthMeter, int... paramVarArgs)
    {
      return new RandomTrackSelection(paramTrackGroup, paramVarArgs, random);
    }
  }
}
