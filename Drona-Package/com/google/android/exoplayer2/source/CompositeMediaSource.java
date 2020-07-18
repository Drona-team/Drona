package com.google.android.exoplayer2.source;

import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public abstract class CompositeMediaSource<T>
  extends BaseMediaSource
{
  private final HashMap<T, MediaSourceAndListener> childSources = new HashMap();
  @Nullable
  private Handler eventHandler;
  @Nullable
  private TransferListener mediaTransferListener;
  @Nullable
  private ExoPlayer player;
  
  protected CompositeMediaSource() {}
  
  protected MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(Object paramObject, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    return paramMediaPeriodId;
  }
  
  protected long getMediaTimeForChildMediaTime(Object paramObject, long paramLong)
  {
    return paramLong;
  }
  
  protected int getWindowIndexForChildWindowIndex(Object paramObject, int paramInt)
  {
    return paramInt;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    Iterator localIterator = childSources.values().iterator();
    while (localIterator.hasNext()) {
      nextmediaSource.maybeThrowSourceInfoRefreshError();
    }
  }
  
  protected abstract void onChildSourceInfoRefreshed(Object paramObject1, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject2);
  
  protected final void prepareChildSource(Object paramObject, MediaSource paramMediaSource)
  {
    Assertions.checkArgument(childSources.containsKey(paramObject) ^ true);
    -..Lambda.CompositeMediaSource.ahAPO18YbnzL6kKRAWdp4FR_Vco localAhAPO18YbnzL6kKRAWdp4FR_Vco = new -..Lambda.CompositeMediaSource.ahAPO18YbnzL6kKRAWdp4FR_Vco(this, paramObject);
    ForwardingEventListener localForwardingEventListener = new ForwardingEventListener(paramObject);
    childSources.put(paramObject, new MediaSourceAndListener(paramMediaSource, localAhAPO18YbnzL6kKRAWdp4FR_Vco, localForwardingEventListener));
    paramMediaSource.addEventListener((Handler)Assertions.checkNotNull(eventHandler), localForwardingEventListener);
    paramMediaSource.prepareSource((ExoPlayer)Assertions.checkNotNull(player), false, localAhAPO18YbnzL6kKRAWdp4FR_Vco, mediaTransferListener);
  }
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    player = paramExoPlayer;
    mediaTransferListener = paramTransferListener;
    eventHandler = new Handler();
  }
  
  protected final void releaseChildSource(Object paramObject)
  {
    paramObject = (MediaSourceAndListener)Assertions.checkNotNull(childSources.remove(paramObject));
    mediaSource.releaseSource(listener);
    mediaSource.removeEventListener(eventListener);
  }
  
  public void releaseSourceInternal()
  {
    Iterator localIterator = childSources.values().iterator();
    while (localIterator.hasNext())
    {
      MediaSourceAndListener localMediaSourceAndListener = (MediaSourceAndListener)localIterator.next();
      mediaSource.releaseSource(listener);
      mediaSource.removeEventListener(eventListener);
    }
    childSources.clear();
    player = null;
  }
  
  private final class ForwardingEventListener
    implements MediaSourceEventListener
  {
    private MediaSourceEventListener.EventDispatcher eventDispatcher = createEventDispatcher(null);
    private final T packageNames;
    
    public ForwardingEventListener(Object paramObject)
    {
      packageNames = paramObject;
    }
    
    private boolean maybeUpdateEventDispatcher(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      if (paramMediaPeriodId != null)
      {
        MediaSource.MediaPeriodId localMediaPeriodId = getMediaPeriodIdForChildMediaPeriodId(packageNames, paramMediaPeriodId);
        paramMediaPeriodId = localMediaPeriodId;
        if (localMediaPeriodId == null) {
          return false;
        }
      }
      else
      {
        paramMediaPeriodId = null;
      }
      paramInt = getWindowIndexForChildWindowIndex(packageNames, paramInt);
      if ((eventDispatcher.windowIndex != paramInt) || (!Util.areEqual(eventDispatcher.mediaPeriodId, paramMediaPeriodId))) {
        eventDispatcher = createEventDispatcher(paramInt, paramMediaPeriodId, 0L);
      }
      return true;
    }
    
    private MediaSourceEventListener.MediaLoadData maybeUpdateMediaLoadData(MediaSourceEventListener.MediaLoadData paramMediaLoadData)
    {
      long l1 = getMediaTimeForChildMediaTime(packageNames, mediaStartTimeMs);
      long l2 = getMediaTimeForChildMediaTime(packageNames, mediaEndTimeMs);
      if ((l1 == mediaStartTimeMs) && (l2 == mediaEndTimeMs)) {
        return paramMediaLoadData;
      }
      return new MediaSourceEventListener.MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, l1, l2);
    }
    
    public void onDownstreamFormatChanged(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.downstreamFormatChanged(maybeUpdateMediaLoadData(paramMediaLoadData));
      }
    }
    
    public void onLoadCanceled(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.loadCanceled(paramLoadEventInfo, maybeUpdateMediaLoadData(paramMediaLoadData));
      }
    }
    
    public void onLoadCompleted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.loadCompleted(paramLoadEventInfo, maybeUpdateMediaLoadData(paramMediaLoadData));
      }
    }
    
    public void onLoadError(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData, IOException paramIOException, boolean paramBoolean)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.loadError(paramLoadEventInfo, maybeUpdateMediaLoadData(paramMediaLoadData), paramIOException, paramBoolean);
      }
    }
    
    public void onLoadStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.loadStarted(paramLoadEventInfo, maybeUpdateMediaLoadData(paramMediaLoadData));
      }
    }
    
    public void onMediaPeriodCreated(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.mediaPeriodCreated();
      }
    }
    
    public void onMediaPeriodReleased(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.mediaPeriodReleased();
      }
    }
    
    public void onReadingStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.readingStarted();
      }
    }
    
    public void onUpstreamDiscarded(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData)
    {
      if (maybeUpdateEventDispatcher(paramInt, paramMediaPeriodId)) {
        eventDispatcher.upstreamDiscarded(maybeUpdateMediaLoadData(paramMediaLoadData));
      }
    }
  }
  
  private static final class MediaSourceAndListener
  {
    public final MediaSourceEventListener eventListener;
    public final MediaSource.SourceInfoRefreshListener listener;
    public final MediaSource mediaSource;
    
    public MediaSourceAndListener(MediaSource paramMediaSource, MediaSource.SourceInfoRefreshListener paramSourceInfoRefreshListener, MediaSourceEventListener paramMediaSourceEventListener)
    {
      mediaSource = paramMediaSource;
      listener = paramSourceInfoRefreshListener;
      eventListener = paramMediaSourceEventListener;
    }
  }
}
