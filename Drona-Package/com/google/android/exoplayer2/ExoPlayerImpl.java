package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class ExoPlayerImpl
  extends BasePlayer
  implements ExoPlayer
{
  private static final String TAG = "ExoPlayerImpl";
  final TrackSelectorResult emptyTrackSelectorResult;
  private final Handler eventHandler;
  private boolean hasPendingPrepare;
  private boolean hasPendingSeek;
  private boolean internalPlayWhenReady;
  private final ExoPlayerImplInternal internalPlayer;
  private final Handler internalPlayerHandler;
  private final CopyOnWriteArraySet<Player.EventListener> listeners;
  private int maskingPeriodIndex;
  private int maskingWindowIndex;
  private long maskingWindowPositionMs;
  private MediaSource mediaSource;
  private int pendingOperationAcks;
  private final ArrayDeque<PlaybackInfoUpdate> pendingPlaybackInfoUpdates;
  private final Timeline.Period period;
  private boolean playWhenReady;
  @Nullable
  private ExoPlaybackException playbackError;
  private PlaybackInfo playbackInfo;
  private PlaybackParameters playbackParameters;
  private final Renderer[] renderers;
  private int repeatMode;
  private SeekParameters seekParameters;
  private boolean shuffleModeEnabled;
  private final TrackSelector trackSelector;
  
  public ExoPlayerImpl(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, BandwidthMeter paramBandwidthMeter, Clock paramClock, Looper paramLooper)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Init ");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" [");
    localStringBuilder.append("ExoPlayerLib/2.9.2");
    localStringBuilder.append("] [");
    localStringBuilder.append(Util.DEVICE_DEBUG_INFO);
    localStringBuilder.append("]");
    Log.log("ExoPlayerImpl", localStringBuilder.toString());
    boolean bool;
    if (paramArrayOfRenderer.length > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    renderers = ((Renderer[])Assertions.checkNotNull(paramArrayOfRenderer));
    trackSelector = ((TrackSelector)Assertions.checkNotNull(paramTrackSelector));
    playWhenReady = false;
    repeatMode = 0;
    shuffleModeEnabled = false;
    listeners = new CopyOnWriteArraySet();
    emptyTrackSelectorResult = new TrackSelectorResult(new RendererConfiguration[paramArrayOfRenderer.length], new TrackSelection[paramArrayOfRenderer.length], null);
    period = new Timeline.Period();
    playbackParameters = PlaybackParameters.DEFAULT;
    seekParameters = SeekParameters.DEFAULT;
    eventHandler = new Handler(paramLooper)
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        handleEvent(paramAnonymousMessage);
      }
    };
    playbackInfo = PlaybackInfo.createDummy(0L, emptyTrackSelectorResult);
    pendingPlaybackInfoUpdates = new ArrayDeque();
    internalPlayer = new ExoPlayerImplInternal(paramArrayOfRenderer, paramTrackSelector, emptyTrackSelectorResult, paramLoadControl, paramBandwidthMeter, playWhenReady, repeatMode, shuffleModeEnabled, eventHandler, this, paramClock);
    internalPlayerHandler = new Handler(internalPlayer.getPlaybackLooper());
  }
  
  private PlaybackInfo getResetPlaybackInfo(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    long l1 = 0L;
    if (paramBoolean1)
    {
      maskingWindowIndex = 0;
      maskingPeriodIndex = 0;
      maskingWindowPositionMs = 0L;
    }
    else
    {
      maskingWindowIndex = getCurrentWindowIndex();
      maskingPeriodIndex = getCurrentPeriodIndex();
      maskingWindowPositionMs = getCurrentPosition();
    }
    if (paramBoolean1) {}
    for (MediaSource.MediaPeriodId localMediaPeriodId = playbackInfo.getDummyFirstMediaPeriodId(shuffleModeEnabled, window);; localMediaPeriodId = playbackInfo.periodId) {
      break;
    }
    if (!paramBoolean1) {
      for (;;)
      {
        l1 = playbackInfo.positionUs;
      }
    }
    if (paramBoolean1) {}
    for (long l2 = -9223372036854775807L;; l2 = playbackInfo.contentPositionUs) {
      break;
    }
    if (paramBoolean2) {}
    for (Timeline localTimeline = Timeline.EMPTY;; localTimeline = playbackInfo.timeline) {
      break;
    }
    if (paramBoolean2) {}
    for (Object localObject = null;; localObject = playbackInfo.manifest) {
      break;
    }
    if (paramBoolean2) {}
    for (TrackGroupArray localTrackGroupArray = TrackGroupArray.EMPTY;; localTrackGroupArray = playbackInfo.trackGroups) {
      break;
    }
    if (paramBoolean2) {}
    for (TrackSelectorResult localTrackSelectorResult = emptyTrackSelectorResult;; localTrackSelectorResult = playbackInfo.trackSelectorResult) {
      break;
    }
    return new PlaybackInfo(localTimeline, localObject, localMediaPeriodId, l1, l2, paramInt, false, localTrackGroupArray, localTrackSelectorResult, localMediaPeriodId, l1, 0L, l1);
  }
  
  private void handlePlaybackInfo(PlaybackInfo paramPlaybackInfo, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    pendingOperationAcks -= paramInt1;
    if (pendingOperationAcks == 0)
    {
      PlaybackInfo localPlaybackInfo = paramPlaybackInfo;
      if (startPositionUs == -9223372036854775807L) {
        localPlaybackInfo = paramPlaybackInfo.resetToNewPosition(periodId, 0L, contentPositionUs);
      }
      if (((!playbackInfo.timeline.isEmpty()) || (hasPendingPrepare)) && (timeline.isEmpty()))
      {
        maskingPeriodIndex = 0;
        maskingWindowIndex = 0;
        maskingWindowPositionMs = 0L;
      }
      if (hasPendingPrepare) {
        paramInt1 = 0;
      } else {
        paramInt1 = 2;
      }
      boolean bool = hasPendingSeek;
      hasPendingPrepare = false;
      hasPendingSeek = false;
      updatePlaybackInfo(localPlaybackInfo, paramBoolean, paramInt2, paramInt1, bool, false);
    }
  }
  
  private long periodPositionUsToWindowPositionMs(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
  {
    paramLong = IpAddress.usToMs(paramLong);
    playbackInfo.timeline.getPeriodByUid(periodUid, period);
    return paramLong + period.getPositionInWindowMs();
  }
  
  private boolean shouldMaskPosition()
  {
    return (playbackInfo.timeline.isEmpty()) || (pendingOperationAcks > 0);
  }
  
  private void updatePlaybackInfo(PlaybackInfo paramPlaybackInfo, boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = pendingPlaybackInfoUpdates.isEmpty();
    pendingPlaybackInfoUpdates.addLast(new PlaybackInfoUpdate(paramPlaybackInfo, playbackInfo, listeners, trackSelector, paramBoolean1, paramInt1, paramInt2, paramBoolean2, playWhenReady, paramBoolean3));
    playbackInfo = paramPlaybackInfo;
    if ((bool ^ true)) {
      return;
    }
    while (!pendingPlaybackInfoUpdates.isEmpty())
    {
      ((PlaybackInfoUpdate)pendingPlaybackInfoUpdates.peekFirst()).notifyListeners();
      pendingPlaybackInfoUpdates.removeFirst();
    }
  }
  
  public void addListener(Player.EventListener paramEventListener)
  {
    listeners.add(paramEventListener);
  }
  
  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    Object localObject = new ArrayList();
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      ExoPlayer.ExoPlayerMessage localExoPlayerMessage = paramVarArgs[i];
      ((List)localObject).add(createMessage(target).setType(messageType).setPayload(message).send());
      i += 1;
    }
    paramVarArgs = ((List)localObject).iterator();
    j = 0;
    while (paramVarArgs.hasNext())
    {
      localObject = (PlayerMessage)paramVarArgs.next();
      i = 1;
      while (i != 0)
      {
        try
        {
          ((PlayerMessage)localObject).blockUntilDelivered();
          i = 0;
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;) {}
        }
        j = 1;
      }
    }
    if (j != 0)
    {
      Thread.currentThread().interrupt();
      return;
    }
  }
  
  public PlayerMessage createMessage(PlayerMessage.Target paramTarget)
  {
    return new PlayerMessage(internalPlayer, paramTarget, playbackInfo.timeline, getCurrentWindowIndex(), internalPlayerHandler);
  }
  
  public Looper getApplicationLooper()
  {
    return eventHandler.getLooper();
  }
  
  public Player.AudioComponent getAudioComponent()
  {
    return null;
  }
  
  public long getBufferedPosition()
  {
    if (isPlayingAd())
    {
      if (playbackInfo.loadingMediaPeriodId.equals(playbackInfo.periodId)) {
        return IpAddress.usToMs(playbackInfo.bufferedPositionUs);
      }
      return getDuration();
    }
    return getContentBufferedPosition();
  }
  
  public long getContentBufferedPosition()
  {
    if (shouldMaskPosition()) {
      return maskingWindowPositionMs;
    }
    if (playbackInfo.loadingMediaPeriodId.windowSequenceNumber != playbackInfo.periodId.windowSequenceNumber) {
      return playbackInfo.timeline.getWindow(getCurrentWindowIndex(), window).getDurationMs();
    }
    long l1 = playbackInfo.bufferedPositionUs;
    if (playbackInfo.loadingMediaPeriodId.isAd())
    {
      Timeline.Period localPeriod = playbackInfo.timeline.getPeriodByUid(playbackInfo.loadingMediaPeriodId.periodUid, period);
      long l2 = localPeriod.getAdGroupTimeUs(playbackInfo.loadingMediaPeriodId.adGroupIndex);
      l1 = l2;
      if (l2 == Long.MIN_VALUE) {
        l1 = durationUs;
      }
    }
    return periodPositionUsToWindowPositionMs(playbackInfo.loadingMediaPeriodId, l1);
  }
  
  public long getContentPosition()
  {
    if (isPlayingAd())
    {
      playbackInfo.timeline.getPeriodByUid(playbackInfo.periodId.periodUid, period);
      return period.getPositionInWindowMs() + IpAddress.usToMs(playbackInfo.contentPositionUs);
    }
    return getCurrentPosition();
  }
  
  public int getCurrentAdGroupIndex()
  {
    if (isPlayingAd()) {
      return playbackInfo.periodId.adGroupIndex;
    }
    return -1;
  }
  
  public int getCurrentAdIndexInAdGroup()
  {
    if (isPlayingAd()) {
      return playbackInfo.periodId.adIndexInAdGroup;
    }
    return -1;
  }
  
  public Object getCurrentManifest()
  {
    return playbackInfo.manifest;
  }
  
  public int getCurrentPeriodIndex()
  {
    if (shouldMaskPosition()) {
      return maskingPeriodIndex;
    }
    return playbackInfo.timeline.getIndexOfPeriod(playbackInfo.periodId.periodUid);
  }
  
  public long getCurrentPosition()
  {
    if (shouldMaskPosition()) {
      return maskingWindowPositionMs;
    }
    if (playbackInfo.periodId.isAd()) {
      return IpAddress.usToMs(playbackInfo.positionUs);
    }
    return periodPositionUsToWindowPositionMs(playbackInfo.periodId, playbackInfo.positionUs);
  }
  
  public Timeline getCurrentTimeline()
  {
    return playbackInfo.timeline;
  }
  
  public TrackGroupArray getCurrentTrackGroups()
  {
    return playbackInfo.trackGroups;
  }
  
  public TrackSelectionArray getCurrentTrackSelections()
  {
    return playbackInfo.trackSelectorResult.selections;
  }
  
  public int getCurrentWindowIndex()
  {
    if (shouldMaskPosition()) {
      return maskingWindowIndex;
    }
    return playbackInfo.timeline.getPeriodByUid(playbackInfo.periodId.periodUid, period).windowIndex;
  }
  
  public long getDuration()
  {
    if (isPlayingAd())
    {
      MediaSource.MediaPeriodId localMediaPeriodId = playbackInfo.periodId;
      playbackInfo.timeline.getPeriodByUid(periodUid, period);
      return IpAddress.usToMs(period.getAdDurationUs(adGroupIndex, adIndexInAdGroup));
    }
    return getContentDuration();
  }
  
  public boolean getPlayWhenReady()
  {
    return playWhenReady;
  }
  
  public ExoPlaybackException getPlaybackError()
  {
    return playbackError;
  }
  
  public Looper getPlaybackLooper()
  {
    return internalPlayer.getPlaybackLooper();
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return playbackParameters;
  }
  
  public int getPlaybackState()
  {
    return playbackInfo.playbackState;
  }
  
  public int getRendererCount()
  {
    return renderers.length;
  }
  
  public int getRendererType(int paramInt)
  {
    return renderers[paramInt].getTrackType();
  }
  
  public int getRepeatMode()
  {
    return repeatMode;
  }
  
  public SeekParameters getSeekParameters()
  {
    return seekParameters;
  }
  
  public boolean getShuffleModeEnabled()
  {
    return shuffleModeEnabled;
  }
  
  public Player.TextComponent getTextComponent()
  {
    return null;
  }
  
  public long getTotalBufferedDuration()
  {
    return Math.max(0L, IpAddress.usToMs(playbackInfo.totalBufferedDurationUs));
  }
  
  public Player.VideoComponent getVideoComponent()
  {
    return null;
  }
  
  void handleEvent(Message paramMessage)
  {
    Object localObject;
    switch (what)
    {
    default: 
      throw new IllegalStateException();
    case 2: 
      paramMessage = (ExoPlaybackException)obj;
      playbackError = paramMessage;
      localObject = listeners.iterator();
    }
    while (((Iterator)localObject).hasNext())
    {
      ((Player.EventListener)((Iterator)localObject).next()).onPlayerError(paramMessage);
      continue;
      paramMessage = (PlaybackParameters)obj;
      if (!playbackParameters.equals(paramMessage))
      {
        playbackParameters = paramMessage;
        localObject = listeners.iterator();
        while (((Iterator)localObject).hasNext())
        {
          ((Player.EventListener)((Iterator)localObject).next()).onPlaybackParametersChanged(paramMessage);
          continue;
          localObject = (PlaybackInfo)obj;
          int i = arg1;
          boolean bool;
          if (arg2 != -1) {
            bool = true;
          } else {
            bool = false;
          }
          handlePlaybackInfo((PlaybackInfo)localObject, i, bool, arg2);
        }
      }
    }
  }
  
  public boolean isLoading()
  {
    return playbackInfo.isLoading;
  }
  
  public boolean isPlayingAd()
  {
    return (!shouldMaskPosition()) && (playbackInfo.periodId.isAd());
  }
  
  public void prepare(MediaSource paramMediaSource)
  {
    prepare(paramMediaSource, true, true);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    playbackError = null;
    mediaSource = paramMediaSource;
    PlaybackInfo localPlaybackInfo = getResetPlaybackInfo(paramBoolean1, paramBoolean2, 2);
    hasPendingPrepare = true;
    pendingOperationAcks += 1;
    internalPlayer.prepare(paramMediaSource, paramBoolean1, paramBoolean2);
    updatePlaybackInfo(localPlaybackInfo, false, 4, 1, false, false);
  }
  
  public void release()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Release ");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" [");
    localStringBuilder.append("ExoPlayerLib/2.9.2");
    localStringBuilder.append("] [");
    localStringBuilder.append(Util.DEVICE_DEBUG_INFO);
    localStringBuilder.append("] [");
    localStringBuilder.append(ExoPlayerLibraryInfo.registeredModules());
    localStringBuilder.append("]");
    Log.log("ExoPlayerImpl", localStringBuilder.toString());
    mediaSource = null;
    internalPlayer.release();
    eventHandler.removeCallbacksAndMessages(null);
  }
  
  public void removeListener(Player.EventListener paramEventListener)
  {
    listeners.remove(paramEventListener);
  }
  
  public void retry()
  {
    if ((mediaSource != null) && ((playbackError != null) || (playbackInfo.playbackState == 1))) {
      prepare(mediaSource, false, false);
    }
  }
  
  public void seekTo(int paramInt, long paramLong)
  {
    Object localObject = playbackInfo.timeline;
    if ((paramInt >= 0) && ((((Timeline)localObject).isEmpty()) || (paramInt < ((Timeline)localObject).getWindowCount())))
    {
      hasPendingSeek = true;
      pendingOperationAcks += 1;
      if (isPlayingAd())
      {
        Log.w("ExoPlayerImpl", "seekTo ignored because an ad is playing");
        eventHandler.obtainMessage(0, 1, -1, playbackInfo).sendToTarget();
        return;
      }
      maskingWindowIndex = paramInt;
      long l;
      if (((Timeline)localObject).isEmpty())
      {
        if (paramLong == -9223372036854775807L) {
          l = 0L;
        } else {
          l = paramLong;
        }
        maskingWindowPositionMs = l;
        maskingPeriodIndex = 0;
      }
      else
      {
        if (paramLong == -9223372036854775807L) {}
        for (l = ((Timeline)localObject).getWindow(paramInt, window).getDefaultPositionUs();; l = IpAddress.msToUs(paramLong)) {
          break;
        }
        Pair localPair = ((Timeline)localObject).getPeriodPosition(window, period, paramInt, l);
        maskingWindowPositionMs = IpAddress.usToMs(l);
        maskingPeriodIndex = ((Timeline)localObject).getIndexOfPeriod(first);
      }
      internalPlayer.seekTo((Timeline)localObject, paramInt, IpAddress.msToUs(paramLong));
      localObject = listeners.iterator();
      while (((Iterator)localObject).hasNext()) {
        ((Player.EventListener)((Iterator)localObject).next()).onPositionDiscontinuity(1);
      }
      return;
    }
    throw new IllegalSeekPositionException((Timeline)localObject, paramInt, paramLong);
  }
  
  public void sendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      ExoPlayer.ExoPlayerMessage localExoPlayerMessage = paramVarArgs[i];
      createMessage(target).setType(messageType).setPayload(message).send();
      i += 1;
    }
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    setPlayWhenReady(paramBoolean, false);
  }
  
  public void setPlayWhenReady(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2)) {
      paramBoolean2 = true;
    } else {
      paramBoolean2 = false;
    }
    if (internalPlayWhenReady != paramBoolean2)
    {
      internalPlayWhenReady = paramBoolean2;
      internalPlayer.setPlayWhenReady(paramBoolean2);
    }
    if (playWhenReady != paramBoolean1)
    {
      playWhenReady = paramBoolean1;
      updatePlaybackInfo(playbackInfo, false, 4, 1, false, true);
    }
  }
  
  public void setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    PlaybackParameters localPlaybackParameters = paramPlaybackParameters;
    if (paramPlaybackParameters == null) {
      localPlaybackParameters = PlaybackParameters.DEFAULT;
    }
    internalPlayer.setPlaybackParameters(localPlaybackParameters);
  }
  
  public void setRepeatMode(int paramInt)
  {
    if (repeatMode != paramInt)
    {
      repeatMode = paramInt;
      internalPlayer.setRepeatMode(paramInt);
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext()) {
        ((Player.EventListener)localIterator.next()).onRepeatModeChanged(paramInt);
      }
    }
  }
  
  public void setSeekParameters(SeekParameters paramSeekParameters)
  {
    SeekParameters localSeekParameters = paramSeekParameters;
    if (paramSeekParameters == null) {
      localSeekParameters = SeekParameters.DEFAULT;
    }
    if (!seekParameters.equals(localSeekParameters))
    {
      seekParameters = localSeekParameters;
      internalPlayer.setSeekParameters(localSeekParameters);
    }
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    if (shuffleModeEnabled != paramBoolean)
    {
      shuffleModeEnabled = paramBoolean;
      internalPlayer.setShuffleModeEnabled(paramBoolean);
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext()) {
        ((Player.EventListener)localIterator.next()).onShuffleModeEnabledChanged(paramBoolean);
      }
    }
  }
  
  public void stop(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      playbackError = null;
      mediaSource = null;
    }
    PlaybackInfo localPlaybackInfo = getResetPlaybackInfo(paramBoolean, paramBoolean, 1);
    pendingOperationAcks += 1;
    internalPlayer.stop(paramBoolean);
    updatePlaybackInfo(localPlaybackInfo, false, 4, 1, false, false);
  }
  
  private static final class PlaybackInfoUpdate
  {
    private final boolean isLoadingChanged;
    private final Set<Player.EventListener> listeners;
    private final boolean playWhenReady;
    private final PlaybackInfo playbackInfo;
    private final boolean playbackStateOrPlayWhenReadyChanged;
    private final boolean positionDiscontinuity;
    private final int positionDiscontinuityReason;
    private final boolean seekProcessed;
    private final int timelineChangeReason;
    private final boolean timelineOrManifestChanged;
    private final TrackSelector trackSelector;
    private final boolean trackSelectorResultChanged;
    
    public PlaybackInfoUpdate(PlaybackInfo paramPlaybackInfo1, PlaybackInfo paramPlaybackInfo2, Set paramSet, TrackSelector paramTrackSelector, boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      playbackInfo = paramPlaybackInfo1;
      listeners = paramSet;
      trackSelector = paramTrackSelector;
      positionDiscontinuity = paramBoolean1;
      positionDiscontinuityReason = paramInt1;
      timelineChangeReason = paramInt2;
      seekProcessed = paramBoolean2;
      playWhenReady = paramBoolean3;
      paramBoolean2 = false;
      if ((!paramBoolean4) && (playbackState == playbackState)) {
        paramBoolean1 = false;
      } else {
        paramBoolean1 = true;
      }
      playbackStateOrPlayWhenReadyChanged = paramBoolean1;
      if ((timeline == timeline) && (manifest == manifest)) {
        paramBoolean1 = false;
      } else {
        paramBoolean1 = true;
      }
      timelineOrManifestChanged = paramBoolean1;
      if (isLoading != isLoading) {
        paramBoolean1 = true;
      } else {
        paramBoolean1 = false;
      }
      isLoadingChanged = paramBoolean1;
      paramBoolean1 = paramBoolean2;
      if (trackSelectorResult != trackSelectorResult) {
        paramBoolean1 = true;
      }
      trackSelectorResultChanged = paramBoolean1;
    }
    
    public void notifyListeners()
    {
      if ((timelineOrManifestChanged) || (timelineChangeReason == 0))
      {
        localObject1 = listeners.iterator();
        while (((Iterator)localObject1).hasNext()) {
          ((Player.EventListener)((Iterator)localObject1).next()).onTimelineChanged(playbackInfo.timeline, playbackInfo.manifest, timelineChangeReason);
        }
      }
      if (positionDiscontinuity)
      {
        localObject1 = listeners.iterator();
        while (((Iterator)localObject1).hasNext()) {
          ((Player.EventListener)((Iterator)localObject1).next()).onPositionDiscontinuity(positionDiscontinuityReason);
        }
      }
      if (trackSelectorResultChanged)
      {
        trackSelector.onSelectionActivated(playbackInfo.trackSelectorResult.info);
        localObject1 = listeners.iterator();
        while (((Iterator)localObject1).hasNext()) {
          ((Player.EventListener)((Iterator)localObject1).next()).onTracksChanged(playbackInfo.trackGroups, playbackInfo.trackSelectorResult.selections);
        }
      }
      if (isLoadingChanged)
      {
        localObject1 = listeners.iterator();
        while (((Iterator)localObject1).hasNext()) {
          ((Player.EventListener)((Iterator)localObject1).next()).onLoadingChanged(playbackInfo.isLoading);
        }
      }
      boolean bool = playbackStateOrPlayWhenReadyChanged;
      Object localObject1 = this;
      Object localObject2 = localObject1;
      if (bool)
      {
        Iterator localIterator = listeners.iterator();
        for (;;)
        {
          localObject2 = localObject1;
          if (!localIterator.hasNext()) {
            break;
          }
          Player.EventListener localEventListener = (Player.EventListener)localIterator.next();
          bool = playWhenReady;
          localObject2 = localObject1;
          localEventListener.onPlayerStateChanged(bool, playbackInfo.playbackState);
          localObject1 = localObject2;
        }
      }
      if (seekProcessed)
      {
        localObject1 = listeners.iterator();
        while (((Iterator)localObject1).hasNext()) {
          ((Player.EventListener)((Iterator)localObject1).next()).onSeekProcessed();
        }
      }
    }
  }
}
