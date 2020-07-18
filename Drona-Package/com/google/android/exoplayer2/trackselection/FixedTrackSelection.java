package com.google.android.exoplayer2.trackselection;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import java.util.List;

public final class FixedTrackSelection
  extends BaseTrackSelection
{
  @Nullable
  private final Object data;
  private final int reason;
  
  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt)
  {
    this(paramTrackGroup, paramInt, 0, null);
  }
  
  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, Object paramObject)
  {
    super(paramTrackGroup, new int[] { paramInt1 });
    reason = paramInt2;
    data = paramObject;
  }
  
  public int getSelectedIndex()
  {
    return 0;
  }
  
  public Object getSelectionData()
  {
    return data;
  }
  
  public int getSelectionReason()
  {
    return reason;
  }
  
  public void updateSelectedTrack(long paramLong1, long paramLong2, long paramLong3, List paramList, MediaChunkIterator[] paramArrayOfMediaChunkIterator) {}
  
  @Deprecated
  public static final class Factory
    implements TrackSelection.Factory
  {
    @Nullable
    private final Object data;
    private final int reason;
    
    public Factory()
    {
      reason = 0;
      data = null;
    }
    
    public Factory(int paramInt, Object paramObject)
    {
      reason = paramInt;
      data = paramObject;
    }
    
    public FixedTrackSelection createTrackSelection(TrackGroup paramTrackGroup, BandwidthMeter paramBandwidthMeter, int... paramVarArgs)
    {
      int i = paramVarArgs.length;
      boolean bool = true;
      if (i != 1) {
        bool = false;
      }
      Assertions.checkArgument(bool);
      return new FixedTrackSelection(paramTrackGroup, paramVarArgs[0], reason, data);
    }
  }
}
