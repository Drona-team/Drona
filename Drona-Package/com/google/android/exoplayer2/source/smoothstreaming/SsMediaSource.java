package com.google.android.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.ClickListeners.AdsMediaSource.MediaSourceFactory;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsUtil;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower.Dummy;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;

public final class SsMediaSource
  extends BaseMediaSource
  implements Loader.Callback<ParsingLoadable<SsManifest>>
{
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000L;
  private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
  private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000L;
  private final SsChunkSource.Factory chunkSourceFactory;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final long livePresentationDelayMs;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  @Nullable
  private final Object localIndex;
  private SsManifest manifest;
  private DataSource manifestDataSource;
  private final DataSource.Factory manifestDataSourceFactory;
  private final MediaSourceEventListener.EventDispatcher manifestEventDispatcher;
  private long manifestLoadStartTimestamp;
  private Loader manifestLoader;
  private LoaderErrorThrower manifestLoaderErrorThrower;
  private final ParsingLoadable.Parser<? extends SsManifest> manifestParser;
  private Handler manifestRefreshHandler;
  private final Uri manifestUri;
  private final ArrayList<SsMediaPeriod> mediaPeriods;
  @Nullable
  private TransferListener mediaTransferListener;
  private final boolean sideloadedManifest;
  
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.smoothstreaming");
  }
  
  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, new SsManifestParser(), paramFactory1, paramInt, paramLong, paramHandler, paramMediaSourceEventListener);
  }
  
  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsChunkSource.Factory paramFactory1, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, paramFactory1, 3, 30000L, paramHandler, paramMediaSourceEventListener);
  }
  
  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, ParsingLoadable.Parser paramParser, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(null, paramUri, paramFactory, paramParser, paramFactory1, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(paramInt), paramLong, null);
    if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
      addEventListener(paramHandler, paramMediaSourceEventListener);
    }
  }
  
  private SsMediaSource(SsManifest paramSsManifest, Uri paramUri, DataSource.Factory paramFactory, ParsingLoadable.Parser paramParser, SsChunkSource.Factory paramFactory1, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, long paramLong, Object paramObject)
  {
    boolean bool2 = false;
    if ((paramSsManifest != null) && (isLive)) {
      bool1 = false;
    } else {
      bool1 = true;
    }
    Assertions.checkState(bool1);
    manifest = paramSsManifest;
    if (paramUri == null) {
      paramUri = null;
    } else {
      paramUri = SsUtil.fixManifestUri(paramUri);
    }
    manifestUri = paramUri;
    manifestDataSourceFactory = paramFactory;
    manifestParser = paramParser;
    chunkSourceFactory = paramFactory1;
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    livePresentationDelayMs = paramLong;
    manifestEventDispatcher = createEventDispatcher(null);
    localIndex = paramObject;
    boolean bool1 = bool2;
    if (paramSsManifest != null) {
      bool1 = true;
    }
    sideloadedManifest = bool1;
    mediaPeriods = new ArrayList();
  }
  
  public SsMediaSource(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramSsManifest, null, null, null, paramFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(paramInt), 30000L, null);
    if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
      addEventListener(paramHandler, paramMediaSourceEventListener);
    }
  }
  
  public SsMediaSource(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramSsManifest, paramFactory, 3, paramHandler, paramMediaSourceEventListener);
  }
  
  private void processManifest()
  {
    int i = 0;
    while (i < mediaPeriods.size())
    {
      ((SsMediaPeriod)mediaPeriods.get(i)).updateManifest(manifest);
      i += 1;
    }
    Object localObject1 = manifest.streamElements;
    int j = localObject1.length;
    long l2 = Long.MIN_VALUE;
    long l1 = Long.MAX_VALUE;
    i = 0;
    long l4;
    long l3;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      l4 = l2;
      l3 = l1;
      if (chunkCount > 0)
      {
        l3 = Math.min(l1, localObject2.getStartTimeUs(0));
        l4 = Math.max(l2, localObject2.getStartTimeUs(chunkCount - 1) + localObject2.getChunkDurationUs(chunkCount - 1));
      }
      i += 1;
      l2 = l4;
      l1 = l3;
    }
    if (l1 == Long.MAX_VALUE)
    {
      if (manifest.isLive) {
        l1 = -9223372036854775807L;
      } else {
        l1 = 0L;
      }
      localObject1 = new SinglePeriodTimeline(l1, 0L, 0L, 0L, true, manifest.isLive, localIndex);
    }
    else if (manifest.isLive)
    {
      l3 = l1;
      if (manifest.dvrWindowLengthUs != -9223372036854775807L)
      {
        l3 = l1;
        if (manifest.dvrWindowLengthUs > 0L) {
          l3 = Math.max(l1, l2 - manifest.dvrWindowLengthUs);
        }
      }
      l4 = l2 - l3;
      l2 = l4 - IpAddress.msToUs(livePresentationDelayMs);
      l1 = l2;
      if (l2 < 5000000L) {
        l1 = Math.min(5000000L, l4 / 2L);
      }
      localObject1 = new SinglePeriodTimeline(-9223372036854775807L, l4, l3, l1, true, true, localIndex);
    }
    else
    {
      if (manifest.durationUs != -9223372036854775807L) {
        l2 = manifest.durationUs;
      } else {
        l2 -= l1;
      }
      localObject1 = new SinglePeriodTimeline(l1 + l2, l2, l1, 0L, true, false, localIndex);
    }
    refreshSourceInfo((Timeline)localObject1, manifest);
  }
  
  private void scheduleManifestRefresh()
  {
    if (!manifest.isLive) {
      return;
    }
    long l = Math.max(0L, manifestLoadStartTimestamp + 5000L - SystemClock.elapsedRealtime());
    manifestRefreshHandler.postDelayed(new -..Lambda.SsMediaSource.tFjHmMdOxDkhvkY7QhPdfdPmbtI(this), l);
  }
  
  private void startLoadingManifest()
  {
    ParsingLoadable localParsingLoadable = new ParsingLoadable(manifestDataSource, manifestUri, 4, manifestParser);
    long l = manifestLoader.startLoading(localParsingLoadable, this, loadErrorHandlingPolicy.getMinimumLoadableRetryCount(type));
    manifestEventDispatcher.loadStarted(dataSpec, type, l);
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    paramMediaPeriodId = createEventDispatcher(paramMediaPeriodId);
    paramMediaPeriodId = new SsMediaPeriod(manifest, chunkSourceFactory, mediaTransferListener, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, paramMediaPeriodId, manifestLoaderErrorThrower, paramAllocator);
    mediaPeriods.add(paramMediaPeriodId);
    return paramMediaPeriodId;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    manifestLoaderErrorThrower.maybeThrowError();
  }
  
  public void onLoadCanceled(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    manifestEventDispatcher.loadCanceled(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  public void onLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
  {
    manifestEventDispatcher.loadCompleted(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    manifest = ((SsManifest)paramParsingLoadable.getResult());
    manifestLoadStartTimestamp = (paramLong1 - paramLong2);
    processManifest();
    scheduleManifestRefresh();
  }
  
  public Loader.LoadErrorAction onLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
  {
    boolean bool = paramIOException instanceof ParserException;
    manifestEventDispatcher.loadError(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {
      return Loader.DONT_RETRY_FATAL;
    }
    return Loader.RETRY;
  }
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    mediaTransferListener = paramTransferListener;
    if (sideloadedManifest)
    {
      manifestLoaderErrorThrower = new LoaderErrorThrower.Dummy();
      processManifest();
      return;
    }
    manifestDataSource = manifestDataSourceFactory.createDataSource();
    manifestLoader = new Loader("Loader:Manifest");
    manifestLoaderErrorThrower = manifestLoader;
    manifestRefreshHandler = new Handler();
    startLoadingManifest();
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((SsMediaPeriod)paramMediaPeriod).release();
    mediaPeriods.remove(paramMediaPeriod);
  }
  
  public void releaseSourceInternal()
  {
    SsManifest localSsManifest;
    if (sideloadedManifest) {
      localSsManifest = manifest;
    } else {
      localSsManifest = null;
    }
    manifest = localSsManifest;
    manifestDataSource = null;
    manifestLoadStartTimestamp = 0L;
    if (manifestLoader != null)
    {
      manifestLoader.release();
      manifestLoader = null;
    }
    if (manifestRefreshHandler != null)
    {
      manifestRefreshHandler.removeCallbacksAndMessages(null);
      manifestRefreshHandler = null;
    }
  }
  
  public static final class Factory
    implements AdsMediaSource.MediaSourceFactory
  {
    private final SsChunkSource.Factory chunkSourceFactory;
    private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private boolean isCreateCalled;
    private long livePresentationDelayMs;
    private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private Object mTags;
    @Nullable
    private final DataSource.Factory manifestDataSourceFactory;
    @Nullable
    private ParsingLoadable.Parser<? extends SsManifest> manifestParser;
    
    public Factory(SsChunkSource.Factory paramFactory, DataSource.Factory paramFactory1)
    {
      chunkSourceFactory = ((SsChunkSource.Factory)Assertions.checkNotNull(paramFactory));
      manifestDataSourceFactory = paramFactory1;
      loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
      livePresentationDelayMs = 30000L;
      compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
    }
    
    public Factory(DataSource.Factory paramFactory)
    {
      this(new DefaultSsChunkSource.Factory(paramFactory), paramFactory);
    }
    
    public SsMediaSource createMediaSource(Uri paramUri)
    {
      isCreateCalled = true;
      if (manifestParser == null) {
        manifestParser = new SsManifestParser();
      }
      return new SsMediaSource(null, (Uri)Assertions.checkNotNull(paramUri), manifestDataSourceFactory, manifestParser, chunkSourceFactory, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, livePresentationDelayMs, mTags, null);
    }
    
    public SsMediaSource createMediaSource(Uri paramUri, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      paramUri = createMediaSource(paramUri);
      if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
        paramUri.addEventListener(paramHandler, paramMediaSourceEventListener);
      }
      return paramUri;
    }
    
    public SsMediaSource createMediaSource(SsManifest paramSsManifest)
    {
      Assertions.checkArgument(isLive ^ true);
      isCreateCalled = true;
      return new SsMediaSource(paramSsManifest, null, null, null, chunkSourceFactory, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, livePresentationDelayMs, mTags, null);
    }
    
    public SsMediaSource createMediaSource(SsManifest paramSsManifest, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      paramSsManifest = createMediaSource(paramSsManifest);
      if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
        paramSsManifest.addEventListener(paramHandler, paramMediaSourceEventListener);
      }
      return paramSsManifest;
    }
    
    public int[] getSupportedTypes()
    {
      return new int[] { 1 };
    }
    
    public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory)
    {
      Assertions.checkState(isCreateCalled ^ true);
      compositeSequenceableLoaderFactory = ((CompositeSequenceableLoaderFactory)Assertions.checkNotNull(paramCompositeSequenceableLoaderFactory));
      return this;
    }
    
    public Factory setLivePresentationDelayMs(long paramLong)
    {
      Assertions.checkState(isCreateCalled ^ true);
      livePresentationDelayMs = paramLong;
      return this;
    }
    
    public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy)
    {
      Assertions.checkState(isCreateCalled ^ true);
      loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
      return this;
    }
    
    public Factory setManifestParser(ParsingLoadable.Parser paramParser)
    {
      Assertions.checkState(isCreateCalled ^ true);
      manifestParser = ((ParsingLoadable.Parser)Assertions.checkNotNull(paramParser));
      return this;
    }
    
    public Factory setMinLoadableRetryCount(int paramInt)
    {
      return setLoadErrorHandlingPolicy(new DefaultLoadErrorHandlingPolicy(paramInt));
    }
    
    public Factory setTag(Object paramObject)
    {
      Assertions.checkState(isCreateCalled ^ true);
      mTags = paramObject;
      return this;
    }
  }
}
