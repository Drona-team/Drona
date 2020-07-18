package com.google.android.exoplayer2;

import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;

final class MediaPeriodQueue
{
  private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
  private int length;
  @Nullable
  private MediaPeriodHolder loading;
  private long nextWindowSequenceNumber;
  @Nullable
  private Object oldFrontPeriodUid;
  private long oldFrontPeriodWindowSequenceNumber;
  private final Timeline.Period period = new Timeline.Period();
  @Nullable
  private MediaPeriodHolder playing;
  @Nullable
  private MediaPeriodHolder reading;
  private int repeatMode;
  private boolean shuffleModeEnabled;
  private Timeline timeline = Timeline.EMPTY;
  private final Timeline.Window window = new Timeline.Window();
  
  public MediaPeriodQueue() {}
  
  private boolean canKeepMediaPeriodHolder(MediaPeriodHolder paramMediaPeriodHolder, MediaPeriodInfo paramMediaPeriodInfo)
  {
    paramMediaPeriodHolder = info;
    return (startPositionUs == startPositionUs) && (anchor.equals(anchor));
  }
  
  private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo paramPlaybackInfo)
  {
    return getMediaPeriodInfo(periodId, contentPositionUs, startPositionUs);
  }
  
  private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodHolder paramMediaPeriodHolder, long paramLong)
  {
    Object localObject2 = info;
    long l2 = paramMediaPeriodHolder.getRendererOffset() + durationUs - paramLong;
    boolean bool = isLastInTimelinePeriod;
    long l1 = 0L;
    int j;
    Object localObject1;
    if (bool)
    {
      i = timeline.getIndexOfPeriod(anchor.periodUid);
      i = timeline.getNextPeriodIndex(i, period, window, repeatMode, shuffleModeEnabled);
      if (i == -1) {
        return null;
      }
      j = timeline.getPeriod(i, period, true).windowIndex;
      localObject1 = period.element;
      paramLong = anchor.windowSequenceNumber;
      if (timeline.getWindow(j, window).firstPeriodIndex == i)
      {
        localObject2 = timeline.getPeriodPosition(window, period, j, -9223372036854775807L, Math.max(0L, l2));
        if (localObject2 == null) {
          return null;
        }
        localObject1 = first;
        l1 = ((Long)second).longValue();
        if ((next != null) && (next.target.equals(localObject1)))
        {
          paramLong = next.info.anchor.windowSequenceNumber;
        }
        else
        {
          paramLong = nextWindowSequenceNumber;
          nextWindowSequenceNumber = (1L + paramLong);
        }
        paramMediaPeriodHolder = (MediaPeriodHolder)localObject1;
      }
      else
      {
        paramMediaPeriodHolder = (MediaPeriodHolder)localObject1;
      }
      return getMediaPeriodInfo(resolveMediaPeriodIdForAds(paramMediaPeriodHolder, l1, paramLong), l1, l1);
    }
    paramMediaPeriodHolder = anchor;
    timeline.getPeriodByUid(periodUid, period);
    if (paramMediaPeriodHolder.isAd())
    {
      i = adGroupIndex;
      j = period.getAdCountInAdGroup(i);
      if (j == -1) {
        return null;
      }
      int k = period.getNextAdIndexToPlay(i, adIndexInAdGroup);
      if (k < j)
      {
        if (!period.isAdAvailable(i, k)) {
          return null;
        }
        return getMediaPeriodInfoForAd(periodUid, i, k, contentPositionUs, windowSequenceNumber);
      }
      paramLong = contentPositionUs;
      if ((period.getAdGroupCount() == 1) && (period.getAdGroupTimeUs(0) == 0L))
      {
        localObject1 = timeline.getPeriodPosition(window, period, period.windowIndex, -9223372036854775807L, Math.max(0L, l2));
        if (localObject1 == null) {
          return null;
        }
        paramLong = ((Long)second).longValue();
      }
      return getMediaPeriodInfoForContent(periodUid, paramLong, windowSequenceNumber);
    }
    if (anchor.endPositionUs != Long.MIN_VALUE)
    {
      i = period.getAdGroupIndexForPositionUs(anchor.endPositionUs);
      if (i == -1) {
        return getMediaPeriodInfoForContent(periodUid, anchor.endPositionUs, windowSequenceNumber);
      }
      j = period.getFirstAdIndexToPlay(i);
      if (!period.isAdAvailable(i, j)) {
        return null;
      }
      return getMediaPeriodInfoForAd(periodUid, i, j, anchor.endPositionUs, windowSequenceNumber);
    }
    int i = period.getAdGroupCount();
    if (i == 0) {
      return null;
    }
    i -= 1;
    if (period.getAdGroupTimeUs(i) == Long.MIN_VALUE)
    {
      if (period.hasPlayedAdGroup(i)) {
        return null;
      }
      j = period.getFirstAdIndexToPlay(i);
      if (!period.isAdAvailable(i, j)) {
        return null;
      }
      paramLong = period.getDurationUs();
      return getMediaPeriodInfoForAd(periodUid, i, j, paramLong, windowSequenceNumber);
    }
    return null;
  }
  
  private MediaPeriodInfo getMediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong1, long paramLong2)
  {
    timeline.getPeriodByUid(periodUid, period);
    if (paramMediaPeriodId.isAd())
    {
      if (!period.isAdAvailable(adGroupIndex, adIndexInAdGroup)) {
        return null;
      }
      return getMediaPeriodInfoForAd(periodUid, adGroupIndex, adIndexInAdGroup, paramLong1, windowSequenceNumber);
    }
    return getMediaPeriodInfoForContent(periodUid, paramLong2, windowSequenceNumber);
  }
  
  private MediaPeriodInfo getMediaPeriodInfoForAd(Object paramObject, int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    paramObject = new MediaSource.MediaPeriodId(paramObject, paramInt1, paramInt2, paramLong2);
    boolean bool1 = isLastInPeriod(paramObject);
    boolean bool2 = isLastInTimeline(paramObject, bool1);
    long l = timeline.getPeriodByUid(periodUid, period).getAdDurationUs(adGroupIndex, adIndexInAdGroup);
    if (paramInt2 == period.getFirstAdIndexToPlay(paramInt1)) {}
    for (paramLong2 = period.getAdResumePositionUs();; paramLong2 = 0L) {
      break;
    }
    return new MediaPeriodInfo(paramObject, paramLong2, paramLong1, l, bool1, bool2);
  }
  
  private MediaPeriodInfo getMediaPeriodInfoForContent(Object paramObject, long paramLong1, long paramLong2)
  {
    int i = period.getAdGroupIndexAfterPositionUs(paramLong1);
    long l;
    if (i == -1) {
      l = Long.MIN_VALUE;
    } else {
      l = period.getAdGroupTimeUs(i);
    }
    paramObject = new MediaSource.MediaPeriodId(paramObject, paramLong2, l);
    timeline.getPeriodByUid(periodUid, period);
    boolean bool1 = isLastInPeriod(paramObject);
    boolean bool2 = isLastInTimeline(paramObject, bool1);
    if (l == Long.MIN_VALUE) {
      l = period.getDurationUs();
    }
    return new MediaPeriodInfo(paramObject, paramLong1, -9223372036854775807L, l, bool1, bool2);
  }
  
  private boolean isLastInPeriod(MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    int i = timeline.getPeriodByUid(periodUid, period).getAdGroupCount();
    if (i == 0) {
      return true;
    }
    int j = i - 1;
    boolean bool = paramMediaPeriodId.isAd();
    if (period.getAdGroupTimeUs(j) != Long.MIN_VALUE) {
      return (!bool) && (endPositionUs == Long.MIN_VALUE);
    }
    int k = period.getAdCountInAdGroup(j);
    if (k == -1) {
      return false;
    }
    if ((bool) && (adGroupIndex == j) && (adIndexInAdGroup == k - 1)) {
      i = 1;
    } else {
      i = 0;
    }
    if (i == 0) {
      return (!bool) && (period.getFirstAdIndexToPlay(j) == k);
    }
    return true;
  }
  
  private boolean isLastInTimeline(MediaSource.MediaPeriodId paramMediaPeriodId, boolean paramBoolean)
  {
    int i = timeline.getIndexOfPeriod(periodUid);
    int j = timeline.getPeriod(i, period).windowIndex;
    return (!timeline.getWindow(j, window).isDynamic) && (timeline.isLastPeriod(i, period, window, repeatMode, shuffleModeEnabled)) && (paramBoolean);
  }
  
  private MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(Object paramObject, long paramLong1, long paramLong2)
  {
    timeline.getPeriodByUid(paramObject, period);
    int i = period.getAdGroupIndexForPositionUs(paramLong1);
    if (i == -1)
    {
      i = period.getAdGroupIndexAfterPositionUs(paramLong1);
      if (i == -1) {}
      for (paramLong1 = Long.MIN_VALUE;; paramLong1 = period.getAdGroupTimeUs(i)) {
        break;
      }
      return new MediaSource.MediaPeriodId(paramObject, paramLong2, paramLong1);
    }
    return new MediaSource.MediaPeriodId(paramObject, i, period.getFirstAdIndexToPlay(i), paramLong2);
  }
  
  private long resolvePeriodIndexToWindowSequenceNumber(Object paramObject)
  {
    Object localObject2 = timeline;
    Object localObject1 = this;
    int i = getPeriodByUidperiod).windowIndex;
    Timeline localTimeline;
    int j;
    if (oldFrontPeriodUid != null)
    {
      localTimeline = timeline;
      localObject2 = localObject1;
      j = localTimeline.getIndexOfPeriod(oldFrontPeriodUid);
      if ((j != -1) && (timeline.getPeriod(j, period).windowIndex == i)) {
        return oldFrontPeriodWindowSequenceNumber;
      }
    }
    localObject1 = this;
    for (localObject2 = ((MediaPeriodQueue)localObject1).getFrontPeriod(); localObject2 != null; localObject2 = next) {
      if (target.equals(paramObject)) {
        return info.anchor.windowSequenceNumber;
      }
    }
    localObject2 = ((MediaPeriodQueue)localObject1).getFrontPeriod();
    paramObject = localObject1;
    for (localObject1 = localObject2; localObject1 != null; localObject1 = next)
    {
      localTimeline = timeline;
      localObject2 = paramObject;
      j = localTimeline.getIndexOfPeriod(target);
      if ((j != -1) && (timeline.getPeriod(j, period).windowIndex == i)) {
        return info.anchor.windowSequenceNumber;
      }
    }
    long l = nextWindowSequenceNumber;
    nextWindowSequenceNumber = (1L + l);
    return l;
  }
  
  private boolean updateForPlaybackModeChange()
  {
    MediaPeriodHolder localMediaPeriodHolder2 = getFrontPeriod();
    MediaPeriodHolder localMediaPeriodHolder1 = localMediaPeriodHolder2;
    if (localMediaPeriodHolder2 == null) {
      return true;
    }
    int i = timeline.getIndexOfPeriod(target);
    for (;;)
    {
      int j = timeline.getNextPeriodIndex(i, period, window, repeatMode, shuffleModeEnabled);
      i = j;
      while ((next != null) && (!info.isLastInTimelinePeriod)) {
        localMediaPeriodHolder1 = next;
      }
      if ((j == -1) || (next == null) || (timeline.getIndexOfPeriod(next.target) != j)) {
        break;
      }
      localMediaPeriodHolder1 = next;
    }
    boolean bool = removeAfter(localMediaPeriodHolder1);
    info = getUpdatedMediaPeriodInfo(info);
    if (bool) {
      return !hasPlayingPeriod();
    }
    return true;
  }
  
  public MediaPeriodHolder advancePlayingPeriod()
  {
    if (playing != null)
    {
      if (playing == reading) {
        reading = playing.next;
      }
      playing.release();
      length -= 1;
      if (length == 0)
      {
        loading = null;
        oldFrontPeriodUid = playing.target;
        oldFrontPeriodWindowSequenceNumber = playing.info.anchor.windowSequenceNumber;
      }
      playing = playing.next;
    }
    else
    {
      playing = loading;
      reading = loading;
    }
    return playing;
  }
  
  public MediaPeriodHolder advanceReadingPeriod()
  {
    boolean bool;
    if ((reading != null) && (reading.next != null)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    reading = reading.next;
    return reading;
  }
  
  public void clear(boolean paramBoolean)
  {
    MediaPeriodHolder localMediaPeriodHolder = getFrontPeriod();
    if (localMediaPeriodHolder != null)
    {
      Object localObject;
      if (paramBoolean) {
        localObject = target;
      } else {
        localObject = null;
      }
      oldFrontPeriodUid = localObject;
      oldFrontPeriodWindowSequenceNumber = info.anchor.windowSequenceNumber;
      localMediaPeriodHolder.release();
      removeAfter(localMediaPeriodHolder);
    }
    else if (!paramBoolean)
    {
      oldFrontPeriodUid = null;
    }
    playing = null;
    loading = null;
    reading = null;
    length = 0;
  }
  
  public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackSelector paramTrackSelector, Allocator paramAllocator, MediaSource paramMediaSource, MediaPeriodInfo paramMediaPeriodInfo)
  {
    if (loading == null) {}
    for (long l = startPositionUs;; l = loading.getRendererOffset() + loading.info.durationUs) {
      break;
    }
    paramArrayOfRendererCapabilities = new MediaPeriodHolder(paramArrayOfRendererCapabilities, l, paramTrackSelector, paramAllocator, paramMediaSource, paramMediaPeriodInfo);
    if (loading != null)
    {
      Assertions.checkState(hasPlayingPeriod());
      loading.next = paramArrayOfRendererCapabilities;
    }
    oldFrontPeriodUid = null;
    loading = paramArrayOfRendererCapabilities;
    length += 1;
    return mediaPeriod;
  }
  
  public MediaPeriodHolder getFrontPeriod()
  {
    if (hasPlayingPeriod()) {
      return playing;
    }
    return loading;
  }
  
  public MediaPeriodHolder getLoadingPeriod()
  {
    return loading;
  }
  
  public MediaPeriodInfo getNextMediaPeriodInfo(long paramLong, PlaybackInfo paramPlaybackInfo)
  {
    if (loading == null) {
      return getFirstMediaPeriodInfo(paramPlaybackInfo);
    }
    return getFollowingMediaPeriodInfo(loading, paramLong);
  }
  
  public MediaPeriodHolder getPlayingPeriod()
  {
    return playing;
  }
  
  public MediaPeriodHolder getReadingPeriod()
  {
    return reading;
  }
  
  public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo)
  {
    boolean bool1 = isLastInPeriod(anchor);
    boolean bool2 = isLastInTimeline(anchor, bool1);
    timeline.getPeriodByUid(anchor.periodUid, period);
    long l;
    if (anchor.isAd()) {
      l = period.getAdDurationUs(anchor.adGroupIndex, anchor.adIndexInAdGroup);
    }
    for (;;)
    {
      break;
      if (anchor.endPositionUs == Long.MIN_VALUE) {
        l = period.getDurationUs();
      } else {
        l = anchor.endPositionUs;
      }
    }
    return new MediaPeriodInfo(anchor, startPositionUs, contentPositionUs, l, bool1, bool2);
  }
  
  public boolean hasPlayingPeriod()
  {
    return playing != null;
  }
  
  public boolean isLoading(MediaPeriod paramMediaPeriod)
  {
    return (loading != null) && (loading.mediaPeriod == paramMediaPeriod);
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    if (loading != null) {
      loading.reevaluateBuffer(paramLong);
    }
  }
  
  public boolean removeAfter(MediaPeriodHolder paramMediaPeriodHolder)
  {
    boolean bool2 = false;
    if (paramMediaPeriodHolder != null) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkState(bool1);
    loading = paramMediaPeriodHolder;
    boolean bool1 = bool2;
    while (next != null)
    {
      paramMediaPeriodHolder = next;
      if (paramMediaPeriodHolder == reading)
      {
        reading = playing;
        bool1 = true;
      }
      paramMediaPeriodHolder.release();
      length -= 1;
    }
    loading.next = null;
    return bool1;
  }
  
  public MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(Object paramObject, long paramLong)
  {
    return resolveMediaPeriodIdForAds(paramObject, paramLong, resolvePeriodIndexToWindowSequenceNumber(paramObject));
  }
  
  public void setTimeline(Timeline paramTimeline)
  {
    timeline = paramTimeline;
  }
  
  public boolean shouldLoadNextMediaPeriod()
  {
    return (loading == null) || ((!loading.info.isFinal) && (loading.isFullyBuffered()) && (loading.info.durationUs != -9223372036854775807L) && (length < 100));
  }
  
  public boolean updateQueuedPeriods(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
  {
    int i = timeline.getIndexOfPeriod(periodUid);
    Object localObject = null;
    paramMediaPeriodId = getFrontPeriod();
    while (paramMediaPeriodId != null)
    {
      if (localObject == null)
      {
        info = getUpdatedMediaPeriodInfo(info);
      }
      else
      {
        if ((i == -1) || (!target.equals(timeline.getUidOfPeriod(i)))) {
          break label183;
        }
        MediaPeriodInfo localMediaPeriodInfo = getFollowingMediaPeriodInfo((MediaPeriodHolder)localObject, paramLong);
        if (localMediaPeriodInfo == null) {
          return removeAfter((MediaPeriodHolder)localObject) ^ true;
        }
        info = getUpdatedMediaPeriodInfo(info);
        if (!canKeepMediaPeriodHolder(paramMediaPeriodId, localMediaPeriodInfo)) {
          return removeAfter((MediaPeriodHolder)localObject) ^ true;
        }
      }
      int j = i;
      if (info.isLastInTimelinePeriod) {
        j = timeline.getNextPeriodIndex(i, period, window, repeatMode, shuffleModeEnabled);
      }
      localObject = paramMediaPeriodId;
      paramMediaPeriodId = next;
      i = j;
      continue;
      label183:
      return removeAfter((MediaPeriodHolder)localObject) ^ true;
    }
    return true;
  }
  
  public boolean updateRepeatMode(int paramInt)
  {
    repeatMode = paramInt;
    return updateForPlaybackModeChange();
  }
  
  public boolean updateShuffleModeEnabled(boolean paramBoolean)
  {
    shuffleModeEnabled = paramBoolean;
    return updateForPlaybackModeChange();
  }
}
