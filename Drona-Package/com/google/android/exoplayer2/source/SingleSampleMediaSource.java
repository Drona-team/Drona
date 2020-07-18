package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class SingleSampleMediaSource
  extends BaseMediaSource
{
  private final DataSource.Factory dataSourceFactory;
  private final DataSpec dataSpec;
  private final long durationUs;
  private final Format format;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  private final Timeline timeline;
  @Nullable
  private TransferListener transferListener;
  private final boolean treatLoadErrorsAsEndOfStream;
  
  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, 3);
  }
  
  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, new DefaultLoadErrorHandlingPolicy(paramInt), false, null);
  }
  
  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2, boolean paramBoolean)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, new DefaultLoadErrorHandlingPolicy(paramInt1), paramBoolean, null);
    if ((paramHandler != null) && (paramEventListener != null)) {
      addEventListener(paramHandler, new EventListenerWrapper(paramEventListener, paramInt2));
    }
  }
  
  private SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, boolean paramBoolean, Object paramObject)
  {
    dataSourceFactory = paramFactory;
    format = paramFormat;
    durationUs = paramLong;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    treatLoadErrorsAsEndOfStream = paramBoolean;
    dataSpec = new DataSpec(paramUri, 3);
    timeline = new SinglePeriodTimeline(paramLong, true, false, paramObject);
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    return new SingleSampleMediaPeriod(dataSpec, dataSourceFactory, transferListener, format, durationUs, loadErrorHandlingPolicy, createEventDispatcher(paramMediaPeriodId), treatLoadErrorsAsEndOfStream);
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {}
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    transferListener = paramTransferListener;
    refreshSourceInfo(timeline, null);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((SingleSampleMediaPeriod)paramMediaPeriod).release();
  }
  
  public void releaseSourceInternal() {}
  
  @Deprecated
  public static abstract interface EventListener
  {
    public abstract void onLoadError(int paramInt, IOException paramIOException);
  }
  
  @Deprecated
  private static final class EventListenerWrapper
    extends DefaultMediaSourceEventListener
  {
    private final SingleSampleMediaSource.EventListener eventListener;
    private final int eventSourceId;
    
    public EventListenerWrapper(SingleSampleMediaSource.EventListener paramEventListener, int paramInt)
    {
      eventListener = ((SingleSampleMediaSource.EventListener)Assertions.checkNotNull(paramEventListener));
      eventSourceId = paramInt;
    }
    
    public void onLoadError(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData, IOException paramIOException, boolean paramBoolean)
    {
      eventListener.onLoadError(eventSourceId, paramIOException);
    }
  }
  
  public static final class Factory
  {
    private final DataSource.Factory dataSourceFactory;
    private boolean isCreateCalled;
    private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private Object mTag;
    private boolean treatLoadErrorsAsEndOfStream;
    
    public Factory(DataSource.Factory paramFactory)
    {
      dataSourceFactory = ((DataSource.Factory)Assertions.checkNotNull(paramFactory));
      loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
    }
    
    public SingleSampleMediaSource createMediaSource(Uri paramUri, Format paramFormat, long paramLong)
    {
      isCreateCalled = true;
      return new SingleSampleMediaSource(paramUri, dataSourceFactory, paramFormat, paramLong, loadErrorHandlingPolicy, treatLoadErrorsAsEndOfStream, mTag, null);
    }
    
    public SingleSampleMediaSource createMediaSource(Uri paramUri, Format paramFormat, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      paramUri = createMediaSource(paramUri, paramFormat, paramLong);
      if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
        paramUri.addEventListener(paramHandler, paramMediaSourceEventListener);
      }
      return paramUri;
    }
    
    public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy)
    {
      Assertions.checkState(isCreateCalled ^ true);
      loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
      return this;
    }
    
    public Factory setMinLoadableRetryCount(int paramInt)
    {
      return setLoadErrorHandlingPolicy(new DefaultLoadErrorHandlingPolicy(paramInt));
    }
    
    public Factory setTag(Object paramObject)
    {
      Assertions.checkState(isCreateCalled ^ true);
      mTag = paramObject;
      return this;
    }
    
    public Factory setTreatLoadErrorsAsEndOfStream(boolean paramBoolean)
    {
      Assertions.checkState(isCreateCalled ^ true);
      treatLoadErrorsAsEndOfStream = paramBoolean;
      return this;
    }
  }
}
