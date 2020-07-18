package com.google.android.exoplayer2.source;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public final class ClippingMediaSource
  extends CompositeMediaSource<Void>
{
  private final boolean allowDynamicClippingUpdates;
  private IllegalClippingException clippingError;
  private ClippingTimeline clippingTimeline;
  private final boolean enableInitialDiscontinuity;
  private final long endUs;
  @Nullable
  private Object manifest;
  private final ArrayList<ClippingMediaPeriod> mediaPeriods;
  private final MediaSource mediaSource;
  private long periodEndUs;
  private long periodStartUs;
  private final boolean relativeToDefaultPosition;
  private final long startUs;
  private final Timeline.Window window;
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong)
  {
    this(paramMediaSource, 0L, paramLong, true, false, true);
  }
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong1, long paramLong2)
  {
    this(paramMediaSource, paramLong1, paramLong2, true, false, false);
  }
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this(paramMediaSource, paramLong1, paramLong2, paramBoolean, false, false);
  }
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool;
    if (paramLong1 >= 0L) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    mediaSource = ((MediaSource)Assertions.checkNotNull(paramMediaSource));
    startUs = paramLong1;
    endUs = paramLong2;
    enableInitialDiscontinuity = paramBoolean1;
    allowDynamicClippingUpdates = paramBoolean2;
    relativeToDefaultPosition = paramBoolean3;
    mediaPeriods = new ArrayList();
    window = new Timeline.Window();
  }
  
  private void refreshClippedTimeline(Timeline paramTimeline)
  {
    Object localObject = window;
    int i = 0;
    paramTimeline.getWindow(0, (Timeline.Window)localObject);
    long l6 = window.getPositionInFirstPeriodUs();
    localObject = clippingTimeline;
    long l3 = Long.MIN_VALUE;
    long l2;
    long l1;
    if ((localObject != null) && (!mediaPeriods.isEmpty()) && (!allowDynamicClippingUpdates))
    {
      l2 = periodStartUs;
      if (endUs == Long.MIN_VALUE) {
        l1 = l3;
      } else {
        l1 = periodEndUs - l6;
      }
      l2 -= l6;
    }
    else
    {
      long l5 = startUs;
      long l4 = endUs;
      l1 = l4;
      l2 = l5;
      if (relativeToDefaultPosition)
      {
        l1 = window.getDefaultPositionUs();
        l2 = l5 + l1;
        l1 = l4 + l1;
      }
      periodStartUs = (l6 + l2);
      if (endUs != Long.MIN_VALUE) {
        l3 = l6 + l1;
      }
      periodEndUs = l3;
      int j = mediaPeriods.size();
      while (i < j)
      {
        ((ClippingMediaPeriod)mediaPeriods.get(i)).updateClipping(periodStartUs, periodEndUs);
        i += 1;
      }
    }
    try
    {
      paramTimeline = new ClippingTimeline(paramTimeline, l2, l1);
      clippingTimeline = paramTimeline;
      refreshSourceInfo(clippingTimeline, manifest);
      return;
    }
    catch (IllegalClippingException paramTimeline)
    {
      clippingError = paramTimeline;
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    paramMediaPeriodId = new ClippingMediaPeriod(mediaSource.createPeriod(paramMediaPeriodId, paramAllocator), enableInitialDiscontinuity, periodStartUs, periodEndUs);
    mediaPeriods.add(paramMediaPeriodId);
    return paramMediaPeriodId;
  }
  
  protected long getMediaTimeForChildMediaTime(Void paramVoid, long paramLong)
  {
    if (paramLong == -9223372036854775807L) {
      return -9223372036854775807L;
    }
    long l2 = IpAddress.usToMs(startUs);
    long l1 = Math.max(0L, paramLong - l2);
    paramLong = l1;
    if (endUs != Long.MIN_VALUE) {
      paramLong = Math.min(IpAddress.usToMs(endUs) - l2, l1);
    }
    return paramLong;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    if (clippingError == null)
    {
      super.maybeThrowSourceInfoRefreshError();
      return;
    }
    throw clippingError;
  }
  
  protected void onChildSourceInfoRefreshed(Void paramVoid, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    if (clippingError != null) {
      return;
    }
    manifest = paramObject;
    refreshClippedTimeline(paramTimeline);
  }
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    super.prepareSourceInternal(paramExoPlayer, paramBoolean, paramTransferListener);
    prepareChildSource(null, mediaSource);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    Assertions.checkState(mediaPeriods.remove(paramMediaPeriod));
    mediaSource.releasePeriod(mediaPeriod);
    if ((mediaPeriods.isEmpty()) && (!allowDynamicClippingUpdates)) {
      refreshClippedTimeline(clippingTimeline.timeline);
    }
  }
  
  public void releaseSourceInternal()
  {
    super.releaseSourceInternal();
    clippingError = null;
    clippingTimeline = null;
  }
  
  private static final class ClippingTimeline
    extends ForwardingTimeline
  {
    private final long durationUs;
    private final long endUs;
    private final boolean isDynamic;
    private final long startUs;
    
    public ClippingTimeline(Timeline paramTimeline, long paramLong1, long paramLong2)
      throws ClippingMediaSource.IllegalClippingException
    {
      super();
      int i = paramTimeline.getPeriodCount();
      boolean bool3 = false;
      if (i == 1)
      {
        paramTimeline = paramTimeline.getWindow(0, new Timeline.Window());
        long l = Math.max(0L, paramLong1);
        if (paramLong2 == Long.MIN_VALUE) {
          paramLong1 = durationUs;
        } else {
          paramLong1 = Math.max(0L, paramLong2);
        }
        paramLong2 = paramLong1;
        if (durationUs != -9223372036854775807L)
        {
          paramLong2 = paramLong1;
          if (paramLong1 > durationUs) {
            paramLong2 = durationUs;
          }
          if ((l != 0L) && (!isSeekable)) {
            throw new ClippingMediaSource.IllegalClippingException(1);
          }
          if (l > paramLong2) {
            throw new ClippingMediaSource.IllegalClippingException(2);
          }
        }
        startUs = l;
        endUs = paramLong2;
        boolean bool1 = paramLong2 < -9223372036854775807L;
        if (!bool1) {
          paramLong1 = -9223372036854775807L;
        } else {
          paramLong1 = paramLong2 - l;
        }
        durationUs = paramLong1;
        boolean bool2 = bool3;
        if (isDynamic) {
          if (bool1)
          {
            bool2 = bool3;
            if (durationUs != -9223372036854775807L)
            {
              bool2 = bool3;
              if (paramLong2 != durationUs) {}
            }
          }
          else
          {
            bool2 = true;
          }
        }
        isDynamic = bool2;
        return;
      }
      throw new ClippingMediaSource.IllegalClippingException(0);
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      timeline.getPeriod(0, paramPeriod, paramBoolean);
      long l2 = paramPeriod.getPositionInWindowUs() - startUs;
      long l1;
      if (durationUs == -9223372036854775807L) {
        l1 = -9223372036854775807L;
      } else {
        l1 = durationUs - l2;
      }
      return paramPeriod.get(index, element, 0, l1, l2);
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      timeline.getWindow(0, paramWindow, paramBoolean, 0L);
      positionInFirstPeriodUs += startUs;
      durationUs = durationUs;
      isDynamic = isDynamic;
      if (defaultPositionUs != -9223372036854775807L)
      {
        defaultPositionUs = Math.max(defaultPositionUs, startUs);
        if (endUs == -9223372036854775807L) {
          paramLong = defaultPositionUs;
        } else {
          paramLong = Math.min(defaultPositionUs, endUs);
        }
        defaultPositionUs = paramLong;
        defaultPositionUs -= startUs;
      }
      paramLong = IpAddress.usToMs(startUs);
      if (presentationStartTimeMs != -9223372036854775807L) {
        presentationStartTimeMs += paramLong;
      }
      if (windowStartTimeMs != -9223372036854775807L) {
        windowStartTimeMs += paramLong;
      }
      return paramWindow;
    }
  }
  
  public static final class IllegalClippingException
    extends IOException
  {
    public static final int REASON_INVALID_PERIOD_COUNT = 0;
    public static final int REASON_NOT_SEEKABLE_TO_START = 1;
    public static final int REASON_START_EXCEEDS_END = 2;
    public final int reason;
    
    public IllegalClippingException(int paramInt)
    {
      super();
      reason = paramInt;
    }
    
    private static String getReasonDescription(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "unknown";
      case 2: 
        return "start exceeds end";
      case 1: 
        return "not seekable to start";
      }
      return "invalid period count";
    }
    
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Reason {}
  }
}
