package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSource.SourceInfoRefreshListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class ExoPlayerImplInternal
  implements Handler.Callback, MediaPeriod.Callback, TrackSelector.InvalidationListener, MediaSource.SourceInfoRefreshListener, DefaultMediaClock.PlaybackParameterListener, PlayerMessage.Sender
{
  private static final int IDLE_INTERVAL_MS = 1000;
  private static final int MSG_DO_SOME_WORK = 2;
  public static final int MSG_ERROR = 2;
  private static final int MSG_PERIOD_PREPARED = 9;
  public static final int MSG_PLAYBACK_INFO_CHANGED = 0;
  public static final int MSG_PLAYBACK_PARAMETERS_CHANGED = 1;
  private static final int MSG_PLAYBACK_PARAMETERS_CHANGED_INTERNAL = 16;
  private static final int MSG_PREPARE = 0;
  private static final int MSG_REFRESH_SOURCE_INFO = 8;
  private static final int MSG_RELEASE = 7;
  private static final int MSG_SEEK_TO = 3;
  private static final int MSG_SEND_MESSAGE = 14;
  private static final int MSG_SEND_MESSAGE_TO_TARGET_THREAD = 15;
  private static final int MSG_SET_PLAYBACK_PARAMETERS = 4;
  private static final int MSG_SET_PLAY_WHEN_READY = 1;
  private static final int MSG_SET_REPEAT_MODE = 12;
  private static final int MSG_SET_SEEK_PARAMETERS = 5;
  private static final int MSG_SET_SHUFFLE_ENABLED = 13;
  private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 10;
  private static final int MSG_STOP = 6;
  private static final int MSG_TRACK_SELECTION_INVALIDATED = 11;
  private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
  private static final int RENDERING_INTERVAL_MS = 10;
  private static final String TAG = "ExoPlayerImplInternal";
  private final long backBufferDurationUs;
  private final BandwidthMeter bandwidthMeter;
  private final Clock clock;
  private final TrackSelectorResult emptyTrackSelectorResult;
  private Renderer[] enabledRenderers;
  private final Handler eventHandler;
  private final HandlerWrapper handler;
  private final HandlerThread internalPlaybackThread;
  private final LoadControl loadControl;
  private final DefaultMediaClock mediaClock;
  private MediaSource mediaSource;
  private int nextPendingMessageIndex;
  private SeekPosition pendingInitialSeekPosition;
  private final ArrayList<PendingMessageInfo> pendingMessages;
  private int pendingPrepareCount;
  private final Timeline.Period period;
  private boolean playWhenReady;
  private PlaybackInfo playbackInfo;
  private final PlaybackInfoUpdate playbackInfoUpdate;
  private final ExoPlayer player;
  private final MediaPeriodQueue queue;
  private boolean rebuffering;
  private boolean released;
  private final RendererCapabilities[] rendererCapabilities;
  private long rendererPositionUs;
  private final Renderer[] renderers;
  private int repeatMode;
  private final boolean retainBackBufferFromKeyframe;
  private SeekParameters seekParameters;
  private boolean shuffleModeEnabled;
  private final TrackSelector trackSelector;
  private final Timeline.Window window;
  
  public ExoPlayerImplInternal(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, TrackSelectorResult paramTrackSelectorResult, LoadControl paramLoadControl, BandwidthMeter paramBandwidthMeter, boolean paramBoolean1, int paramInt, boolean paramBoolean2, Handler paramHandler, ExoPlayer paramExoPlayer, Clock paramClock)
  {
    renderers = paramArrayOfRenderer;
    trackSelector = paramTrackSelector;
    emptyTrackSelectorResult = paramTrackSelectorResult;
    loadControl = paramLoadControl;
    bandwidthMeter = paramBandwidthMeter;
    playWhenReady = paramBoolean1;
    repeatMode = paramInt;
    shuffleModeEnabled = paramBoolean2;
    eventHandler = paramHandler;
    player = paramExoPlayer;
    clock = paramClock;
    queue = new MediaPeriodQueue();
    backBufferDurationUs = paramLoadControl.getBackBufferDurationUs();
    retainBackBufferFromKeyframe = paramLoadControl.retainBackBufferFromKeyframe();
    seekParameters = SeekParameters.DEFAULT;
    playbackInfo = PlaybackInfo.createDummy(-9223372036854775807L, paramTrackSelectorResult);
    playbackInfoUpdate = new PlaybackInfoUpdate(null);
    rendererCapabilities = new RendererCapabilities[paramArrayOfRenderer.length];
    paramInt = 0;
    while (paramInt < paramArrayOfRenderer.length)
    {
      paramArrayOfRenderer[paramInt].setIndex(paramInt);
      rendererCapabilities[paramInt] = paramArrayOfRenderer[paramInt].getCapabilities();
      paramInt += 1;
    }
    mediaClock = new DefaultMediaClock(this, paramClock);
    pendingMessages = new ArrayList();
    enabledRenderers = new Renderer[0];
    window = new Timeline.Window();
    period = new Timeline.Period();
    paramTrackSelector.init(this, paramBandwidthMeter);
    internalPlaybackThread = new HandlerThread("ExoPlayerImplInternal:Handler", -16);
    internalPlaybackThread.start();
    handler = paramClock.createHandler(internalPlaybackThread.getLooper(), this);
  }
  
  private void deliverMessage(PlayerMessage paramPlayerMessage)
    throws ExoPlaybackException
  {
    if (paramPlayerMessage.isCanceled()) {
      return;
    }
    try
    {
      paramPlayerMessage.getTarget().handleMessage(paramPlayerMessage.getType(), paramPlayerMessage.getPayload());
      paramPlayerMessage.markAsProcessed(true);
      return;
    }
    catch (Throwable localThrowable)
    {
      paramPlayerMessage.markAsProcessed(true);
      throw localThrowable;
    }
  }
  
  private void disableRenderer(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    mediaClock.onRendererDisabled(paramRenderer);
    ensureStopped(paramRenderer);
    paramRenderer.disable();
  }
  
  private void doSomeWork()
    throws ExoPlaybackException, IOException
  {
    long l1 = clock.uptimeMillis();
    updatePeriods();
    if (!queue.hasPlayingPeriod())
    {
      maybeThrowPeriodPrepareError();
      scheduleNextWork(l1, 10L);
      return;
    }
    Object localObject = queue.getPlayingPeriod();
    TraceUtil.beginSection("doSomeWork");
    updatePlaybackPositions();
    long l2 = SystemClock.elapsedRealtime();
    mediaPeriod.discardBuffer(playbackInfo.positionUs - backBufferDurationUs, retainBackBufferFromKeyframe);
    Renderer[] arrayOfRenderer = enabledRenderers;
    int m = arrayOfRenderer.length;
    boolean bool = true;
    int j = 0;
    int i = 1;
    while (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      localRenderer.render(rendererPositionUs, l2 * 1000L);
      if ((i != 0) && (localRenderer.isEnded())) {
        i = 1;
      } else {
        i = 0;
      }
      int k;
      if ((!localRenderer.isReady()) && (!localRenderer.isEnded()) && (!rendererWaitingForNextStream(localRenderer))) {
        k = 0;
      } else {
        k = 1;
      }
      if (k == 0) {
        localRenderer.maybeThrowStreamError();
      }
      if ((bool) && (k != 0)) {
        bool = true;
      } else {
        bool = false;
      }
      j += 1;
    }
    if (!bool) {
      maybeThrowPeriodPrepareError();
    }
    l2 = info.durationUs;
    if ((i != 0) && ((l2 == -9223372036854775807L) || (l2 <= playbackInfo.positionUs)) && (info.isFinal))
    {
      setState(4);
      stopRenderers();
    }
    else if ((playbackInfo.playbackState == 2) && (shouldTransitionToReadyState(bool)))
    {
      setState(3);
      if (playWhenReady) {
        startRenderers();
      }
    }
    else if ((playbackInfo.playbackState == 3) && (enabledRenderers.length == 0 ? !isTimelineReady() : !bool))
    {
      rebuffering = playWhenReady;
      setState(2);
      stopRenderers();
    }
    if (playbackInfo.playbackState == 2)
    {
      localObject = enabledRenderers;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        localObject[i].maybeThrowStreamError();
        i += 1;
      }
    }
    if (((playWhenReady) && (playbackInfo.playbackState == 3)) || (playbackInfo.playbackState == 2)) {
      scheduleNextWork(l1, 10L);
    } else if ((enabledRenderers.length != 0) && (playbackInfo.playbackState != 4)) {
      scheduleNextWork(l1, 1000L);
    } else {
      handler.removeMessages(2);
    }
    TraceUtil.endSection();
  }
  
  private void enableRenderer(int paramInt1, boolean paramBoolean, int paramInt2)
    throws ExoPlaybackException
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getPlayingPeriod();
    Renderer localRenderer = renderers[paramInt1];
    enabledRenderers[paramInt2] = localRenderer;
    if (localRenderer.getState() == 0)
    {
      RendererConfiguration localRendererConfiguration = trackSelectorResult.rendererConfigurations[paramInt1];
      Format[] arrayOfFormat = getFormats(trackSelectorResult.selections.getChapters(paramInt1));
      if ((playWhenReady) && (playbackInfo.playbackState == 3)) {
        paramInt2 = 1;
      } else {
        paramInt2 = 0;
      }
      if ((!paramBoolean) && (paramInt2 != 0)) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
      localRenderer.enable(localRendererConfiguration, arrayOfFormat, sampleStreams[paramInt1], rendererPositionUs, paramBoolean, localMediaPeriodHolder.getRendererOffset());
      mediaClock.onRendererEnabled(localRenderer);
      if (paramInt2 != 0) {
        localRenderer.start();
      }
    }
  }
  
  private void enableRenderers(boolean[] paramArrayOfBoolean, int paramInt)
    throws ExoPlaybackException
  {
    enabledRenderers = new Renderer[paramInt];
    MediaPeriodHolder localMediaPeriodHolder = queue.getPlayingPeriod();
    paramInt = 0;
    int j;
    for (int i = 0; paramInt < renderers.length; i = j)
    {
      j = i;
      if (trackSelectorResult.isRendererEnabled(paramInt))
      {
        enableRenderer(paramInt, paramArrayOfBoolean[paramInt], i);
        j = i + 1;
      }
      paramInt += 1;
    }
  }
  
  private void ensureStopped(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    if (paramRenderer.getState() == 2) {
      paramRenderer.stop();
    }
  }
  
  private static Format[] getFormats(TrackSelection paramTrackSelection)
  {
    int j = 0;
    int i;
    if (paramTrackSelection != null) {
      i = paramTrackSelection.length();
    } else {
      i = 0;
    }
    Format[] arrayOfFormat = new Format[i];
    while (j < i)
    {
      arrayOfFormat[j] = paramTrackSelection.getFormat(j);
      j += 1;
    }
    return arrayOfFormat;
  }
  
  private Pair getPeriodPosition(Timeline paramTimeline, int paramInt, long paramLong)
  {
    return paramTimeline.getPeriodPosition(window, period, paramInt, paramLong);
  }
  
  private long getTotalBufferedDurationUs()
  {
    return getTotalBufferedDurationUs(playbackInfo.bufferedPositionUs);
  }
  
  private long getTotalBufferedDurationUs(long paramLong)
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getLoadingPeriod();
    if (localMediaPeriodHolder == null) {
      return 0L;
    }
    return paramLong - localMediaPeriodHolder.toPeriodTime(rendererPositionUs);
  }
  
  private void handleContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    if (!queue.isLoading(paramMediaPeriod)) {
      return;
    }
    queue.reevaluateBuffer(rendererPositionUs);
    maybeContinueLoading();
  }
  
  private void handleLoadingMediaPeriodChanged(boolean paramBoolean)
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getLoadingPeriod();
    if (localMediaPeriodHolder == null) {
      localObject = playbackInfo.periodId;
    } else {
      localObject = info.anchor;
    }
    boolean bool = playbackInfo.loadingMediaPeriodId.equals(localObject) ^ true;
    if (bool) {
      playbackInfo = playbackInfo.copyWithLoadingMediaPeriodId((MediaSource.MediaPeriodId)localObject);
    }
    Object localObject = playbackInfo;
    long l;
    if (localMediaPeriodHolder == null) {
      l = playbackInfo.positionUs;
    } else {
      l = localMediaPeriodHolder.getBufferedPositionUs();
    }
    bufferedPositionUs = l;
    playbackInfo.totalBufferedDurationUs = getTotalBufferedDurationUs();
    if (((bool) || (paramBoolean)) && (localMediaPeriodHolder != null) && (prepared)) {
      updateLoadControlTrackSelection(trackGroups, trackSelectorResult);
    }
  }
  
  private void handlePeriodPrepared(MediaPeriod paramMediaPeriod)
    throws ExoPlaybackException
  {
    if (!queue.isLoading(paramMediaPeriod)) {
      return;
    }
    paramMediaPeriod = queue.getLoadingPeriod();
    paramMediaPeriod.handlePrepared(mediaClock.getPlaybackParameters().speed);
    updateLoadControlTrackSelection(trackGroups, trackSelectorResult);
    if (!queue.hasPlayingPeriod())
    {
      resetRendererPosition(queue.advancePlayingPeriod().info.startPositionUs);
      updatePlayingPeriodRenderers(null);
    }
    maybeContinueLoading();
  }
  
  private void handlePlaybackParameters(PlaybackParameters paramPlaybackParameters)
    throws ExoPlaybackException
  {
    eventHandler.obtainMessage(1, paramPlaybackParameters).sendToTarget();
    updateTrackSelectionPlaybackSpeed(speed);
    Renderer[] arrayOfRenderer = renderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = arrayOfRenderer[i];
      if (localRenderer != null) {
        localRenderer.setOperatingRate(speed);
      }
      i += 1;
    }
  }
  
  private void handleSourceInfoRefreshEndedPlayback()
  {
    setState(4);
    resetInternal(false, true, false);
  }
  
  private void handleSourceInfoRefreshed(MediaSourceRefreshInfo paramMediaSourceRefreshInfo)
    throws ExoPlaybackException
  {
    if (source != mediaSource) {
      return;
    }
    Timeline localTimeline = playbackInfo.timeline;
    Object localObject2 = timeline;
    paramMediaSourceRefreshInfo = manifest;
    queue.setTimeline((Timeline)localObject2);
    playbackInfo = playbackInfo.copyWithTimeline((Timeline)localObject2, paramMediaSourceRefreshInfo);
    resolvePendingMessagePositions();
    int i = pendingPrepareCount;
    long l1 = 0L;
    Object localObject1;
    long l2;
    if (i > 0)
    {
      playbackInfoUpdate.incrementPendingOperationAcks(pendingPrepareCount);
      pendingPrepareCount = 0;
      if (pendingInitialSeekPosition != null)
      {
        paramMediaSourceRefreshInfo = pendingInitialSeekPosition;
        try
        {
          paramMediaSourceRefreshInfo = resolveSeekPosition(paramMediaSourceRefreshInfo, true);
          pendingInitialSeekPosition = null;
          if (paramMediaSourceRefreshInfo == null)
          {
            handleSourceInfoRefreshEndedPlayback();
            return;
          }
          localObject1 = first;
          l2 = ((Long)second).longValue();
          paramMediaSourceRefreshInfo = queue.resolveMediaPeriodIdForAds(localObject1, l2);
          localObject1 = playbackInfo;
          if (paramMediaSourceRefreshInfo.isAd()) {
            l1 = 0L;
          } else {
            l1 = l2;
          }
          playbackInfo = ((PlaybackInfo)localObject1).resetToNewPosition(paramMediaSourceRefreshInfo, l1, l2);
          return;
        }
        catch (IllegalSeekPositionException paramMediaSourceRefreshInfo)
        {
          localObject1 = playbackInfo.getDummyFirstMediaPeriodId(shuffleModeEnabled, window);
          playbackInfo = playbackInfo.resetToNewPosition((MediaSource.MediaPeriodId)localObject1, -9223372036854775807L, -9223372036854775807L);
          throw paramMediaSourceRefreshInfo;
        }
      }
      if (playbackInfo.startPositionUs == -9223372036854775807L)
      {
        if (((Timeline)localObject2).isEmpty())
        {
          handleSourceInfoRefreshEndedPlayback();
          return;
        }
        paramMediaSourceRefreshInfo = getPeriodPosition((Timeline)localObject2, ((Timeline)localObject2).getFirstWindowIndex(shuffleModeEnabled), -9223372036854775807L);
        localObject1 = first;
        l2 = ((Long)second).longValue();
        paramMediaSourceRefreshInfo = queue.resolveMediaPeriodIdForAds(localObject1, l2);
        localObject1 = playbackInfo;
        if (paramMediaSourceRefreshInfo.isAd()) {
          l1 = 0L;
        } else {
          l1 = l2;
        }
        playbackInfo = ((PlaybackInfo)localObject1).resetToNewPosition(paramMediaSourceRefreshInfo, l1, l2);
      }
    }
    else if (localTimeline.isEmpty())
    {
      if (!((Timeline)localObject2).isEmpty())
      {
        paramMediaSourceRefreshInfo = getPeriodPosition((Timeline)localObject2, ((Timeline)localObject2).getFirstWindowIndex(shuffleModeEnabled), -9223372036854775807L);
        localObject1 = first;
        l2 = ((Long)second).longValue();
        paramMediaSourceRefreshInfo = queue.resolveMediaPeriodIdForAds(localObject1, l2);
        localObject1 = playbackInfo;
        if (paramMediaSourceRefreshInfo.isAd()) {
          l1 = 0L;
        } else {
          l1 = l2;
        }
        playbackInfo = ((PlaybackInfo)localObject1).resetToNewPosition(paramMediaSourceRefreshInfo, l1, l2);
      }
    }
    else
    {
      MediaPeriodHolder localMediaPeriodHolder = queue.getFrontPeriod();
      paramMediaSourceRefreshInfo = localMediaPeriodHolder;
      l2 = playbackInfo.contentPositionUs;
      if (localMediaPeriodHolder == null) {
        localObject1 = playbackInfo.periodId.periodUid;
      } else {
        localObject1 = target;
      }
      if (((Timeline)localObject2).getIndexOfPeriod(localObject1) == -1)
      {
        localObject1 = resolveSubsequentPeriod(localObject1, localTimeline, (Timeline)localObject2);
        if (localObject1 == null)
        {
          handleSourceInfoRefreshEndedPlayback();
          return;
        }
        localObject1 = getPeriodPosition((Timeline)localObject2, getPeriodByUidperiod).windowIndex, -9223372036854775807L);
        localObject2 = first;
        l2 = ((Long)second).longValue();
        localObject2 = queue.resolveMediaPeriodIdForAds(localObject2, l2);
        if (localMediaPeriodHolder != null) {
          while (next != null)
          {
            localMediaPeriodHolder = next;
            localObject1 = localMediaPeriodHolder;
            paramMediaSourceRefreshInfo = (MediaSourceRefreshInfo)localObject1;
            if (info.anchor.equals(localObject2))
            {
              info = queue.getUpdatedMediaPeriodInfo(info);
              paramMediaSourceRefreshInfo = (MediaSourceRefreshInfo)localObject1;
            }
          }
        }
        if (!((MediaSource.MediaPeriodId)localObject2).isAd()) {
          l1 = l2;
        }
        l1 = seekToPeriodPosition((MediaSource.MediaPeriodId)localObject2, l1);
        playbackInfo = playbackInfo.copyWithNewPosition((MediaSource.MediaPeriodId)localObject2, l1, l2, getTotalBufferedDurationUs());
        return;
      }
      paramMediaSourceRefreshInfo = playbackInfo.periodId;
      if (paramMediaSourceRefreshInfo.isAd())
      {
        localObject1 = queue.resolveMediaPeriodIdForAds(localObject1, l2);
        if (!((MediaSource.MediaPeriodId)localObject1).equals(paramMediaSourceRefreshInfo))
        {
          if (!((MediaSource.MediaPeriodId)localObject1).isAd()) {
            l1 = l2;
          }
          l1 = seekToPeriodPosition((MediaSource.MediaPeriodId)localObject1, l1);
          playbackInfo = playbackInfo.copyWithNewPosition((MediaSource.MediaPeriodId)localObject1, l1, l2, getTotalBufferedDurationUs());
          return;
        }
      }
      if (!queue.updateQueuedPeriods(paramMediaSourceRefreshInfo, rendererPositionUs)) {
        seekToCurrentPosition(false);
      }
      handleLoadingMediaPeriodChanged(false);
    }
  }
  
  private boolean isTimelineReady()
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getPlayingPeriod();
    long l = info.durationUs;
    return (l == -9223372036854775807L) || (playbackInfo.positionUs < l) || ((next != null) && ((next.prepared) || (next.info.anchor.isAd())));
  }
  
  private void maybeContinueLoading()
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getLoadingPeriod();
    long l = localMediaPeriodHolder.getNextLoadPositionUs();
    if (l == Long.MIN_VALUE)
    {
      setIsLoading(false);
      return;
    }
    l = getTotalBufferedDurationUs(l);
    boolean bool = loadControl.shouldContinueLoading(l, mediaClock.getPlaybackParameters().speed);
    setIsLoading(bool);
    if (bool) {
      localMediaPeriodHolder.continueLoading(rendererPositionUs);
    }
  }
  
  private void maybeNotifyPlaybackInfoChanged()
  {
    if (playbackInfoUpdate.hasPendingUpdate(playbackInfo))
    {
      Handler localHandler = eventHandler;
      int j = playbackInfoUpdate.operationAcks;
      int i;
      if (playbackInfoUpdate.positionDiscontinuity) {
        i = playbackInfoUpdate.discontinuityReason;
      } else {
        i = -1;
      }
      localHandler.obtainMessage(0, j, i, playbackInfo).sendToTarget();
      playbackInfoUpdate.reset(playbackInfo);
    }
  }
  
  private void maybeThrowPeriodPrepareError()
    throws IOException
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getLoadingPeriod();
    Object localObject = queue.getReadingPeriod();
    if ((localMediaPeriodHolder != null) && (!prepared) && ((localObject == null) || (next == localMediaPeriodHolder)))
    {
      localObject = enabledRenderers;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        if (!localObject[i].hasReadStreamToEnd()) {
          return;
        }
        i += 1;
      }
      mediaPeriod.maybeThrowPrepareError();
    }
  }
  
  private void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    if (queue.getLoadingPeriod() != null)
    {
      Renderer[] arrayOfRenderer = enabledRenderers;
      int j = arrayOfRenderer.length;
      int i = 0;
      while (i < j)
      {
        if (!arrayOfRenderer[i].hasReadStreamToEnd()) {
          return;
        }
        i += 1;
      }
    }
    mediaSource.maybeThrowSourceInfoRefreshError();
  }
  
  private void maybeTriggerPendingMessages(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (!pendingMessages.isEmpty())
    {
      if (playbackInfo.periodId.isAd()) {
        return;
      }
      long l = paramLong1;
      if (playbackInfo.startPositionUs == paramLong1) {
        l = paramLong1 - 1L;
      }
      int i = playbackInfo.timeline.getIndexOfPeriod(playbackInfo.periodId.periodUid);
      paramLong1 = l;
      PendingMessageInfo localPendingMessageInfo1;
      if (nextPendingMessageIndex > 0)
      {
        localPendingMessageInfo1 = (PendingMessageInfo)pendingMessages.get(nextPendingMessageIndex - 1);
      }
      else
      {
        localPendingMessageInfo1 = null;
        l = paramLong1;
      }
      for (;;)
      {
        if ((localPendingMessageInfo1 == null) || ((resolvedPeriodIndex <= i) && ((resolvedPeriodIndex != i) || (resolvedPeriodTimeUs <= l)))) {
          break label180;
        }
        nextPendingMessageIndex -= 1;
        paramLong1 = l;
        if (nextPendingMessageIndex <= 0) {
          break;
        }
        localPendingMessageInfo1 = (PendingMessageInfo)pendingMessages.get(nextPendingMessageIndex - 1);
      }
      label180:
      paramLong1 = l;
      if (nextPendingMessageIndex < pendingMessages.size())
      {
        localPendingMessageInfo1 = (PendingMessageInfo)pendingMessages.get(nextPendingMessageIndex);
      }
      else
      {
        localPendingMessageInfo1 = null;
        l = paramLong1;
      }
      PendingMessageInfo localPendingMessageInfo2;
      for (;;)
      {
        localPendingMessageInfo2 = localPendingMessageInfo1;
        if (localPendingMessageInfo1 == null) {
          break label328;
        }
        localPendingMessageInfo2 = localPendingMessageInfo1;
        if (resolvedPeriodUid == null) {
          break label328;
        }
        if (resolvedPeriodIndex >= i)
        {
          localPendingMessageInfo2 = localPendingMessageInfo1;
          if (resolvedPeriodIndex != i) {
            break label328;
          }
          localPendingMessageInfo2 = localPendingMessageInfo1;
          if (resolvedPeriodTimeUs > l) {
            break label328;
          }
        }
        nextPendingMessageIndex += 1;
        paramLong1 = l;
        if (nextPendingMessageIndex >= pendingMessages.size()) {
          break;
        }
        localPendingMessageInfo1 = (PendingMessageInfo)pendingMessages.get(nextPendingMessageIndex);
      }
      label328:
      while ((localPendingMessageInfo2 != null) && (resolvedPeriodUid != null) && (resolvedPeriodIndex == i) && (resolvedPeriodTimeUs > l) && (resolvedPeriodTimeUs <= paramLong2))
      {
        sendMessageToTarget(message);
        if ((!message.getDeleteAfterDelivery()) && (!message.isCanceled())) {
          nextPendingMessageIndex += 1;
        } else {
          pendingMessages.remove(nextPendingMessageIndex);
        }
        if (nextPendingMessageIndex < pendingMessages.size()) {
          localPendingMessageInfo2 = (PendingMessageInfo)pendingMessages.get(nextPendingMessageIndex);
        } else {
          localPendingMessageInfo2 = null;
        }
      }
    }
  }
  
  private void maybeUpdateLoadingPeriod()
    throws IOException
  {
    queue.reevaluateBuffer(rendererPositionUs);
    if (queue.shouldLoadNextMediaPeriod())
    {
      MediaPeriodInfo localMediaPeriodInfo = queue.getNextMediaPeriodInfo(rendererPositionUs, playbackInfo);
      if (localMediaPeriodInfo == null)
      {
        maybeThrowSourceInfoRefreshError();
        return;
      }
      queue.enqueueNextMediaPeriod(rendererCapabilities, trackSelector, loadControl.getAllocator(), mediaSource, localMediaPeriodInfo).prepare(this, startPositionUs);
      setIsLoading(true);
      handleLoadingMediaPeriodChanged(false);
    }
  }
  
  private void prepareInternal(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    pendingPrepareCount += 1;
    resetInternal(true, paramBoolean1, paramBoolean2);
    loadControl.onPrepared();
    mediaSource = paramMediaSource;
    setState(2);
    paramMediaSource.prepareSource(player, true, this, bandwidthMeter.getTransferListener());
    handler.sendEmptyMessage(2);
  }
  
  private void releaseInternal()
  {
    resetInternal(true, true, true);
    loadControl.onReleased();
    setState(1);
    internalPlaybackThread.quit();
    try
    {
      released = true;
      notifyAll();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private boolean rendererWaitingForNextStream(Renderer paramRenderer)
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getReadingPeriod();
    return (next != null) && (next.prepared) && (paramRenderer.hasReadStreamToEnd());
  }
  
  private void reselectTracksInternal()
    throws ExoPlaybackException
  {
    if (!queue.hasPlayingPeriod()) {
      return;
    }
    float f = mediaClock.getPlaybackParameters().speed;
    MediaPeriodHolder localMediaPeriodHolder = queue.getPlayingPeriod();
    Object localObject = queue.getReadingPeriod();
    int i = 1;
    while (localMediaPeriodHolder != null)
    {
      if (!prepared) {
        return;
      }
      if (localMediaPeriodHolder.selectTracks(f))
      {
        if (i != 0)
        {
          localMediaPeriodHolder = queue.getPlayingPeriod();
          boolean bool = queue.removeAfter(localMediaPeriodHolder);
          localObject = new boolean[renderers.length];
          long l = localMediaPeriodHolder.applyTrackSelection(playbackInfo.positionUs, bool, (boolean[])localObject);
          if ((playbackInfo.playbackState != 4) && (l != playbackInfo.positionUs))
          {
            playbackInfo = playbackInfo.copyWithNewPosition(playbackInfo.periodId, l, playbackInfo.contentPositionUs, getTotalBufferedDurationUs());
            playbackInfoUpdate.setPositionDiscontinuity(4);
            resetRendererPosition(l);
          }
          boolean[] arrayOfBoolean = new boolean[renderers.length];
          i = 0;
          int k;
          for (int j = 0; i < renderers.length; j = k)
          {
            Renderer localRenderer = renderers[i];
            if (localRenderer.getState() != 0) {
              bool = true;
            } else {
              bool = false;
            }
            arrayOfBoolean[i] = bool;
            SampleStream localSampleStream = sampleStreams[i];
            k = j;
            if (localSampleStream != null) {
              k = j + 1;
            }
            if (arrayOfBoolean[i] != 0) {
              if (localSampleStream != localRenderer.getStream()) {
                disableRenderer(localRenderer);
              } else if (localObject[i] != 0) {
                localRenderer.resetPosition(rendererPositionUs);
              }
            }
            i += 1;
          }
          playbackInfo = playbackInfo.copyWithTrackInfo(trackGroups, trackSelectorResult);
          enableRenderers(arrayOfBoolean, j);
        }
        else
        {
          queue.removeAfter(localMediaPeriodHolder);
          if (prepared) {
            localMediaPeriodHolder.applyTrackSelection(Math.max(info.startPositionUs, localMediaPeriodHolder.toPeriodTime(rendererPositionUs)), false);
          }
        }
        handleLoadingMediaPeriodChanged(true);
        if (playbackInfo.playbackState == 4) {
          break;
        }
        maybeContinueLoading();
        updatePlaybackPositions();
        handler.sendEmptyMessage(2);
        return;
      }
      if (localMediaPeriodHolder == localObject) {
        i = 0;
      }
      localMediaPeriodHolder = next;
    }
  }
  
  private void resetInternal(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    handler.removeMessages(2);
    rebuffering = false;
    mediaClock.stop();
    rendererPositionUs = 0L;
    Object localObject1 = enabledRenderers;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = localObject1[i];
      try
      {
        disableRenderer(localRenderer);
      }
      catch (ExoPlaybackException|RuntimeException localExoPlaybackException)
      {
        Log.e("ExoPlayerImplInternal", "Stop failed.", localExoPlaybackException);
      }
      i += 1;
    }
    enabledRenderers = new Renderer[0];
    queue.clear(paramBoolean2 ^ true);
    setIsLoading(false);
    if (paramBoolean2) {
      pendingInitialSeekPosition = null;
    }
    if (paramBoolean3)
    {
      queue.setTimeline(Timeline.EMPTY);
      localObject1 = pendingMessages.iterator();
      while (((Iterator)localObject1).hasNext()) {
        nextmessage.markAsProcessed(false);
      }
      pendingMessages.clear();
      nextPendingMessageIndex = 0;
    }
    if (paramBoolean2) {}
    for (localObject1 = playbackInfo.getDummyFirstMediaPeriodId(shuffleModeEnabled, window);; localObject1 = playbackInfo.periodId) {
      break;
    }
    long l2 = -9223372036854775807L;
    long l1;
    if (paramBoolean2) {
      l1 = -9223372036854775807L;
    } else {
      l1 = playbackInfo.positionUs;
    }
    if (!paramBoolean2) {
      for (;;)
      {
        l2 = playbackInfo.contentPositionUs;
      }
    }
    if (paramBoolean3) {}
    for (Timeline localTimeline = Timeline.EMPTY;; localTimeline = playbackInfo.timeline) {
      break;
    }
    Object localObject2;
    if (paramBoolean3) {
      localObject2 = null;
    } else {
      localObject2 = playbackInfo.manifest;
    }
    i = playbackInfo.playbackState;
    if (paramBoolean3) {}
    for (TrackGroupArray localTrackGroupArray = TrackGroupArray.EMPTY;; localTrackGroupArray = playbackInfo.trackGroups) {
      break;
    }
    if (paramBoolean3) {}
    for (TrackSelectorResult localTrackSelectorResult = emptyTrackSelectorResult;; localTrackSelectorResult = playbackInfo.trackSelectorResult) {
      break;
    }
    playbackInfo = new PlaybackInfo(localTimeline, localObject2, (MediaSource.MediaPeriodId)localObject1, l1, l2, i, false, localTrackGroupArray, localTrackSelectorResult, (MediaSource.MediaPeriodId)localObject1, l1, 0L, l1);
    if ((paramBoolean1) && (mediaSource != null))
    {
      mediaSource.releaseSource(this);
      mediaSource = null;
    }
  }
  
  private void resetRendererPosition(long paramLong)
    throws ExoPlaybackException
  {
    if (queue.hasPlayingPeriod()) {
      paramLong = queue.getPlayingPeriod().toRendererTime(paramLong);
    }
    rendererPositionUs = paramLong;
    mediaClock.resetPosition(rendererPositionUs);
    Renderer[] arrayOfRenderer = enabledRenderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      arrayOfRenderer[i].resetPosition(rendererPositionUs);
      i += 1;
    }
  }
  
  private boolean resolvePendingMessagePosition(PendingMessageInfo paramPendingMessageInfo)
  {
    if (resolvedPeriodUid == null)
    {
      Pair localPair = resolveSeekPosition(new SeekPosition(message.getTimeline(), message.getWindowIndex(), IpAddress.msToUs(message.getPositionMs())), false);
      if (localPair == null) {
        return false;
      }
      paramPendingMessageInfo.setResolvedPosition(playbackInfo.timeline.getIndexOfPeriod(first), ((Long)second).longValue(), first);
    }
    else
    {
      int i = playbackInfo.timeline.getIndexOfPeriod(resolvedPeriodUid);
      if (i == -1) {
        return false;
      }
      resolvedPeriodIndex = i;
    }
    return true;
  }
  
  private void resolvePendingMessagePositions()
  {
    int i = pendingMessages.size() - 1;
    while (i >= 0)
    {
      if (!resolvePendingMessagePosition((PendingMessageInfo)pendingMessages.get(i)))
      {
        pendingMessages.get(i)).message.markAsProcessed(false);
        pendingMessages.remove(i);
      }
      i -= 1;
    }
    Collections.sort(pendingMessages);
  }
  
  private Pair resolveSeekPosition(SeekPosition paramSeekPosition, boolean paramBoolean)
  {
    Timeline localTimeline = playbackInfo.timeline;
    Object localObject2 = timeline;
    if (localTimeline.isEmpty()) {
      return null;
    }
    Object localObject1 = localObject2;
    if (((Timeline)localObject2).isEmpty()) {
      localObject1 = localTimeline;
    }
    localObject2 = window;
    Timeline.Period localPeriod = period;
    int i = windowIndex;
    long l = windowPositionUs;
    try
    {
      localObject2 = ((Timeline)localObject1).getPeriodPosition((Timeline.Window)localObject2, localPeriod, i, l);
      if (localTimeline == localObject1) {
        return localObject2;
      }
      i = localTimeline.getIndexOfPeriod(first);
      if (i != -1) {
        return localObject2;
      }
      if (paramBoolean)
      {
        if (resolveSubsequentPeriod(first, (Timeline)localObject1, localTimeline) == null) {
          break label175;
        }
        return getPeriodPosition(localTimeline, getPeriodperiod).windowIndex, -9223372036854775807L);
      }
      return null;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      for (;;) {}
    }
    throw new IllegalSeekPositionException(localTimeline, windowIndex, windowPositionUs);
    label175:
    return null;
  }
  
  private Object resolveSubsequentPeriod(Object paramObject, Timeline paramTimeline1, Timeline paramTimeline2)
  {
    int k = paramTimeline1.getIndexOfPeriod(paramObject);
    int n = paramTimeline1.getPeriodCount();
    int i = 0;
    int j = -1;
    while ((i < n) && (j == -1))
    {
      int m = paramTimeline1.getNextPeriodIndex(k, period, window, repeatMode, shuffleModeEnabled);
      k = m;
      if (m == -1) {
        break;
      }
      j = paramTimeline2.getIndexOfPeriod(paramTimeline1.getUidOfPeriod(m));
      i += 1;
    }
    if (j == -1) {
      return null;
    }
    return paramTimeline2.getUidOfPeriod(j);
  }
  
  private void scheduleNextWork(long paramLong1, long paramLong2)
  {
    handler.removeMessages(2);
    handler.sendEmptyMessageAtTime(2, paramLong1 + paramLong2);
  }
  
  private void seekToCurrentPosition(boolean paramBoolean)
    throws ExoPlaybackException
  {
    MediaSource.MediaPeriodId localMediaPeriodId = queue.getPlayingPeriod().info.anchor;
    long l = seekToPeriodPosition(localMediaPeriodId, playbackInfo.positionUs, true);
    if (l != playbackInfo.positionUs)
    {
      playbackInfo = playbackInfo.copyWithNewPosition(localMediaPeriodId, l, playbackInfo.contentPositionUs, getTotalBufferedDurationUs());
      if (paramBoolean) {
        playbackInfoUpdate.setPositionDiscontinuity(4);
      }
    }
  }
  
  private void seekToInternal(SeekPosition paramSeekPosition)
    throws ExoPlaybackException
  {
    Object localObject1 = playbackInfoUpdate;
    int j = 1;
    ((PlaybackInfoUpdate)localObject1).incrementPendingOperationAcks(1);
    Object localObject2 = resolveSeekPosition(paramSeekPosition, true);
    long l2;
    if (localObject2 == null)
    {
      localObject1 = playbackInfo.getDummyFirstMediaPeriodId(shuffleModeEnabled, window);
      l1 = -9223372036854775807L;
      l2 = -9223372036854775807L;
    }
    int i;
    long l3;
    for (;;)
    {
      i = 1;
      l3 = l2;
      break label149;
      localObject1 = first;
      l2 = ((Long)second).longValue();
      localObject1 = queue.resolveMediaPeriodIdForAds(localObject1, l2);
      if (!((MediaSource.MediaPeriodId)localObject1).isAd()) {
        break;
      }
      l1 = 0L;
    }
    long l1 = ((Long)second).longValue();
    if (windowPositionUs == -9223372036854775807L)
    {
      i = 1;
      l3 = l2;
    }
    else
    {
      i = 0;
      l3 = l2;
    }
    try
    {
      label149:
      localObject2 = mediaSource;
      if (localObject2 != null)
      {
        int k = pendingPrepareCount;
        if (k <= 0)
        {
          if (l1 == -9223372036854775807L)
          {
            setState(4);
            resetInternal(false, true, false);
            break label373;
          }
          boolean bool = ((MediaSource.MediaPeriodId)localObject1).equals(playbackInfo.periodId);
          if (bool)
          {
            paramSeekPosition = queue.getPlayingPeriod();
            if ((paramSeekPosition != null) && (l1 != 0L)) {
              l2 = mediaPeriod.getAdjustedSeekPositionUs(l1, seekParameters);
            } else {
              l2 = l1;
            }
            long l4 = IpAddress.usToMs(l2);
            long l5 = IpAddress.usToMs(playbackInfo.positionUs);
            if (l4 == l5)
            {
              l2 = playbackInfo.positionUs;
              playbackInfo = playbackInfo.copyWithNewPosition((MediaSource.MediaPeriodId)localObject1, l2, l3, getTotalBufferedDurationUs());
              if (i == 0) {
                return;
              }
              playbackInfoUpdate.setPositionDiscontinuity(2);
            }
          }
          else
          {
            l2 = l1;
          }
          l2 = seekToPeriodPosition((MediaSource.MediaPeriodId)localObject1, l2);
          if (l1 == l2) {
            j = 0;
          }
          i |= j;
          l1 = l2;
          break label373;
        }
      }
      pendingInitialSeekPosition = paramSeekPosition;
      label373:
      playbackInfo = playbackInfo.copyWithNewPosition((MediaSource.MediaPeriodId)localObject1, l1, l3, getTotalBufferedDurationUs());
      if (i != 0)
      {
        playbackInfoUpdate.setPositionDiscontinuity(2);
        return;
      }
    }
    catch (Throwable paramSeekPosition)
    {
      playbackInfo = playbackInfo.copyWithNewPosition((MediaSource.MediaPeriodId)localObject1, l1, l3, getTotalBufferedDurationUs());
      if (i != 0) {
        playbackInfoUpdate.setPositionDiscontinuity(2);
      }
      throw paramSeekPosition;
    }
  }
  
  private long seekToPeriodPosition(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
    throws ExoPlaybackException
  {
    boolean bool;
    if (queue.getPlayingPeriod() != queue.getReadingPeriod()) {
      bool = true;
    } else {
      bool = false;
    }
    return seekToPeriodPosition(paramMediaPeriodId, paramLong, bool);
  }
  
  private long seekToPeriodPosition(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    stopRenderers();
    rebuffering = false;
    setState(2);
    MediaPeriodHolder localMediaPeriodHolder2 = queue.getPlayingPeriod();
    MediaPeriodHolder localMediaPeriodHolder3 = localMediaPeriodHolder2;
    for (MediaPeriodHolder localMediaPeriodHolder1 = localMediaPeriodHolder2; localMediaPeriodHolder1 != null; localMediaPeriodHolder1 = queue.advancePlayingPeriod()) {
      if ((paramMediaPeriodId.equals(info.anchor)) && (prepared))
      {
        queue.removeAfter(localMediaPeriodHolder1);
        break;
      }
    }
    if ((localMediaPeriodHolder2 != localMediaPeriodHolder1) || (paramBoolean))
    {
      paramMediaPeriodId = enabledRenderers;
      int j = paramMediaPeriodId.length;
      int i = 0;
      while (i < j)
      {
        disableRenderer(paramMediaPeriodId[i]);
        i += 1;
      }
      enabledRenderers = new Renderer[0];
      localMediaPeriodHolder3 = null;
    }
    if (localMediaPeriodHolder1 != null)
    {
      updatePlayingPeriodRenderers(localMediaPeriodHolder3);
      long l = paramLong;
      if (hasEnabledTracks)
      {
        paramLong = mediaPeriod.seekToUs(paramLong);
        l = paramLong;
        mediaPeriod.discardBuffer(paramLong - backBufferDurationUs, retainBackBufferFromKeyframe);
      }
      resetRendererPosition(l);
      maybeContinueLoading();
      paramLong = l;
    }
    else
    {
      queue.clear(true);
      playbackInfo = playbackInfo.copyWithTrackInfo(TrackGroupArray.EMPTY, emptyTrackSelectorResult);
      resetRendererPosition(paramLong);
    }
    handleLoadingMediaPeriodChanged(false);
    handler.sendEmptyMessage(2);
    return paramLong;
  }
  
  private void sendMessageInternal(PlayerMessage paramPlayerMessage)
    throws ExoPlaybackException
  {
    if (paramPlayerMessage.getPositionMs() == -9223372036854775807L)
    {
      sendMessageToTarget(paramPlayerMessage);
      return;
    }
    if ((mediaSource != null) && (pendingPrepareCount <= 0))
    {
      PendingMessageInfo localPendingMessageInfo = new PendingMessageInfo(paramPlayerMessage);
      if (resolvePendingMessagePosition(localPendingMessageInfo))
      {
        pendingMessages.add(localPendingMessageInfo);
        Collections.sort(pendingMessages);
        return;
      }
      paramPlayerMessage.markAsProcessed(false);
      return;
    }
    pendingMessages.add(new PendingMessageInfo(paramPlayerMessage));
  }
  
  private void sendMessageToTarget(PlayerMessage paramPlayerMessage)
    throws ExoPlaybackException
  {
    if (paramPlayerMessage.getHandler().getLooper() == handler.getLooper())
    {
      deliverMessage(paramPlayerMessage);
      if ((playbackInfo.playbackState == 3) || (playbackInfo.playbackState == 2)) {
        handler.sendEmptyMessage(2);
      }
    }
    else
    {
      handler.obtainMessage(15, paramPlayerMessage).sendToTarget();
    }
  }
  
  private void sendMessageToTargetThread(PlayerMessage paramPlayerMessage)
  {
    paramPlayerMessage.getHandler().post(new -..Lambda.ExoPlayerImplInternal.XwFxncwlyfAWA4k618O8BNtCsr0(this, paramPlayerMessage));
  }
  
  private void setIsLoading(boolean paramBoolean)
  {
    if (playbackInfo.isLoading != paramBoolean) {
      playbackInfo = playbackInfo.copyWithIsLoading(paramBoolean);
    }
  }
  
  private void setPlayWhenReadyInternal(boolean paramBoolean)
    throws ExoPlaybackException
  {
    rebuffering = false;
    playWhenReady = paramBoolean;
    if (!paramBoolean)
    {
      stopRenderers();
      updatePlaybackPositions();
      return;
    }
    if (playbackInfo.playbackState == 3)
    {
      startRenderers();
      handler.sendEmptyMessage(2);
      return;
    }
    if (playbackInfo.playbackState == 2) {
      handler.sendEmptyMessage(2);
    }
  }
  
  private void setPlaybackParametersInternal(PlaybackParameters paramPlaybackParameters)
  {
    mediaClock.setPlaybackParameters(paramPlaybackParameters);
  }
  
  private void setRepeatModeInternal(int paramInt)
    throws ExoPlaybackException
  {
    repeatMode = paramInt;
    if (!queue.updateRepeatMode(paramInt)) {
      seekToCurrentPosition(true);
    }
    handleLoadingMediaPeriodChanged(false);
  }
  
  private void setSeekParametersInternal(SeekParameters paramSeekParameters)
  {
    seekParameters = paramSeekParameters;
  }
  
  private void setShuffleModeEnabledInternal(boolean paramBoolean)
    throws ExoPlaybackException
  {
    shuffleModeEnabled = paramBoolean;
    if (!queue.updateShuffleModeEnabled(paramBoolean)) {
      seekToCurrentPosition(true);
    }
    handleLoadingMediaPeriodChanged(false);
  }
  
  private void setState(int paramInt)
  {
    if (playbackInfo.playbackState != paramInt) {
      playbackInfo = playbackInfo.copyWithPlaybackState(paramInt);
    }
  }
  
  private boolean shouldTransitionToReadyState(boolean paramBoolean)
  {
    if (enabledRenderers.length == 0) {
      return isTimelineReady();
    }
    if (!paramBoolean) {
      return false;
    }
    if (!playbackInfo.isLoading) {
      return true;
    }
    MediaPeriodHolder localMediaPeriodHolder = queue.getLoadingPeriod();
    int i;
    if ((localMediaPeriodHolder.isFullyBuffered()) && (info.isFinal)) {
      i = 1;
    } else {
      i = 0;
    }
    return (i != 0) || (loadControl.shouldStartPlayback(getTotalBufferedDurationUs(), mediaClock.getPlaybackParameters().speed, rebuffering));
  }
  
  private void startRenderers()
    throws ExoPlaybackException
  {
    int i = 0;
    rebuffering = false;
    mediaClock.start();
    Renderer[] arrayOfRenderer = enabledRenderers;
    int j = arrayOfRenderer.length;
    while (i < j)
    {
      arrayOfRenderer[i].start();
      i += 1;
    }
  }
  
  private void stopInternal(boolean paramBoolean1, boolean paramBoolean2)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  private void stopRenderers()
    throws ExoPlaybackException
  {
    mediaClock.stop();
    Renderer[] arrayOfRenderer = enabledRenderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      ensureStopped(arrayOfRenderer[i]);
      i += 1;
    }
  }
  
  private void updateLoadControlTrackSelection(TrackGroupArray paramTrackGroupArray, TrackSelectorResult paramTrackSelectorResult)
  {
    loadControl.onTracksSelected(renderers, paramTrackGroupArray, selections);
  }
  
  private void updatePeriods()
    throws ExoPlaybackException, IOException
  {
    if (mediaSource == null) {
      return;
    }
    if (pendingPrepareCount > 0)
    {
      mediaSource.maybeThrowSourceInfoRefreshError();
      return;
    }
    maybeUpdateLoadingPeriod();
    Object localObject1 = queue.getLoadingPeriod();
    int j = 0;
    if ((localObject1 != null) && (!((MediaPeriodHolder)localObject1).isFullyBuffered()))
    {
      if (!playbackInfo.isLoading) {
        maybeContinueLoading();
      }
    }
    else {
      setIsLoading(false);
    }
    if (!queue.hasPlayingPeriod()) {
      return;
    }
    localObject1 = queue.getPlayingPeriod();
    Object localObject3 = queue.getReadingPeriod();
    for (int i = 0; (playWhenReady) && (localObject1 != localObject3) && (rendererPositionUs >= next.getStartPositionRendererTime()); i = 1)
    {
      if (i != 0) {
        maybeNotifyPlaybackInfoChanged();
      }
      if (info.isLastInTimelinePeriod) {
        i = 0;
      } else {
        i = 3;
      }
      localObject2 = queue.advancePlayingPeriod();
      updatePlayingPeriodRenderers((MediaPeriodHolder)localObject1);
      playbackInfo = playbackInfo.copyWithNewPosition(info.anchor, info.startPositionUs, info.contentPositionUs, getTotalBufferedDurationUs());
      playbackInfoUpdate.setPositionDiscontinuity(i);
      updatePlaybackPositions();
      localObject1 = localObject2;
    }
    if (info.isFinal)
    {
      i = j;
      while (i < renderers.length)
      {
        localObject1 = renderers[i];
        localObject2 = sampleStreams[i];
        if ((localObject2 != null) && (((Renderer)localObject1).getStream() == localObject2) && (((Renderer)localObject1).hasReadStreamToEnd())) {
          ((Renderer)localObject1).setCurrentStreamFinal();
        }
        i += 1;
      }
      return;
    }
    if (next == null) {
      return;
    }
    i = 0;
    while (i < renderers.length)
    {
      localObject1 = renderers[i];
      localObject2 = sampleStreams[i];
      if (((Renderer)localObject1).getStream() == localObject2)
      {
        if ((localObject2 != null) && (!((Renderer)localObject1).hasReadStreamToEnd())) {
          return;
        }
        i += 1;
      }
      else
      {
        return;
      }
    }
    if (!next.prepared)
    {
      maybeThrowPeriodPrepareError();
      return;
    }
    localObject1 = trackSelectorResult;
    Object localObject2 = queue.advanceReadingPeriod();
    localObject3 = trackSelectorResult;
    if (mediaPeriod.readDiscontinuity() != -9223372036854775807L) {
      i = 1;
    } else {
      i = 0;
    }
    j = 0;
    while (j < renderers.length)
    {
      Renderer localRenderer = renderers[j];
      if (((TrackSelectorResult)localObject1).isRendererEnabled(j)) {
        if (i != 0)
        {
          localRenderer.setCurrentStreamFinal();
        }
        else if (!localRenderer.isCurrentStreamFinal())
        {
          TrackSelection localTrackSelection = selections.getChapters(j);
          boolean bool = ((TrackSelectorResult)localObject3).isRendererEnabled(j);
          int k;
          if (rendererCapabilities[j].getTrackType() == 6) {
            k = 1;
          } else {
            k = 0;
          }
          RendererConfiguration localRendererConfiguration1 = rendererConfigurations[j];
          RendererConfiguration localRendererConfiguration2 = rendererConfigurations[j];
          if ((bool) && (localRendererConfiguration2.equals(localRendererConfiguration1)) && (k == 0)) {
            localRenderer.replaceStream(getFormats(localTrackSelection), sampleStreams[j], ((MediaPeriodHolder)localObject2).getRendererOffset());
          } else {
            localRenderer.setCurrentStreamFinal();
          }
        }
      }
      j += 1;
    }
  }
  
  private void updatePlaybackPositions()
    throws ExoPlaybackException
  {
    if (!queue.hasPlayingPeriod()) {
      return;
    }
    MediaPeriodHolder localMediaPeriodHolder = queue.getPlayingPeriod();
    long l = mediaPeriod.readDiscontinuity();
    if (l != -9223372036854775807L)
    {
      resetRendererPosition(l);
      if (l != playbackInfo.positionUs)
      {
        playbackInfo = playbackInfo.copyWithNewPosition(playbackInfo.periodId, l, playbackInfo.contentPositionUs, getTotalBufferedDurationUs());
        playbackInfoUpdate.setPositionDiscontinuity(4);
      }
    }
    else
    {
      rendererPositionUs = mediaClock.syncAndGetPositionUs();
      l = localMediaPeriodHolder.toPeriodTime(rendererPositionUs);
      maybeTriggerPendingMessages(playbackInfo.positionUs, l);
      playbackInfo.positionUs = l;
    }
    localMediaPeriodHolder = queue.getLoadingPeriod();
    playbackInfo.bufferedPositionUs = localMediaPeriodHolder.getBufferedPositionUs();
    playbackInfo.totalBufferedDurationUs = getTotalBufferedDurationUs();
  }
  
  private void updatePlayingPeriodRenderers(MediaPeriodHolder paramMediaPeriodHolder)
    throws ExoPlaybackException
  {
    MediaPeriodHolder localMediaPeriodHolder = queue.getPlayingPeriod();
    if (localMediaPeriodHolder != null)
    {
      if (paramMediaPeriodHolder == localMediaPeriodHolder) {
        return;
      }
      boolean[] arrayOfBoolean = new boolean[renderers.length];
      int i = 0;
      int k;
      for (int j = 0; i < renderers.length; j = k)
      {
        Renderer localRenderer = renderers[i];
        int m;
        if (localRenderer.getState() != 0) {
          m = 1;
        } else {
          m = 0;
        }
        arrayOfBoolean[i] = m;
        k = j;
        if (trackSelectorResult.isRendererEnabled(i)) {
          k = j + 1;
        }
        if ((arrayOfBoolean[i] != 0) && ((!trackSelectorResult.isRendererEnabled(i)) || ((localRenderer.isCurrentStreamFinal()) && (localRenderer.getStream() == sampleStreams[i])))) {
          disableRenderer(localRenderer);
        }
        i += 1;
      }
      playbackInfo = playbackInfo.copyWithTrackInfo(trackGroups, trackSelectorResult);
      enableRenderers(arrayOfBoolean, j);
    }
  }
  
  private void updateTrackSelectionPlaybackSpeed(float paramFloat)
  {
    for (MediaPeriodHolder localMediaPeriodHolder = queue.getFrontPeriod(); localMediaPeriodHolder != null; localMediaPeriodHolder = next) {
      if (trackSelectorResult != null)
      {
        TrackSelection[] arrayOfTrackSelection = trackSelectorResult.selections.getAll();
        int j = arrayOfTrackSelection.length;
        int i = 0;
        while (i < j)
        {
          TrackSelection localTrackSelection = arrayOfTrackSelection[i];
          if (localTrackSelection != null) {
            localTrackSelection.onPlaybackSpeed(paramFloat);
          }
          i += 1;
        }
      }
    }
  }
  
  public Looper getPlaybackLooper()
  {
    return internalPlaybackThread.getLooper();
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    try
    {
      i = what;
      switch (i)
      {
      default: 
        return false;
      }
    }
    catch (RuntimeException paramMessage)
    {
      try
      {
        handlePlaybackParameters(paramMessage);
        break label459;
        paramMessage = (PlayerMessage)obj;
        sendMessageToTargetThread(paramMessage);
        break label459;
        paramMessage = (PlayerMessage)obj;
        sendMessageInternal(paramMessage);
        break label459;
        int i = arg1;
        boolean bool1;
        if (i != 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        setShuffleModeEnabledInternal(bool1);
        break label459;
        i = arg1;
        setRepeatModeInternal(i);
        break label459;
        reselectTracksInternal();
        break label459;
        paramMessage = (MediaPeriod)obj;
        handleContinueLoadingRequested(paramMessage);
        break label459;
        paramMessage = (MediaPeriod)obj;
        handlePeriodPrepared(paramMessage);
        break label459;
        paramMessage = (MediaSourceRefreshInfo)obj;
        handleSourceInfoRefreshed(paramMessage);
        break label459;
        releaseInternal();
        return true;
        i = arg1;
        if (i != 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        stopInternal(bool1, true);
        break label459;
        paramMessage = (SeekParameters)obj;
        setSeekParametersInternal(paramMessage);
        break label459;
        paramMessage = (PlaybackParameters)obj;
        setPlaybackParametersInternal(paramMessage);
        break label459;
        paramMessage = (SeekPosition)obj;
        seekToInternal(paramMessage);
        break label459;
        doSomeWork();
        break label459;
        i = arg1;
        if (i != 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        setPlayWhenReadyInternal(bool1);
        break label459;
        MediaSource localMediaSource = (MediaSource)obj;
        i = arg1;
        if (i != 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        i = arg2;
        boolean bool2;
        if (i != 0) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        prepareInternal(localMediaSource, bool1, bool2);
        label459:
        maybeNotifyPlaybackInfoChanged();
        return true;
      }
      catch (IOException paramMessage)
      {
        Log.e("ExoPlayerImplInternal", "Source error.", paramMessage);
        stopInternal(false, false);
        eventHandler.obtainMessage(2, ExoPlaybackException.createForSource(paramMessage)).sendToTarget();
        maybeNotifyPlaybackInfoChanged();
        return true;
      }
      catch (ExoPlaybackException paramMessage)
      {
        Log.e("ExoPlayerImplInternal", "Playback error.", paramMessage);
        stopInternal(false, false);
        eventHandler.obtainMessage(2, paramMessage).sendToTarget();
        maybeNotifyPlaybackInfoChanged();
      }
      paramMessage = paramMessage;
      Log.e("ExoPlayerImplInternal", "Internal runtime error.", paramMessage);
      stopInternal(false, false);
      eventHandler.obtainMessage(2, ExoPlaybackException.createForUnexpected(paramMessage)).sendToTarget();
      maybeNotifyPlaybackInfoChanged();
      return true;
    }
    paramMessage = (PlaybackParameters)obj;
    return true;
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    handler.obtainMessage(10, paramMediaPeriod).sendToTarget();
  }
  
  public void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters)
  {
    handler.obtainMessage(16, paramPlaybackParameters).sendToTarget();
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    handler.obtainMessage(9, paramMediaPeriod).sendToTarget();
  }
  
  public void onSourceInfoRefreshed(MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    handler.obtainMessage(8, new MediaSourceRefreshInfo(paramMediaSource, paramTimeline, paramObject)).sendToTarget();
  }
  
  public void onTrackSelectionsInvalidated()
  {
    handler.sendEmptyMessage(11);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void release()
  {
    for (;;)
    {
      try
      {
        boolean bool = released;
        if (bool) {
          return;
        }
        handler.sendEmptyMessage(7);
        i = 0;
        bool = released;
        if (bool) {
          break;
        }
      }
      catch (Throwable localThrowable)
      {
        int i;
        throw localThrowable;
      }
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        continue;
      }
      i = 1;
    }
    if (i != 0) {
      Thread.currentThread().interrupt();
    }
  }
  
  public void seekTo(Timeline paramTimeline, int paramInt, long paramLong)
  {
    handler.obtainMessage(3, new SeekPosition(paramTimeline, paramInt, paramLong)).sendToTarget();
  }
  
  public void sendMessage(PlayerMessage paramPlayerMessage)
  {
    try
    {
      if (released)
      {
        Log.w("ExoPlayerImplInternal", "Ignoring messages sent after release.");
        paramPlayerMessage.markAsProcessed(false);
        return;
      }
      handler.obtainMessage(14, paramPlayerMessage).sendToTarget();
      return;
    }
    catch (Throwable paramPlayerMessage)
    {
      throw paramPlayerMessage;
    }
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    handler.obtainMessage(4, paramPlaybackParameters).sendToTarget();
  }
  
  public void setRepeatMode(int paramInt)
  {
    handler.obtainMessage(12, paramInt, 0).sendToTarget();
  }
  
  public void setSeekParameters(SeekParameters paramSeekParameters)
  {
    handler.obtainMessage(5, paramSeekParameters).sendToTarget();
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void stop(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  private static final class MediaSourceRefreshInfo
  {
    public final Object manifest;
    public final MediaSource source;
    public final Timeline timeline;
    
    public MediaSourceRefreshInfo(MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
    {
      source = paramMediaSource;
      timeline = paramTimeline;
      manifest = paramObject;
    }
  }
  
  private static final class PendingMessageInfo
    implements Comparable<PendingMessageInfo>
  {
    public final PlayerMessage message;
    public int resolvedPeriodIndex;
    public long resolvedPeriodTimeUs;
    @Nullable
    public Object resolvedPeriodUid;
    
    public PendingMessageInfo(PlayerMessage paramPlayerMessage)
    {
      message = paramPlayerMessage;
    }
    
    public int compareTo(PendingMessageInfo paramPendingMessageInfo)
    {
      int i;
      if (resolvedPeriodUid == null) {
        i = 1;
      } else {
        i = 0;
      }
      int j;
      if (resolvedPeriodUid == null) {
        j = 1;
      } else {
        j = 0;
      }
      if (i != j)
      {
        if (resolvedPeriodUid != null) {
          return -1;
        }
      }
      else
      {
        if (resolvedPeriodUid == null) {
          return 0;
        }
        i = resolvedPeriodIndex - resolvedPeriodIndex;
        if (i != 0) {
          return i;
        }
        return Util.compareLong(resolvedPeriodTimeUs, resolvedPeriodTimeUs);
      }
      return 1;
    }
    
    public void setResolvedPosition(int paramInt, long paramLong, Object paramObject)
    {
      resolvedPeriodIndex = paramInt;
      resolvedPeriodTimeUs = paramLong;
      resolvedPeriodUid = paramObject;
    }
  }
  
  private static final class PlaybackInfoUpdate
  {
    private int discontinuityReason;
    private PlaybackInfo lastPlaybackInfo;
    private int operationAcks;
    private boolean positionDiscontinuity;
    
    private PlaybackInfoUpdate() {}
    
    public boolean hasPendingUpdate(PlaybackInfo paramPlaybackInfo)
    {
      return (paramPlaybackInfo != lastPlaybackInfo) || (operationAcks > 0) || (positionDiscontinuity);
    }
    
    public void incrementPendingOperationAcks(int paramInt)
    {
      operationAcks += paramInt;
    }
    
    public void reset(PlaybackInfo paramPlaybackInfo)
    {
      lastPlaybackInfo = paramPlaybackInfo;
      operationAcks = 0;
      positionDiscontinuity = false;
    }
    
    public void setPositionDiscontinuity(int paramInt)
    {
      boolean bool2 = positionDiscontinuity;
      boolean bool1 = true;
      if ((bool2) && (discontinuityReason != 4))
      {
        if (paramInt != 4) {
          bool1 = false;
        }
        Assertions.checkArgument(bool1);
        return;
      }
      positionDiscontinuity = true;
      discontinuityReason = paramInt;
    }
  }
  
  private static final class SeekPosition
  {
    public final Timeline timeline;
    public final int windowIndex;
    public final long windowPositionUs;
    
    public SeekPosition(Timeline paramTimeline, int paramInt, long paramLong)
    {
      timeline = paramTimeline;
      windowIndex = paramInt;
      windowPositionUs = paramLong;
    }
  }
}
