package com.google.android.exoplayer2.analytics;

import android.view.Surface;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upgrade.DefaultDrmSessionEventListener;
import com.google.android.exoplayer2.upstream.BandwidthMeter.EventListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AnalyticsCollector
  implements Player.EventListener, MetadataOutput, AudioRendererEventListener, VideoRendererEventListener, MediaSourceEventListener, BandwidthMeter.EventListener, DefaultDrmSessionEventListener, VideoListener, AudioListener
{
  private final Clock clock;
  private final CopyOnWriteArraySet<AnalyticsListener> listeners;
  private final MediaPeriodQueueTracker mediaPeriodQueueTracker;
  private Player player;
  private final Timeline.Window window;
  
  protected AnalyticsCollector(Player paramPlayer, Clock paramClock)
  {
    if (paramPlayer != null) {
      player = paramPlayer;
    }
    clock = ((Clock)Assertions.checkNotNull(paramClock));
    listeners = new CopyOnWriteArraySet();
    mediaPeriodQueueTracker = new MediaPeriodQueueTracker();
    window = new Timeline.Window();
  }
  
  private AnalyticsListener.EventTime generateEventTime(MediaPeriodInfo paramMediaPeriodInfo)
  {
    Assertions.checkNotNull(player);
    Object localObject = paramMediaPeriodInfo;
    if (paramMediaPeriodInfo == null)
    {
      int j = player.getCurrentWindowIndex();
      paramMediaPeriodInfo = mediaPeriodQueueTracker.tryResolveWindowIndex(j);
      localObject = paramMediaPeriodInfo;
      if (paramMediaPeriodInfo == null)
      {
        localObject = player.getCurrentTimeline();
        paramMediaPeriodInfo = (MediaPeriodInfo)localObject;
        int i;
        if (j < ((Timeline)localObject).getWindowCount()) {
          i = 1;
        } else {
          i = 0;
        }
        if (i == 0) {
          paramMediaPeriodInfo = Timeline.EMPTY;
        }
        return generateEventTime(paramMediaPeriodInfo, j, null);
      }
    }
    return generateEventTime(timeline, windowIndex, mediaPeriodId);
  }
  
  private AnalyticsListener.EventTime generateLastReportedPlayingMediaPeriodEventTime()
  {
    return generateEventTime(mediaPeriodQueueTracker.getLastReportedPlayingMediaPeriod());
  }
  
  private AnalyticsListener.EventTime generateLoadingMediaPeriodEventTime()
  {
    return generateEventTime(mediaPeriodQueueTracker.getLoadingMediaPeriod());
  }
  
  private AnalyticsListener.EventTime generateMediaPeriodEventTime(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    Assertions.checkNotNull(player);
    if (paramMediaPeriodId != null)
    {
      localObject = mediaPeriodQueueTracker.getMediaPeriodInfo(paramMediaPeriodId);
      if (localObject != null) {
        return generateEventTime((MediaPeriodInfo)localObject);
      }
      return generateEventTime(Timeline.EMPTY, paramInt, paramMediaPeriodId);
    }
    Object localObject = player.getCurrentTimeline();
    paramMediaPeriodId = (MediaSource.MediaPeriodId)localObject;
    int i;
    if (paramInt < ((Timeline)localObject).getWindowCount()) {
      i = 1;
    } else {
      i = 0;
    }
    if (i == 0) {
      paramMediaPeriodId = Timeline.EMPTY;
    }
    return generateEventTime(paramMediaPeriodId, paramInt, null);
  }
  
  private AnalyticsListener.EventTime generatePlayingMediaPeriodEventTime()
  {
    return generateEventTime(mediaPeriodQueueTracker.getPlayingMediaPeriod());
  }
  
  private AnalyticsListener.EventTime generateReadingMediaPeriodEventTime()
  {
    return generateEventTime(mediaPeriodQueueTracker.getReadingMediaPeriod());
  }
  
  public void addListener(AnalyticsListener paramAnalyticsListener)
  {
    listeners.add(paramAnalyticsListener);
  }
  
  protected AnalyticsListener.EventTime generateEventTime(Timeline paramTimeline, int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    if (paramTimeline.isEmpty()) {
      paramMediaPeriodId = null;
    }
    long l2 = clock.elapsedRealtime();
    Timeline localTimeline = player.getCurrentTimeline();
    int j = 1;
    int i;
    if ((paramTimeline == localTimeline) && (paramInt == player.getCurrentWindowIndex())) {
      i = 1;
    } else {
      i = 0;
    }
    long l1 = 0L;
    if ((paramMediaPeriodId != null) && (paramMediaPeriodId.isAd()))
    {
      if ((i != 0) && (player.getCurrentAdGroupIndex() == adGroupIndex) && (player.getCurrentAdIndexInAdGroup() == adIndexInAdGroup)) {
        i = j;
      } else {
        i = 0;
      }
      if (i != 0) {
        l1 = player.getCurrentPosition();
      }
    }
    else if (i != 0)
    {
      l1 = player.getContentPosition();
    }
    else if (!paramTimeline.isEmpty())
    {
      l1 = paramTimeline.getWindow(paramInt, window).getDefaultPositionMs();
    }
    return new AnalyticsListener.EventTime(l2, paramTimeline, paramInt, paramMediaPeriodId, l1, player.getCurrentPosition(), player.getTotalBufferedDuration());
  }
  
  protected Set getListeners()
  {
    return Collections.unmodifiableSet(listeners);
  }
  
  public final void notifySeekStarted()
  {
    if (!mediaPeriodQueueTracker.isSeeking())
    {
      AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
      mediaPeriodQueueTracker.onSeekStarted();
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext()) {
        ((AnalyticsListener)localIterator.next()).onSeekStarted(localEventTime);
      }
    }
  }
  
  public void onAudioAttributesChanged(AudioAttributes paramAudioAttributes)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onAudioAttributesChanged(localEventTime, paramAudioAttributes);
    }
  }
  
  public final void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderInitialized(localEventTime, 1, paramString, paramLong2);
    }
  }
  
  public final void onAudioDisabled(DecoderCounters paramDecoderCounters)
  {
    AnalyticsListener.EventTime localEventTime = generateLastReportedPlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderDisabled(localEventTime, 1, paramDecoderCounters);
    }
  }
  
  public final void onAudioEnabled(DecoderCounters paramDecoderCounters)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderEnabled(localEventTime, 1, paramDecoderCounters);
    }
  }
  
  public final void onAudioInputFormatChanged(Format paramFormat)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderInputFormatChanged(localEventTime, 1, paramFormat);
    }
  }
  
  public final void onAudioSessionId(int paramInt)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onAudioSessionId(localEventTime, paramInt);
    }
  }
  
  public final void onAudioSinkUnderrun(int paramInt, long paramLong1, long paramLong2)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onAudioUnderrun(localEventTime, paramInt, paramLong1, paramLong2);
    }
  }
  
  public final void onBandwidthSample(int paramInt, long paramLong1, long paramLong2)
  {
    AnalyticsListener.EventTime localEventTime = generateLoadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onBandwidthEstimate(localEventTime, paramInt, paramLong1, paramLong2);
    }
  }
  
  public final void onDownstreamFormatChanged(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
  {
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDownstreamFormatChanged(paramMediaPeriodId, paramMediaLoadData);
    }
  }
  
  public final void onDrmKeysLoaded()
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDrmKeysLoaded(localEventTime);
    }
  }
  
  public final void onDrmKeysRemoved()
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDrmKeysRemoved(localEventTime);
    }
  }
  
  public final void onDrmKeysRestored()
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDrmKeysRestored(localEventTime);
    }
  }
  
  public final void onDrmSessionAcquired()
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDrmSessionAcquired(localEventTime);
    }
  }
  
  public final void onDrmSessionManagerError(Exception paramException)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDrmSessionManagerError(localEventTime, paramException);
    }
  }
  
  public final void onDrmSessionReleased()
  {
    AnalyticsListener.EventTime localEventTime = generateLastReportedPlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDrmSessionReleased(localEventTime);
    }
  }
  
  public final void onDroppedFrames(int paramInt, long paramLong)
  {
    AnalyticsListener.EventTime localEventTime = generateLastReportedPlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDroppedVideoFrames(localEventTime, paramInt, paramLong);
    }
  }
  
  public final void onLoadCanceled(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
  {
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onLoadCanceled(paramMediaPeriodId, paramLoadEventInfo, paramMediaLoadData);
    }
  }
  
  public final void onLoadCompleted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
  {
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onLoadCompleted(paramMediaPeriodId, paramLoadEventInfo, paramMediaLoadData);
    }
  }
  
  public final void onLoadError(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData, IOException paramIOException, boolean paramBoolean)
  {
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onLoadError(paramMediaPeriodId, paramLoadEventInfo, paramMediaLoadData, paramIOException, paramBoolean);
    }
  }
  
  public final void onLoadStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
  {
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onLoadStarted(paramMediaPeriodId, paramLoadEventInfo, paramMediaLoadData);
    }
  }
  
  public final void onLoadingChanged(boolean paramBoolean)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onLoadingChanged(localEventTime, paramBoolean);
    }
  }
  
  public final void onMediaPeriodCreated(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    mediaPeriodQueueTracker.onMediaPeriodCreated(paramInt, paramMediaPeriodId);
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onMediaPeriodCreated(paramMediaPeriodId);
    }
  }
  
  public final void onMediaPeriodReleased(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    AnalyticsListener.EventTime localEventTime = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    if (mediaPeriodQueueTracker.onMediaPeriodReleased(paramMediaPeriodId))
    {
      paramMediaPeriodId = listeners.iterator();
      while (paramMediaPeriodId.hasNext()) {
        ((AnalyticsListener)paramMediaPeriodId.next()).onMediaPeriodReleased(localEventTime);
      }
    }
  }
  
  public final void onMetadata(Metadata paramMetadata)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onMetadata(localEventTime, paramMetadata);
    }
  }
  
  public final void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onPlaybackParametersChanged(localEventTime, paramPlaybackParameters);
    }
  }
  
  public final void onPlayerError(ExoPlaybackException paramExoPlaybackException)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onPlayerError(localEventTime, paramExoPlaybackException);
    }
  }
  
  public final void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onPlayerStateChanged(localEventTime, paramBoolean, paramInt);
    }
  }
  
  public final void onPositionDiscontinuity(int paramInt)
  {
    mediaPeriodQueueTracker.onPositionDiscontinuity(paramInt);
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onPositionDiscontinuity(localEventTime, paramInt);
    }
  }
  
  public final void onReadingStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    mediaPeriodQueueTracker.onReadingStarted(paramMediaPeriodId);
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onReadingStarted(paramMediaPeriodId);
    }
  }
  
  public final void onRenderedFirstFrame() {}
  
  public final void onRenderedFirstFrame(Surface paramSurface)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onRenderedFirstFrame(localEventTime, paramSurface);
    }
  }
  
  public final void onRepeatModeChanged(int paramInt)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onRepeatModeChanged(localEventTime, paramInt);
    }
  }
  
  public final void onSeekProcessed()
  {
    if (mediaPeriodQueueTracker.isSeeking())
    {
      mediaPeriodQueueTracker.onSeekProcessed();
      AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext()) {
        ((AnalyticsListener)localIterator.next()).onSeekProcessed(localEventTime);
      }
    }
  }
  
  public final void onShuffleModeEnabledChanged(boolean paramBoolean)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onShuffleModeChanged(localEventTime, paramBoolean);
    }
  }
  
  public void onSurfaceSizeChanged(int paramInt1, int paramInt2)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onSurfaceSizeChanged(localEventTime, paramInt1, paramInt2);
    }
  }
  
  public final void onTimelineChanged(Timeline paramTimeline, Object paramObject, int paramInt)
  {
    mediaPeriodQueueTracker.onTimelineChanged(paramTimeline);
    paramTimeline = generatePlayingMediaPeriodEventTime();
    paramObject = listeners.iterator();
    while (paramObject.hasNext()) {
      ((AnalyticsListener)paramObject.next()).onTimelineChanged(paramTimeline, paramInt);
    }
  }
  
  public final void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onTracksChanged(localEventTime, paramTrackGroupArray, paramTrackSelectionArray);
    }
  }
  
  public final void onUpstreamDiscarded(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
  {
    paramMediaPeriodId = generateMediaPeriodEventTime(paramInt, paramMediaPeriodId);
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onUpstreamDiscarded(paramMediaPeriodId, paramMediaLoadData);
    }
  }
  
  public final void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderInitialized(localEventTime, 2, paramString, paramLong2);
    }
  }
  
  public final void onVideoDisabled(DecoderCounters paramDecoderCounters)
  {
    AnalyticsListener.EventTime localEventTime = generateLastReportedPlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderDisabled(localEventTime, 2, paramDecoderCounters);
    }
  }
  
  public final void onVideoEnabled(DecoderCounters paramDecoderCounters)
  {
    AnalyticsListener.EventTime localEventTime = generatePlayingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderEnabled(localEventTime, 2, paramDecoderCounters);
    }
  }
  
  public final void onVideoInputFormatChanged(Format paramFormat)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onDecoderInputFormatChanged(localEventTime, 2, paramFormat);
    }
  }
  
  public final void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onVideoSizeChanged(localEventTime, paramInt1, paramInt2, paramInt3, paramFloat);
    }
  }
  
  public void onVolumeChanged(float paramFloat)
  {
    AnalyticsListener.EventTime localEventTime = generateReadingMediaPeriodEventTime();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((AnalyticsListener)localIterator.next()).onVolumeChanged(localEventTime, paramFloat);
    }
  }
  
  public void removeListener(AnalyticsListener paramAnalyticsListener)
  {
    listeners.remove(paramAnalyticsListener);
  }
  
  public final void resetForNewMediaSource()
  {
    Iterator localIterator = new ArrayList(mediaPeriodQueueTracker.mediaPeriodInfoQueue).iterator();
    while (localIterator.hasNext())
    {
      MediaPeriodInfo localMediaPeriodInfo = (MediaPeriodInfo)localIterator.next();
      onMediaPeriodReleased(windowIndex, mediaPeriodId);
    }
  }
  
  public void setPlayer(Player paramPlayer)
  {
    boolean bool;
    if (player == null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    player = ((Player)Assertions.checkNotNull(paramPlayer));
  }
  
  public static class Factory
  {
    public Factory() {}
    
    public AnalyticsCollector createAnalyticsCollector(Player paramPlayer, Clock paramClock)
    {
      return new AnalyticsCollector(paramPlayer, paramClock);
    }
  }
  
  private static final class MediaPeriodInfo
  {
    public final MediaSource.MediaPeriodId mediaPeriodId;
    public final Timeline timeline;
    public final int windowIndex;
    
    public MediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, Timeline paramTimeline, int paramInt)
    {
      mediaPeriodId = paramMediaPeriodId;
      timeline = paramTimeline;
      windowIndex = paramInt;
    }
  }
  
  private static final class MediaPeriodQueueTracker
  {
    private boolean isSeeking;
    @Nullable
    private AnalyticsCollector.MediaPeriodInfo lastReportedPlayingMediaPeriod;
    private final HashMap<MediaSource.MediaPeriodId, AnalyticsCollector.MediaPeriodInfo> mediaPeriodIdToInfo = new HashMap();
    private final ArrayList<AnalyticsCollector.MediaPeriodInfo> mediaPeriodInfoQueue = new ArrayList();
    private final Timeline.Period period = new Timeline.Period();
    @Nullable
    private AnalyticsCollector.MediaPeriodInfo readingMediaPeriod;
    private Timeline timeline = Timeline.EMPTY;
    
    public MediaPeriodQueueTracker() {}
    
    private void updateLastReportedPlayingMediaPeriod()
    {
      if (!mediaPeriodInfoQueue.isEmpty()) {
        lastReportedPlayingMediaPeriod = ((AnalyticsCollector.MediaPeriodInfo)mediaPeriodInfoQueue.get(0));
      }
    }
    
    private AnalyticsCollector.MediaPeriodInfo updateMediaPeriodInfoToNewTimeline(AnalyticsCollector.MediaPeriodInfo paramMediaPeriodInfo, Timeline paramTimeline)
    {
      int i = paramTimeline.getIndexOfPeriod(mediaPeriodId.periodUid);
      if (i == -1) {
        return paramMediaPeriodInfo;
      }
      i = getPeriodperiod).windowIndex;
      return new AnalyticsCollector.MediaPeriodInfo(mediaPeriodId, paramTimeline, i);
    }
    
    public AnalyticsCollector.MediaPeriodInfo getLastReportedPlayingMediaPeriod()
    {
      return lastReportedPlayingMediaPeriod;
    }
    
    public AnalyticsCollector.MediaPeriodInfo getLoadingMediaPeriod()
    {
      if (mediaPeriodInfoQueue.isEmpty()) {
        return null;
      }
      return (AnalyticsCollector.MediaPeriodInfo)mediaPeriodInfoQueue.get(mediaPeriodInfoQueue.size() - 1);
    }
    
    public AnalyticsCollector.MediaPeriodInfo getMediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      return (AnalyticsCollector.MediaPeriodInfo)mediaPeriodIdToInfo.get(paramMediaPeriodId);
    }
    
    public AnalyticsCollector.MediaPeriodInfo getPlayingMediaPeriod()
    {
      if ((!mediaPeriodInfoQueue.isEmpty()) && (!timeline.isEmpty()) && (!isSeeking)) {
        return (AnalyticsCollector.MediaPeriodInfo)mediaPeriodInfoQueue.get(0);
      }
      return null;
    }
    
    public AnalyticsCollector.MediaPeriodInfo getReadingMediaPeriod()
    {
      return readingMediaPeriod;
    }
    
    public boolean isSeeking()
    {
      return isSeeking;
    }
    
    public void onMediaPeriodCreated(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      int i;
      if (timeline.getIndexOfPeriod(periodUid) != -1) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0) {
        localObject = timeline;
      } else {
        localObject = Timeline.EMPTY;
      }
      Object localObject = new AnalyticsCollector.MediaPeriodInfo(paramMediaPeriodId, (Timeline)localObject, paramInt);
      mediaPeriodInfoQueue.add(localObject);
      mediaPeriodIdToInfo.put(paramMediaPeriodId, localObject);
      if ((mediaPeriodInfoQueue.size() == 1) && (!timeline.isEmpty())) {
        updateLastReportedPlayingMediaPeriod();
      }
    }
    
    public boolean onMediaPeriodReleased(MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      AnalyticsCollector.MediaPeriodInfo localMediaPeriodInfo = (AnalyticsCollector.MediaPeriodInfo)mediaPeriodIdToInfo.remove(paramMediaPeriodId);
      if (localMediaPeriodInfo == null) {
        return false;
      }
      mediaPeriodInfoQueue.remove(localMediaPeriodInfo);
      if ((readingMediaPeriod != null) && (paramMediaPeriodId.equals(readingMediaPeriod.mediaPeriodId)))
      {
        if (mediaPeriodInfoQueue.isEmpty()) {
          paramMediaPeriodId = null;
        } else {
          paramMediaPeriodId = (AnalyticsCollector.MediaPeriodInfo)mediaPeriodInfoQueue.get(0);
        }
        readingMediaPeriod = paramMediaPeriodId;
      }
      return true;
    }
    
    public void onPositionDiscontinuity(int paramInt)
    {
      updateLastReportedPlayingMediaPeriod();
    }
    
    public void onReadingStarted(MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      readingMediaPeriod = ((AnalyticsCollector.MediaPeriodInfo)mediaPeriodIdToInfo.get(paramMediaPeriodId));
    }
    
    public void onSeekProcessed()
    {
      isSeeking = false;
      updateLastReportedPlayingMediaPeriod();
    }
    
    public void onSeekStarted()
    {
      isSeeking = true;
    }
    
    public void onTimelineChanged(Timeline paramTimeline)
    {
      int i = 0;
      while (i < mediaPeriodInfoQueue.size())
      {
        AnalyticsCollector.MediaPeriodInfo localMediaPeriodInfo = updateMediaPeriodInfoToNewTimeline((AnalyticsCollector.MediaPeriodInfo)mediaPeriodInfoQueue.get(i), paramTimeline);
        mediaPeriodInfoQueue.set(i, localMediaPeriodInfo);
        mediaPeriodIdToInfo.put(mediaPeriodId, localMediaPeriodInfo);
        i += 1;
      }
      if (readingMediaPeriod != null) {
        readingMediaPeriod = updateMediaPeriodInfoToNewTimeline(readingMediaPeriod, paramTimeline);
      }
      timeline = paramTimeline;
      updateLastReportedPlayingMediaPeriod();
    }
    
    public AnalyticsCollector.MediaPeriodInfo tryResolveWindowIndex(int paramInt)
    {
      int i = 0;
      Object localObject2;
      for (Object localObject1 = null; i < mediaPeriodInfoQueue.size(); localObject1 = localObject2)
      {
        AnalyticsCollector.MediaPeriodInfo localMediaPeriodInfo = (AnalyticsCollector.MediaPeriodInfo)mediaPeriodInfoQueue.get(i);
        int j = timeline.getIndexOfPeriod(mediaPeriodId.periodUid);
        localObject2 = localObject1;
        if (j != -1)
        {
          localObject2 = localObject1;
          if (timeline.getPeriod(j, period).windowIndex == paramInt)
          {
            if (localObject1 != null) {
              return null;
            }
            localObject2 = localMediaPeriodInfo;
          }
        }
        i += 1;
      }
      return localObject1;
    }
  }
}
