package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;

final class MediaPeriodInfo
{
  public final MediaSource.MediaPeriodId anchor;
  public final long contentPositionUs;
  public final long durationUs;
  public final boolean isFinal;
  public final boolean isLastInTimelinePeriod;
  public final long startPositionUs;
  
  MediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean1, boolean paramBoolean2)
  {
    anchor = paramMediaPeriodId;
    startPositionUs = paramLong1;
    contentPositionUs = paramLong2;
    durationUs = paramLong3;
    isLastInTimelinePeriod = paramBoolean1;
    isFinal = paramBoolean2;
  }
  
  public MediaPeriodInfo copyWithStartPositionUs(long paramLong)
  {
    return new MediaPeriodInfo(anchor, paramLong, contentPositionUs, durationUs, isLastInTimelinePeriod, isFinal);
  }
}
