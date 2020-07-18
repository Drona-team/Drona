package com.google.android.exoplayer2.source.ClickListeners;

import androidx.annotation.VisibleForTesting;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.ForwardingTimeline;
import com.google.android.exoplayer2.util.Assertions;

@VisibleForTesting(otherwise=3)
public final class SinglePeriodAdTimeline
  extends ForwardingTimeline
{
  private final AdPlaybackState adPlaybackState;
  
  public SinglePeriodAdTimeline(Timeline paramTimeline, AdPlaybackState paramAdPlaybackState)
  {
    super(paramTimeline);
    int i = paramTimeline.getPeriodCount();
    boolean bool2 = false;
    if (i == 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkState(bool1);
    boolean bool1 = bool2;
    if (paramTimeline.getWindowCount() == 1) {
      bool1 = true;
    }
    Assertions.checkState(bool1);
    adPlaybackState = paramAdPlaybackState;
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    timeline.getPeriod(paramInt, paramPeriod, paramBoolean);
    paramPeriod.append(index, element, windowIndex, durationUs, paramPeriod.getPositionInWindowUs(), adPlaybackState);
    return paramPeriod;
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    paramWindow = super.getWindow(paramInt, paramWindow, paramBoolean, paramLong);
    if (durationUs == -9223372036854775807L) {
      durationUs = adPlaybackState.contentDurationUs;
    }
    return paramWindow;
  }
}
