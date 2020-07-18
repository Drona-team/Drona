package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;

public abstract class ForwardingTimeline
  extends Timeline
{
  protected final Timeline timeline;
  
  public ForwardingTimeline(Timeline paramTimeline)
  {
    timeline = paramTimeline;
  }
  
  public int getFirstWindowIndex(boolean paramBoolean)
  {
    return timeline.getFirstWindowIndex(paramBoolean);
  }
  
  public int getIndexOfPeriod(Object paramObject)
  {
    return timeline.getIndexOfPeriod(paramObject);
  }
  
  public int getLastWindowIndex(boolean paramBoolean)
  {
    return timeline.getLastWindowIndex(paramBoolean);
  }
  
  public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return timeline.getNextWindowIndex(paramInt1, paramInt2, paramBoolean);
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    return timeline.getPeriod(paramInt, paramPeriod, paramBoolean);
  }
  
  public int getPeriodCount()
  {
    return timeline.getPeriodCount();
  }
  
  public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return timeline.getPreviousWindowIndex(paramInt1, paramInt2, paramBoolean);
  }
  
  public Object getUidOfPeriod(int paramInt)
  {
    return timeline.getUidOfPeriod(paramInt);
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    return timeline.getWindow(paramInt, paramWindow, paramBoolean, paramLong);
  }
  
  public int getWindowCount()
  {
    return timeline.getWindowCount();
  }
}
