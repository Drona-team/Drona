package com.google.android.exoplayer2.source;

@Deprecated
public final class DynamicConcatenatingMediaSource
  extends ConcatenatingMediaSource
{
  public DynamicConcatenatingMediaSource()
  {
    super(new MediaSource[0]);
  }
  
  public DynamicConcatenatingMediaSource(boolean paramBoolean)
  {
    super(paramBoolean, new MediaSource[0]);
  }
  
  public DynamicConcatenatingMediaSource(boolean paramBoolean, ShuffleOrder paramShuffleOrder)
  {
    super(paramBoolean, paramShuffleOrder, new MediaSource[0]);
  }
}
