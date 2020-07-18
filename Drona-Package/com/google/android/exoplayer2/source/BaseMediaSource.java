package com.google.android.exoplayer2.source;

import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class BaseMediaSource
  implements MediaSource
{
  private final MediaSourceEventListener.EventDispatcher eventDispatcher = new MediaSourceEventListener.EventDispatcher();
  @Nullable
  private Object manifest;
  @Nullable
  private ExoPlayer player;
  private final ArrayList<MediaSource.SourceInfoRefreshListener> sourceInfoListeners = new ArrayList(1);
  @Nullable
  private Timeline timeline;
  
  public BaseMediaSource() {}
  
  public final void addEventListener(Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    eventDispatcher.addEventListener(paramHandler, paramMediaSourceEventListener);
  }
  
  protected final MediaSourceEventListener.EventDispatcher createEventDispatcher(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
  {
    return eventDispatcher.withParameters(paramInt, paramMediaPeriodId, paramLong);
  }
  
  protected final MediaSourceEventListener.EventDispatcher createEventDispatcher(MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    return eventDispatcher.withParameters(0, paramMediaPeriodId, 0L);
  }
  
  protected final MediaSourceEventListener.EventDispatcher createEventDispatcher(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
  {
    boolean bool;
    if (paramMediaPeriodId != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    return eventDispatcher.withParameters(0, paramMediaPeriodId, paramLong);
  }
  
  public final void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.SourceInfoRefreshListener paramSourceInfoRefreshListener)
  {
    prepareSource(paramExoPlayer, paramBoolean, paramSourceInfoRefreshListener, null);
  }
  
  public final void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.SourceInfoRefreshListener paramSourceInfoRefreshListener, TransferListener paramTransferListener)
  {
    boolean bool;
    if ((player != null) && (player != paramExoPlayer)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkArgument(bool);
    sourceInfoListeners.add(paramSourceInfoRefreshListener);
    if (player == null)
    {
      player = paramExoPlayer;
      prepareSourceInternal(paramExoPlayer, paramBoolean, paramTransferListener);
      return;
    }
    if (timeline != null) {
      paramSourceInfoRefreshListener.onSourceInfoRefreshed(this, timeline, manifest);
    }
  }
  
  protected abstract void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener);
  
  protected final void refreshSourceInfo(Timeline paramTimeline, Object paramObject)
  {
    timeline = paramTimeline;
    manifest = paramObject;
    Iterator localIterator = sourceInfoListeners.iterator();
    while (localIterator.hasNext()) {
      ((MediaSource.SourceInfoRefreshListener)localIterator.next()).onSourceInfoRefreshed(this, paramTimeline, paramObject);
    }
  }
  
  public final void releaseSource(MediaSource.SourceInfoRefreshListener paramSourceInfoRefreshListener)
  {
    sourceInfoListeners.remove(paramSourceInfoRefreshListener);
    if (sourceInfoListeners.isEmpty())
    {
      player = null;
      timeline = null;
      manifest = null;
      releaseSourceInternal();
    }
  }
  
  protected abstract void releaseSourceInternal();
  
  public final void removeEventListener(MediaSourceEventListener paramMediaSourceEventListener)
  {
    eventDispatcher.removeEventListener(paramMediaSourceEventListener);
  }
}