package com.google.android.exoplayer2.source.configurations;

import android.net.Uri;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.ClickListeners.AdsMediaSource.MediaSourceFactory;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.configurations.playlist.DefaultHlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.configurations.playlist.DefaultHlsPlaylistTracker;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistTracker.Factory;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.List;

public final class HlsMediaSource
  extends BaseMediaSource
  implements HlsPlaylistTracker.PrimaryPlaylistListener
{
  private final boolean allowChunklessPreparation;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final HlsDataSourceFactory dataSourceFactory;
  private final HlsExtractorFactory extractorFactory;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  private final Uri manifestUri;
  @Nullable
  private TransferListener mediaTransferListener;
  private final HlsPlaylistTracker playlistTracker;
  @Nullable
  private final Object sources;
  
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
  }
  
  public HlsMediaSource(Uri paramUri, HlsDataSourceFactory paramHlsDataSourceFactory, HlsExtractorFactory paramHlsExtractorFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener, ParsingLoadable.Parser paramParser)
  {
    this(paramUri, paramHlsDataSourceFactory, paramHlsExtractorFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(paramInt), new DefaultHlsPlaylistTracker(paramHlsDataSourceFactory, new DefaultLoadErrorHandlingPolicy(paramInt), paramParser), false, null);
    if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
      addEventListener(paramHandler, paramMediaSourceEventListener);
    }
  }
  
  private HlsMediaSource(Uri paramUri, HlsDataSourceFactory paramHlsDataSourceFactory, HlsExtractorFactory paramHlsExtractorFactory, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, HlsPlaylistTracker paramHlsPlaylistTracker, boolean paramBoolean, Object paramObject)
  {
    manifestUri = paramUri;
    dataSourceFactory = paramHlsDataSourceFactory;
    extractorFactory = paramHlsExtractorFactory;
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    playlistTracker = paramHlsPlaylistTracker;
    allowChunklessPreparation = paramBoolean;
    sources = paramObject;
  }
  
  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, new DefaultHlsDataSourceFactory(paramFactory), HlsExtractorFactory.DEFAULT, paramInt, paramHandler, paramMediaSourceEventListener, new HlsPlaylistParser());
  }
  
  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, 3, paramHandler, paramMediaSourceEventListener);
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    paramMediaPeriodId = createEventDispatcher(paramMediaPeriodId);
    return new HlsMediaPeriod(extractorFactory, playlistTracker, dataSourceFactory, mediaTransferListener, loadErrorHandlingPolicy, paramMediaPeriodId, paramAllocator, compositeSequenceableLoaderFactory, allowChunklessPreparation);
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
  }
  
  public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    long l1;
    if (hasProgramDateTime) {
      l1 = IpAddress.usToMs(startTimeUs);
    } else {
      l1 = -9223372036854775807L;
    }
    long l3;
    if ((playlistType != 2) && (playlistType != 1)) {
      l3 = -9223372036854775807L;
    } else {
      l3 = l1;
    }
    long l2 = startOffsetUs;
    Object localObject;
    if (playlistTracker.isLive())
    {
      long l6 = startTimeUs - playlistTracker.getInitialStartTimeUs();
      long l4;
      if (hasEndTag) {
        l4 = l6 + durationUs;
      } else {
        l4 = -9223372036854775807L;
      }
      localObject = segments;
      long l5 = l2;
      if (l2 == -9223372036854775807L)
      {
        if (((List)localObject).isEmpty()) {
          l2 = 0L;
        } else {
          l2 = getmax0size3relativeStartTimeUs;
        }
        l5 = l2;
      }
      localObject = new SinglePeriodTimeline(l3, l1, l4, durationUs, l6, l5, true, hasEndTag ^ true, sources);
    }
    else
    {
      if (l2 == -9223372036854775807L) {
        l2 = 0L;
      }
      localObject = new SinglePeriodTimeline(l3, l1, durationUs, durationUs, 0L, l2, true, false, sources);
    }
    refreshSourceInfo((Timeline)localObject, new HlsManifest(playlistTracker.getMasterPlaylist(), paramHlsMediaPlaylist));
  }
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    mediaTransferListener = paramTransferListener;
    paramExoPlayer = createEventDispatcher(null);
    playlistTracker.start(manifestUri, paramExoPlayer, this);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((HlsMediaPeriod)paramMediaPeriod).release();
  }
  
  public void releaseSourceInternal()
  {
    playlistTracker.stop();
  }
  
  public final class Factory
    implements AdsMediaSource.MediaSourceFactory
  {
    private boolean allowChunklessPreparation;
    private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private HlsExtractorFactory extractorFactory;
    private final HlsDataSourceFactory hlsDataSourceFactory;
    private boolean isCreateCalled;
    private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private Object mTag;
    private HlsPlaylistParserFactory playlistParserFactory;
    private HlsPlaylistTracker.Factory playlistTrackerFactory;
    
    public Factory()
    {
      hlsDataSourceFactory = ((HlsDataSourceFactory)Assertions.checkNotNull(this$1));
      playlistParserFactory = new DefaultHlsPlaylistParserFactory();
      playlistTrackerFactory = DefaultHlsPlaylistTracker.FACTORY;
      extractorFactory = HlsExtractorFactory.DEFAULT;
      loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
      compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
    }
    
    public Factory()
    {
      this();
    }
    
    public HlsMediaSource createMediaSource(Uri paramUri)
    {
      isCreateCalled = true;
      return new HlsMediaSource(paramUri, hlsDataSourceFactory, extractorFactory, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, playlistTrackerFactory.createTracker(hlsDataSourceFactory, loadErrorHandlingPolicy, playlistParserFactory), allowChunklessPreparation, mTag, null);
    }
    
    public HlsMediaSource createMediaSource(Uri paramUri, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      paramUri = createMediaSource(paramUri);
      if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
        paramUri.addEventListener(paramHandler, paramMediaSourceEventListener);
      }
      return paramUri;
    }
    
    public int[] getSupportedTypes()
    {
      return new int[] { 2 };
    }
    
    public Factory setAllowChunklessPreparation(boolean paramBoolean)
    {
      Assertions.checkState(isCreateCalled ^ true);
      allowChunklessPreparation = paramBoolean;
      return this;
    }
    
    public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory)
    {
      Assertions.checkState(isCreateCalled ^ true);
      compositeSequenceableLoaderFactory = ((CompositeSequenceableLoaderFactory)Assertions.checkNotNull(paramCompositeSequenceableLoaderFactory));
      return this;
    }
    
    public Factory setExtractorFactory(HlsExtractorFactory paramHlsExtractorFactory)
    {
      Assertions.checkState(isCreateCalled ^ true);
      extractorFactory = ((HlsExtractorFactory)Assertions.checkNotNull(paramHlsExtractorFactory));
      return this;
    }
    
    public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy)
    {
      Assertions.checkState(isCreateCalled ^ true);
      loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
      return this;
    }
    
    public Factory setMinLoadableRetryCount(int paramInt)
    {
      Assertions.checkState(isCreateCalled ^ true);
      loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy(paramInt);
      return this;
    }
    
    public Factory setPlaylistParserFactory(HlsPlaylistParserFactory paramHlsPlaylistParserFactory)
    {
      Assertions.checkState(isCreateCalled ^ true);
      playlistParserFactory = ((HlsPlaylistParserFactory)Assertions.checkNotNull(paramHlsPlaylistParserFactory));
      return this;
    }
    
    public Factory setPlaylistTrackerFactory(HlsPlaylistTracker.Factory paramFactory)
    {
      Assertions.checkState(isCreateCalled ^ true);
      playlistTrackerFactory = ((HlsPlaylistTracker.Factory)Assertions.checkNotNull(paramFactory));
      return this;
    }
    
    public Factory setTag(Object paramObject)
    {
      Assertions.checkState(isCreateCalled ^ true);
      mTag = paramObject;
      return this;
    }
  }
}
