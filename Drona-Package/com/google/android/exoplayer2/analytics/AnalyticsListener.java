package com.google.android.exoplayer2.analytics;

import android.view.Surface;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import java.io.IOException;

public abstract interface AnalyticsListener
{
  public abstract void onAudioAttributesChanged(EventTime paramEventTime, AudioAttributes paramAudioAttributes);
  
  public abstract void onAudioSessionId(EventTime paramEventTime, int paramInt);
  
  public abstract void onAudioUnderrun(EventTime paramEventTime, int paramInt, long paramLong1, long paramLong2);
  
  public abstract void onBandwidthEstimate(EventTime paramEventTime, int paramInt, long paramLong1, long paramLong2);
  
  public abstract void onDecoderDisabled(EventTime paramEventTime, int paramInt, DecoderCounters paramDecoderCounters);
  
  public abstract void onDecoderEnabled(EventTime paramEventTime, int paramInt, DecoderCounters paramDecoderCounters);
  
  public abstract void onDecoderInitialized(EventTime paramEventTime, int paramInt, String paramString, long paramLong);
  
  public abstract void onDecoderInputFormatChanged(EventTime paramEventTime, int paramInt, Format paramFormat);
  
  public abstract void onDownstreamFormatChanged(EventTime paramEventTime, MediaSourceEventListener.MediaLoadData paramMediaLoadData);
  
  public abstract void onDrmKeysLoaded(EventTime paramEventTime);
  
  public abstract void onDrmKeysRemoved(EventTime paramEventTime);
  
  public abstract void onDrmKeysRestored(EventTime paramEventTime);
  
  public abstract void onDrmSessionAcquired(EventTime paramEventTime);
  
  public abstract void onDrmSessionManagerError(EventTime paramEventTime, Exception paramException);
  
  public abstract void onDrmSessionReleased(EventTime paramEventTime);
  
  public abstract void onDroppedVideoFrames(EventTime paramEventTime, int paramInt, long paramLong);
  
  public abstract void onLoadCanceled(EventTime paramEventTime, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData);
  
  public abstract void onLoadCompleted(EventTime paramEventTime, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData);
  
  public abstract void onLoadError(EventTime paramEventTime, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData, IOException paramIOException, boolean paramBoolean);
  
  public abstract void onLoadStarted(EventTime paramEventTime, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData);
  
  public abstract void onLoadingChanged(EventTime paramEventTime, boolean paramBoolean);
  
  public abstract void onMediaPeriodCreated(EventTime paramEventTime);
  
  public abstract void onMediaPeriodReleased(EventTime paramEventTime);
  
  public abstract void onMetadata(EventTime paramEventTime, Metadata paramMetadata);
  
  public abstract void onPlaybackParametersChanged(EventTime paramEventTime, PlaybackParameters paramPlaybackParameters);
  
  public abstract void onPlayerError(EventTime paramEventTime, ExoPlaybackException paramExoPlaybackException);
  
  public abstract void onPlayerStateChanged(EventTime paramEventTime, boolean paramBoolean, int paramInt);
  
  public abstract void onPositionDiscontinuity(EventTime paramEventTime, int paramInt);
  
  public abstract void onReadingStarted(EventTime paramEventTime);
  
  public abstract void onRenderedFirstFrame(EventTime paramEventTime, Surface paramSurface);
  
  public abstract void onRepeatModeChanged(EventTime paramEventTime, int paramInt);
  
  public abstract void onSeekProcessed(EventTime paramEventTime);
  
  public abstract void onSeekStarted(EventTime paramEventTime);
  
  public abstract void onShuffleModeChanged(EventTime paramEventTime, boolean paramBoolean);
  
  public abstract void onSurfaceSizeChanged(EventTime paramEventTime, int paramInt1, int paramInt2);
  
  public abstract void onTimelineChanged(EventTime paramEventTime, int paramInt);
  
  public abstract void onTracksChanged(EventTime paramEventTime, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);
  
  public abstract void onUpstreamDiscarded(EventTime paramEventTime, MediaSourceEventListener.MediaLoadData paramMediaLoadData);
  
  public abstract void onVideoSizeChanged(EventTime paramEventTime, int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  
  public abstract void onVolumeChanged(EventTime paramEventTime, float paramFloat);
  
  public static final class EventTime
  {
    public final long currentPlaybackPositionMs;
    public final long eventPlaybackPositionMs;
    @Nullable
    public final MediaSource.MediaPeriodId mediaPeriodId;
    public final long realtimeMs;
    public final Timeline timeline;
    public final long totalBufferedDurationMs;
    public final int windowIndex;
    
    public EventTime(long paramLong1, Timeline paramTimeline, int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong2, long paramLong3, long paramLong4)
    {
      realtimeMs = paramLong1;
      timeline = paramTimeline;
      windowIndex = paramInt;
      mediaPeriodId = paramMediaPeriodId;
      eventPlaybackPositionMs = paramLong2;
      currentPlaybackPositionMs = paramLong3;
      totalBufferedDurationMs = paramLong4;
    }
  }
}
