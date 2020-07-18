package com.google.android.exoplayer2.source;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline
  extends Timeline
{
  private static final Object enclosureUrl = new Object();
  @Nullable
  private final Object _data;
  private final boolean isDynamic;
  private final boolean isSeekable;
  private final long periodDurationUs;
  private final long presentationStartTimeMs;
  private final long windowDefaultStartPositionUs;
  private final long windowDurationUs;
  private final long windowPositionInPeriodUs;
  private final long windowStartTimeMs;
  
  public SinglePeriodTimeline(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
  {
    presentationStartTimeMs = paramLong1;
    windowStartTimeMs = paramLong2;
    periodDurationUs = paramLong3;
    windowDurationUs = paramLong4;
    windowPositionInPeriodUs = paramLong5;
    windowDefaultStartPositionUs = paramLong6;
    isSeekable = paramBoolean1;
    isDynamic = paramBoolean2;
    _data = paramObject;
  }
  
  public SinglePeriodTimeline(long paramLong1, long paramLong2, long paramLong3, long paramLong4, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
  {
    this(-9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3, paramLong4, paramBoolean1, paramBoolean2, paramObject);
  }
  
  public SinglePeriodTimeline(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramLong, paramBoolean1, paramBoolean2, null);
  }
  
  public SinglePeriodTimeline(long paramLong, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
  {
    this(paramLong, paramLong, 0L, 0L, paramBoolean1, paramBoolean2, paramObject);
  }
  
  public int getIndexOfPeriod(Object paramObject)
  {
    if (enclosureUrl.equals(paramObject)) {
      return 0;
    }
    return -1;
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    if (paramBoolean) {}
    for (Object localObject = enclosureUrl;; localObject = null) {
      break;
    }
    return paramPeriod.get(null, localObject, 0, periodDurationUs, -windowPositionInPeriodUs);
  }
  
  public int getPeriodCount()
  {
    return 1;
  }
  
  public Object getUidOfPeriod(int paramInt)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    return enclosureUrl;
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    if (paramBoolean) {}
    for (Object localObject = _data;; localObject = null) {
      break;
    }
    long l2 = windowDefaultStartPositionUs;
    long l1 = l2;
    if (isDynamic)
    {
      l1 = l2;
      if (paramLong != 0L)
      {
        if (windowDurationUs == -9223372036854775807L) {}
        do
        {
          l1 = -9223372036854775807L;
          break;
          paramLong = l2 + paramLong;
          l1 = paramLong;
        } while (paramLong > windowDurationUs);
      }
    }
    return paramWindow.readInt(localObject, presentationStartTimeMs, windowStartTimeMs, isSeekable, isDynamic, l1, windowDurationUs, 0, 0, windowPositionInPeriodUs);
  }
  
  public int getWindowCount()
  {
    return 1;
  }
}
