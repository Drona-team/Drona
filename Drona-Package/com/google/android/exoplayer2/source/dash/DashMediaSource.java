package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.ClickListeners.AdsMediaSource.MediaSourceFactory;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.source.dash.manifest.UtcTimingElement;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
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
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DashMediaSource
  extends BaseMediaSource
{
  @Deprecated
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_FIXED_MS = 30000L;
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000L;
  @Deprecated
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS = -1L;
  private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000L;
  private static final int NOTIFY_MANIFEST_INTERVAL_MS = 5000;
  private static final String PAGE_KEY = "DashMediaSource";
  private final DashChunkSource.Factory chunkSourceFactory;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private DataSource dataSource;
  private long elapsedRealtimeOffsetMs;
  private long expiredManifestPublishTimeUs;
  private int firstPeriodId;
  private Handler handler;
  private Uri initialManifestUri;
  private final long livePresentationDelayMs;
  private final boolean livePresentationDelayOverridesManifest;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  private Loader loader;
  private DashManifest manifest;
  private final ManifestCallback manifestCallback;
  private final DataSource.Factory manifestDataSourceFactory;
  private final MediaSourceEventListener.EventDispatcher manifestEventDispatcher;
  private IOException manifestFatalError;
  private long manifestLoadEndTimestampMs;
  private final LoaderErrorThrower manifestLoadErrorThrower;
  private boolean manifestLoadPending;
  private long manifestLoadStartTimestampMs;
  private final ParsingLoadable.Parser<? extends DashManifest> manifestParser;
  private Uri manifestUri;
  private final Object manifestUriLock;
  @Nullable
  private TransferListener mediaTransferListener;
  private final SparseArray<DashMediaPeriod> periodsById;
  private final PlayerEmsgHandler.PlayerEmsgCallback playerEmsgCallback;
  private final Runnable refreshManifestRunnable;
  private final boolean sideloadedManifest;
  private final Runnable simulateManifestRefreshRunnable;
  private int staleManifestReloadAttempt;
  @Nullable
  private final Object subscriberId;
  
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.dash");
  }
  
  public DashMediaSource(Uri paramUri, DataSource.Factory paramFactory, DashChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, new DashManifestParser(), paramFactory1, paramInt, paramLong, paramHandler, paramMediaSourceEventListener);
  }
  
  public DashMediaSource(Uri paramUri, DataSource.Factory paramFactory, DashChunkSource.Factory paramFactory1, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, paramFactory1, 3, -1L, paramHandler, paramMediaSourceEventListener);
  }
  
  public DashMediaSource(Uri paramUri, DataSource.Factory paramFactory, ParsingLoadable.Parser paramParser, DashChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(null, paramUri, paramFactory, paramParser, paramFactory1, localDefaultCompositeSequenceableLoaderFactory, localDefaultLoadErrorHandlingPolicy, paramLong, bool, null);
    if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
      addEventListener(paramHandler, paramMediaSourceEventListener);
    }
  }
  
  private DashMediaSource(DashManifest paramDashManifest, Uri paramUri, DataSource.Factory paramFactory, ParsingLoadable.Parser paramParser, DashChunkSource.Factory paramFactory1, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, long paramLong, boolean paramBoolean, Object paramObject)
  {
    initialManifestUri = paramUri;
    manifest = paramDashManifest;
    manifestUri = paramUri;
    manifestDataSourceFactory = paramFactory;
    manifestParser = paramParser;
    chunkSourceFactory = paramFactory1;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    livePresentationDelayMs = paramLong;
    livePresentationDelayOverridesManifest = paramBoolean;
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    subscriberId = paramObject;
    if (paramDashManifest != null) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    sideloadedManifest = paramBoolean;
    manifestEventDispatcher = createEventDispatcher(null);
    manifestUriLock = new Object();
    periodsById = new SparseArray();
    playerEmsgCallback = new DefaultPlayerEmsgCallback(null);
    expiredManifestPublishTimeUs = -9223372036854775807L;
    if (sideloadedManifest)
    {
      Assertions.checkState(dynamic ^ true);
      manifestCallback = null;
      refreshManifestRunnable = null;
      simulateManifestRefreshRunnable = null;
      manifestLoadErrorThrower = new LoaderErrorThrower.Dummy();
      return;
    }
    manifestCallback = new ManifestCallback(null);
    manifestLoadErrorThrower = new ManifestLoadErrorThrower();
    refreshManifestRunnable = new -..Lambda.DashMediaSource.QbzYvqCY1TT8f0KClkalovG-Oxc(this);
    simulateManifestRefreshRunnable = new -..Lambda.DashMediaSource.e1nzB-O4m3YSG1BkxQDKPaNvDa8(this);
  }
  
  public DashMediaSource(DashManifest paramDashManifest, DashChunkSource.Factory paramFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramDashManifest, null, null, null, paramFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(paramInt), 30000L, false, null);
    if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
      addEventListener(paramHandler, paramMediaSourceEventListener);
    }
  }
  
  public DashMediaSource(DashManifest paramDashManifest, DashChunkSource.Factory paramFactory, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramDashManifest, paramFactory, 3, paramHandler, paramMediaSourceEventListener);
  }
  
  private long getManifestLoadRetryDelayMillis()
  {
    return Math.min((staleManifestReloadAttempt - 1) * 1000, 5000);
  }
  
  private long getNowUnixTimeUs()
  {
    if (elapsedRealtimeOffsetMs != 0L) {
      return IpAddress.msToUs(SystemClock.elapsedRealtime() + elapsedRealtimeOffsetMs);
    }
    return IpAddress.msToUs(System.currentTimeMillis());
  }
  
  private void onUtcTimestampResolutionError(IOException paramIOException)
  {
    Log.e("DashMediaSource", "Failed to resolve UtcTiming element.", paramIOException);
    processManifest(true);
  }
  
  private void onUtcTimestampResolved(long paramLong)
  {
    elapsedRealtimeOffsetMs = paramLong;
    processManifest(true);
  }
  
  private void processManifest(boolean paramBoolean)
  {
    int i = 0;
    while (i < periodsById.size())
    {
      j = periodsById.keyAt(i);
      if (j >= firstPeriodId) {
        ((DashMediaPeriod)periodsById.valueAt(i)).updateManifest(manifest, j - firstPeriodId);
      }
      i += 1;
    }
    i = manifest.getPeriodCount() - 1;
    Object localObject = PeriodSeekInfo.createPeriodSeekInfo(manifest.getPeriod(0), manifest.getPeriodDurationUs(0));
    PeriodSeekInfo localPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(manifest.getPeriod(i), manifest.getPeriodDurationUs(i));
    long l3 = availableStartTimeUs;
    long l1 = availableEndTimeUs;
    long l2;
    if ((manifest.dynamic) && (!isIndexExplicit))
    {
      l4 = Math.min(getNowUnixTimeUs() - IpAddress.msToUs(manifest.availabilityStartTimeMs) - IpAddress.msToUs(manifest.getPeriod(i).startMs), l1);
      l2 = l4;
      l1 = l3;
      if (manifest.timeShiftBufferDepthMs != -9223372036854775807L)
      {
        for (l1 = l4 - IpAddress.msToUs(manifest.timeShiftBufferDepthMs); (l1 < 0L) && (i > 0); l1 += ((DashManifest)localObject).getPeriodDurationUs(i))
        {
          localObject = manifest;
          i -= 1;
        }
        if (i == 0) {}
        for (l1 = Math.max(l3, l1);; l1 = manifest.getPeriodDurationUs(0)) {
          break;
        }
      }
      i = 1;
      l3 = l2;
      l2 = l1;
    }
    else
    {
      i = 0;
      l2 = l3;
      l3 = l1;
    }
    l3 -= l2;
    int j = 0;
    while (j < manifest.getPeriodCount() - 1)
    {
      l3 += manifest.getPeriodDurationUs(j);
      j += 1;
    }
    if (manifest.dynamic)
    {
      l4 = livePresentationDelayMs;
      l1 = l4;
      if (!livePresentationDelayOverridesManifest)
      {
        l1 = l4;
        if (manifest.suggestedPresentationDelayMs != -9223372036854775807L) {
          l1 = manifest.suggestedPresentationDelayMs;
        }
      }
      l4 = l3 - IpAddress.msToUs(l1);
      l1 = l4;
      if (l4 < 5000000L) {
        l1 = Math.min(5000000L, l3 / 2L);
      }
    }
    else
    {
      l1 = 0L;
    }
    long l4 = manifest.availabilityStartTimeMs;
    long l5 = manifest.getPeriod(0).startMs;
    long l6 = IpAddress.usToMs(l2);
    refreshSourceInfo(new DashTimeline(manifest.availabilityStartTimeMs, l4 + l5 + l6, firstPeriodId, l2, l3, l1, manifest, subscriberId), manifest);
    if (!sideloadedManifest)
    {
      handler.removeCallbacks(simulateManifestRefreshRunnable);
      if (i != 0) {
        handler.postDelayed(simulateManifestRefreshRunnable, 5000L);
      }
      if (manifestLoadPending)
      {
        startLoadingManifest();
        return;
      }
      if ((paramBoolean) && (manifest.dynamic) && (manifest.minUpdatePeriodMs != -9223372036854775807L))
      {
        l2 = manifest.minUpdatePeriodMs;
        l1 = l2;
        if (l2 == 0L) {
          l1 = 5000L;
        }
        scheduleManifestRefresh(Math.max(0L, manifestLoadStartTimestampMs + l1 - SystemClock.elapsedRealtime()));
      }
    }
  }
  
  private void resolveUtcTimingElement(UtcTimingElement paramUtcTimingElement)
  {
    String str = schemeIdUri;
    if ((!Util.areEqual(str, "urn:mpeg:dash:utc:direct:2014")) && (!Util.areEqual(str, "urn:mpeg:dash:utc:direct:2012")))
    {
      if ((!Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2014")) && (!Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2012")))
      {
        if ((!Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2014")) && (!Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2012")))
        {
          onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
          return;
        }
        resolveUtcTimingElementHttp(paramUtcTimingElement, new XsDateTimeParser(null));
        return;
      }
      resolveUtcTimingElementHttp(paramUtcTimingElement, new Iso8601Parser());
      return;
    }
    resolveUtcTimingElementDirect(paramUtcTimingElement);
  }
  
  private void resolveUtcTimingElementDirect(UtcTimingElement paramUtcTimingElement)
  {
    paramUtcTimingElement = value;
    try
    {
      long l1 = Util.parseXsDateTime(paramUtcTimingElement);
      long l2 = manifestLoadEndTimestampMs;
      onUtcTimestampResolved(l1 - l2);
      return;
    }
    catch (ParserException paramUtcTimingElement)
    {
      onUtcTimestampResolutionError(paramUtcTimingElement);
    }
  }
  
  private void resolveUtcTimingElementHttp(UtcTimingElement paramUtcTimingElement, ParsingLoadable.Parser paramParser)
  {
    startLoading(new ParsingLoadable(dataSource, Uri.parse(value), 5, paramParser), new UtcTimestampCallback(null), 1);
  }
  
  private void scheduleManifestRefresh(long paramLong)
  {
    handler.postDelayed(refreshManifestRunnable, paramLong);
  }
  
  private void startLoading(ParsingLoadable paramParsingLoadable, Loader.Callback paramCallback, int paramInt)
  {
    long l = loader.startLoading(paramParsingLoadable, paramCallback, paramInt);
    manifestEventDispatcher.loadStarted(dataSpec, type, l);
  }
  
  private void startLoadingManifest()
  {
    handler.removeCallbacks(refreshManifestRunnable);
    if (loader.isLoading())
    {
      manifestLoadPending = true;
      return;
    }
    Object localObject = manifestUriLock;
    try
    {
      Uri localUri = manifestUri;
      manifestLoadPending = false;
      startLoading(new ParsingLoadable(dataSource, localUri, 4, manifestParser), manifestCallback, loadErrorHandlingPolicy.getMinimumLoadableRetryCount(4));
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    int i = ((Integer)periodUid).intValue() - firstPeriodId;
    paramMediaPeriodId = createEventDispatcher(paramMediaPeriodId, manifest.getPeriod(i).startMs);
    paramMediaPeriodId = new DashMediaPeriod(firstPeriodId + i, manifest, i, chunkSourceFactory, mediaTransferListener, loadErrorHandlingPolicy, paramMediaPeriodId, elapsedRealtimeOffsetMs, manifestLoadErrorThrower, paramAllocator, compositeSequenceableLoaderFactory, playerEmsgCallback);
    periodsById.put(localIndex, paramMediaPeriodId);
    return paramMediaPeriodId;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    manifestLoadErrorThrower.maybeThrowError();
  }
  
  void onDashManifestPublishTimeExpired(long paramLong)
  {
    if ((expiredManifestPublishTimeUs == -9223372036854775807L) || (expiredManifestPublishTimeUs < paramLong)) {
      expiredManifestPublishTimeUs = paramLong;
    }
  }
  
  void onDashManifestRefreshRequested()
  {
    handler.removeCallbacks(simulateManifestRefreshRunnable);
    startLoadingManifest();
  }
  
  void onLoadCanceled(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
  {
    manifestEventDispatcher.loadCanceled(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  void onManifestLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
  {
    manifestEventDispatcher.loadCompleted(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    Object localObject1 = (DashManifest)paramParsingLoadable.getResult();
    Object localObject2 = manifest;
    int m = 0;
    int i;
    if (localObject2 == null) {
      i = 0;
    } else {
      i = manifest.getPeriodCount();
    }
    long l = getPeriod0startMs;
    int j = 0;
    while ((j < i) && (manifest.getPeriod(j).startMs < l)) {
      j += 1;
    }
    int k;
    if (dynamic)
    {
      if (i - j > ((DashManifest)localObject1).getPeriodCount()) {
        Log.w("DashMediaSource", "Loaded out of sync manifest");
      }
      for (;;)
      {
        k = 1;
        break label243;
        if ((expiredManifestPublishTimeUs == -9223372036854775807L) || (publishTimeMs * 1000L > expiredManifestPublishTimeUs)) {
          break;
        }
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Loaded stale dynamic manifest: ");
        ((StringBuilder)localObject2).append(publishTimeMs);
        ((StringBuilder)localObject2).append(", ");
        ((StringBuilder)localObject2).append(expiredManifestPublishTimeUs);
        Log.w("DashMediaSource", ((StringBuilder)localObject2).toString());
      }
      k = 0;
      label243:
      if (k != 0)
      {
        i = staleManifestReloadAttempt;
        staleManifestReloadAttempt = (i + 1);
        if (i < loadErrorHandlingPolicy.getMinimumLoadableRetryCount(type))
        {
          scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
          return;
        }
        manifestFatalError = new DashManifestStaleException();
        return;
      }
      staleManifestReloadAttempt = 0;
    }
    manifest = ((DashManifest)localObject1);
    manifestLoadPending &= manifest.dynamic;
    manifestLoadStartTimestampMs = (paramLong1 - paramLong2);
    manifestLoadEndTimestampMs = paramLong1;
    if (manifest.location != null)
    {
      localObject1 = manifestUriLock;
      k = m;
      try
      {
        if (dataSpec.uri == manifestUri) {
          k = 1;
        }
        if (k != 0) {
          manifestUri = manifest.location;
        }
      }
      catch (Throwable paramParsingLoadable)
      {
        throw paramParsingLoadable;
      }
    }
    if (i == 0)
    {
      if (manifest.utcTiming != null)
      {
        resolveUtcTimingElement(manifest.utcTiming);
        return;
      }
      processManifest(true);
      return;
    }
    firstPeriodId += j;
    processManifest(true);
  }
  
  Loader.LoadErrorAction onManifestLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    manifestEventDispatcher.loadError(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {
      return Loader.DONT_RETRY_FATAL;
    }
    return Loader.RETRY;
  }
  
  void onUtcTimestampLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
  {
    manifestEventDispatcher.loadCompleted(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    onUtcTimestampResolved(((Long)paramParsingLoadable.getResult()).longValue() - paramLong1);
  }
  
  Loader.LoadErrorAction onUtcTimestampLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    manifestEventDispatcher.loadError(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, true);
    onUtcTimestampResolutionError(paramIOException);
    return Loader.DONT_RETRY;
  }
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    mediaTransferListener = paramTransferListener;
    if (sideloadedManifest)
    {
      processManifest(false);
      return;
    }
    dataSource = manifestDataSourceFactory.createDataSource();
    loader = new Loader("Loader:DashMediaSource");
    handler = new Handler();
    startLoadingManifest();
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    paramMediaPeriod = (DashMediaPeriod)paramMediaPeriod;
    paramMediaPeriod.release();
    periodsById.remove(localIndex);
  }
  
  public void releaseSourceInternal()
  {
    manifestLoadPending = false;
    dataSource = null;
    if (loader != null)
    {
      loader.release();
      loader = null;
    }
    manifestLoadStartTimestampMs = 0L;
    manifestLoadEndTimestampMs = 0L;
    DashManifest localDashManifest;
    if (sideloadedManifest) {
      localDashManifest = manifest;
    } else {
      localDashManifest = null;
    }
    manifest = localDashManifest;
    manifestUri = initialManifestUri;
    manifestFatalError = null;
    if (handler != null)
    {
      handler.removeCallbacksAndMessages(null);
      handler = null;
    }
    elapsedRealtimeOffsetMs = 0L;
    staleManifestReloadAttempt = 0;
    expiredManifestPublishTimeUs = -9223372036854775807L;
    firstPeriodId = 0;
    periodsById.clear();
  }
  
  public void replaceManifestUri(Uri paramUri)
  {
    Object localObject = manifestUriLock;
    try
    {
      manifestUri = paramUri;
      initialManifestUri = paramUri;
      return;
    }
    catch (Throwable paramUri)
    {
      throw paramUri;
    }
  }
  
  private static final class DashTimeline
    extends Timeline
  {
    private final int firstPeriodId;
    private final DashManifest manifest;
    private final long offsetInFirstPeriodUs;
    private final long presentationStartTimeMs;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowStartTimeMs;
    @Nullable
    private final Object windowTag;
    
    public DashTimeline(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4, long paramLong5, DashManifest paramDashManifest, Object paramObject)
    {
      presentationStartTimeMs = paramLong1;
      windowStartTimeMs = paramLong2;
      firstPeriodId = paramInt;
      offsetInFirstPeriodUs = paramLong3;
      windowDurationUs = paramLong4;
      windowDefaultStartPositionUs = paramLong5;
      manifest = paramDashManifest;
      windowTag = paramObject;
    }
    
    private long getAdjustedWindowDefaultStartPositionUs(long paramLong)
    {
      long l2 = windowDefaultStartPositionUs;
      if (!manifest.dynamic) {
        return l2;
      }
      long l1 = l2;
      if (paramLong > 0L)
      {
        paramLong = l2 + paramLong;
        l1 = paramLong;
        if (paramLong > windowDurationUs) {
          return -9223372036854775807L;
        }
      }
      l2 = offsetInFirstPeriodUs;
      paramLong = manifest.getPeriodDurationUs(0);
      l2 += l1;
      int i = 0;
      while ((i < manifest.getPeriodCount() - 1) && (l2 >= paramLong))
      {
        l2 -= paramLong;
        i += 1;
        paramLong = manifest.getPeriodDurationUs(i);
      }
      Object localObject = manifest.getPeriod(i);
      i = ((Period)localObject).getAdaptationSetIndex(2);
      if (i == -1) {
        return l1;
      }
      localObject = ((Representation)adaptationSets.get(i)).representations.get(0)).getIndex();
      if (localObject != null)
      {
        if (((DashSegmentIndex)localObject).getSegmentCount(paramLong) == 0) {
          return l1;
        }
        return l1 + ((DashSegmentIndex)localObject).getTimeUs(((DashSegmentIndex)localObject).getSegmentNum(l2, paramLong)) - l2;
      }
      return l1;
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      if (!(paramObject instanceof Integer)) {
        return -1;
      }
      int i = ((Integer)paramObject).intValue() - firstPeriodId;
      if ((i < 0) || (i >= getPeriodCount())) {
        return -1;
      }
      return i;
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      Assertions.checkIndex(paramInt, 0, getPeriodCount());
      Integer localInteger = null;
      String str;
      if (paramBoolean) {
        str = manifest.getPeriod(paramInt).size;
      } else {
        str = null;
      }
      if (paramBoolean) {
        localInteger = Integer.valueOf(firstPeriodId + paramInt);
      }
      return paramPeriod.get(str, localInteger, 0, manifest.getPeriodDurationUs(paramInt), IpAddress.msToUs(manifest.getPeriod(paramInt).startMs - manifest.getPeriod(0).startMs) - offsetInFirstPeriodUs);
    }
    
    public int getPeriodCount()
    {
      return manifest.getPeriodCount();
    }
    
    public Object getUidOfPeriod(int paramInt)
    {
      Assertions.checkIndex(paramInt, 0, getPeriodCount());
      return Integer.valueOf(firstPeriodId + paramInt);
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      Assertions.checkIndex(paramInt, 0, 1);
      paramLong = getAdjustedWindowDefaultStartPositionUs(paramLong);
      if (paramBoolean) {}
      for (Object localObject = windowTag;; localObject = null) {
        break;
      }
      if ((manifest.dynamic) && (manifest.minUpdatePeriodMs != -9223372036854775807L) && (manifest.durationMs == -9223372036854775807L)) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
      return paramWindow.readInt(localObject, presentationStartTimeMs, windowStartTimeMs, true, paramBoolean, paramLong, windowDurationUs, 0, getPeriodCount() - 1, offsetInFirstPeriodUs);
    }
    
    public int getWindowCount()
    {
      return 1;
    }
  }
  
  private final class DefaultPlayerEmsgCallback
    implements PlayerEmsgHandler.PlayerEmsgCallback
  {
    private DefaultPlayerEmsgCallback() {}
    
    public void onDashManifestPublishTimeExpired(long paramLong)
    {
      DashMediaSource.this.onDashManifestPublishTimeExpired(paramLong);
    }
    
    public void onDashManifestRefreshRequested()
    {
      DashMediaSource.this.onDashManifestRefreshRequested();
    }
  }
  
  public static final class Factory
    implements AdsMediaSource.MediaSourceFactory
  {
    private final DashChunkSource.Factory chunkSourceFactory;
    private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private boolean isCreateCalled;
    private long livePresentationDelayMs;
    private boolean livePresentationDelayOverridesManifest;
    private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private Object mTags;
    @Nullable
    private final DataSource.Factory manifestDataSourceFactory;
    @Nullable
    private ParsingLoadable.Parser<? extends DashManifest> manifestParser;
    
    public Factory(DashChunkSource.Factory paramFactory, DataSource.Factory paramFactory1)
    {
      chunkSourceFactory = ((DashChunkSource.Factory)Assertions.checkNotNull(paramFactory));
      manifestDataSourceFactory = paramFactory1;
      loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
      livePresentationDelayMs = 30000L;
      compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
    }
    
    public Factory(DataSource.Factory paramFactory)
    {
      this(new DefaultDashChunkSource.Factory(paramFactory), paramFactory);
    }
    
    public DashMediaSource createMediaSource(Uri paramUri)
    {
      isCreateCalled = true;
      if (manifestParser == null) {
        manifestParser = new DashManifestParser();
      }
      return new DashMediaSource(null, (Uri)Assertions.checkNotNull(paramUri), manifestDataSourceFactory, manifestParser, chunkSourceFactory, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, livePresentationDelayMs, livePresentationDelayOverridesManifest, mTags, null);
    }
    
    public DashMediaSource createMediaSource(Uri paramUri, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      paramUri = createMediaSource(paramUri);
      if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
        paramUri.addEventListener(paramHandler, paramMediaSourceEventListener);
      }
      return paramUri;
    }
    
    public DashMediaSource createMediaSource(DashManifest paramDashManifest)
    {
      Assertions.checkArgument(dynamic ^ true);
      isCreateCalled = true;
      return new DashMediaSource(paramDashManifest, null, null, null, chunkSourceFactory, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, livePresentationDelayMs, livePresentationDelayOverridesManifest, mTags, null);
    }
    
    public DashMediaSource createMediaSource(DashManifest paramDashManifest, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      paramDashManifest = createMediaSource(paramDashManifest);
      if ((paramHandler != null) && (paramMediaSourceEventListener != null)) {
        paramDashManifest.addEventListener(paramHandler, paramMediaSourceEventListener);
      }
      return paramDashManifest;
    }
    
    public int[] getSupportedTypes()
    {
      return new int[] { 0 };
    }
    
    public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory)
    {
      Assertions.checkState(isCreateCalled ^ true);
      compositeSequenceableLoaderFactory = ((CompositeSequenceableLoaderFactory)Assertions.checkNotNull(paramCompositeSequenceableLoaderFactory));
      return this;
    }
    
    public Factory setLivePresentationDelayMs(long paramLong)
    {
      if (paramLong == -1L) {
        return setLivePresentationDelayMs(30000L, false);
      }
      return setLivePresentationDelayMs(paramLong, true);
    }
    
    public Factory setLivePresentationDelayMs(long paramLong, boolean paramBoolean)
    {
      Assertions.checkState(isCreateCalled ^ true);
      livePresentationDelayMs = paramLong;
      livePresentationDelayOverridesManifest = paramBoolean;
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
  
  static final class Iso8601Parser
    implements ParsingLoadable.Parser<Long>
  {
    private static final Pattern TIMESTAMP_WITH_TIMEZONE_PATTERN = Pattern.compile("(.+?)(Z|((\\+|-|?)(\\d\\d)(:?(\\d\\d))?))");
    
    Iso8601Parser() {}
    
    public Long parse(Uri paramUri, InputStream paramInputStream)
      throws IOException
    {
      paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream, Charset.forName("UTF-8"))).readLine();
      paramUri = TIMESTAMP_WITH_TIMEZONE_PATTERN;
      try
      {
        paramUri = paramUri.matcher(paramInputStream);
        boolean bool = paramUri.matches();
        if (bool)
        {
          paramInputStream = paramUri.group(1);
          Object localObject = Locale.US;
          localObject = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", (Locale)localObject);
          ((SimpleDateFormat)localObject).setTimeZone(TimeZone.getTimeZone("UTC"));
          long l3 = ((SimpleDateFormat)localObject).parse(paramInputStream).getTime();
          long l1 = l3;
          paramInputStream = paramUri.group(2);
          bool = "Z".equals(paramInputStream);
          if (!bool)
          {
            bool = "+".equals(paramUri.group(4));
            if (bool) {
              l1 = 1L;
            } else {
              l1 = -1L;
            }
            long l4 = Long.parseLong(paramUri.group(5));
            paramUri = paramUri.group(7);
            bool = TextUtils.isEmpty(paramUri);
            long l2;
            if (bool) {
              l2 = 0L;
            } else {
              l2 = Long.parseLong(paramUri);
            }
            l1 = l3 - l1 * ((l4 * 60L + l2) * 60L * 1000L);
          }
          return Long.valueOf(l1);
        }
        paramUri = new StringBuilder();
        paramUri.append("Couldn't parse timestamp: ");
        paramUri.append(paramInputStream);
        paramUri = new ParserException(paramUri.toString());
        throw paramUri;
      }
      catch (ParseException paramUri)
      {
        throw new ParserException(paramUri);
      }
    }
  }
  
  private final class ManifestCallback
    implements Loader.Callback<ParsingLoadable<DashManifest>>
  {
    private ManifestCallback() {}
    
    public void onLoadCanceled(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      onLoadCanceled(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public void onLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
    {
      onManifestLoadCompleted(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public Loader.LoadErrorAction onLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
    {
      return onManifestLoadError(paramParsingLoadable, paramLong1, paramLong2, paramIOException);
    }
  }
  
  final class ManifestLoadErrorThrower
    implements LoaderErrorThrower
  {
    ManifestLoadErrorThrower() {}
    
    private void maybeThrowManifestError()
      throws IOException
    {
      if (manifestFatalError == null) {
        return;
      }
      throw manifestFatalError;
    }
    
    public void maybeThrowError()
      throws IOException
    {
      loader.maybeThrowError();
      maybeThrowManifestError();
    }
    
    public void maybeThrowError(int paramInt)
      throws IOException
    {
      loader.maybeThrowError(paramInt);
      maybeThrowManifestError();
    }
  }
  
  private static final class PeriodSeekInfo
  {
    public final long availableEndTimeUs;
    public final long availableStartTimeUs;
    public final boolean isIndexExplicit;
    
    private PeriodSeekInfo(boolean paramBoolean, long paramLong1, long paramLong2)
    {
      isIndexExplicit = paramBoolean;
      availableStartTimeUs = paramLong1;
      availableEndTimeUs = paramLong2;
    }
    
    public static PeriodSeekInfo createPeriodSeekInfo(Period paramPeriod, long paramLong)
    {
      int n = adaptationSets.size();
      int i = 0;
      while (i < n)
      {
        j = adaptationSets.get(i)).type;
        if ((j != 1) && (j != 2))
        {
          i += 1;
        }
        else
        {
          i = 1;
          break label66;
        }
      }
      i = 0;
      label66:
      long l3 = Long.MAX_VALUE;
      int j = 0;
      int k = 0;
      boolean bool1 = false;
      long l2;
      for (long l4 = 0L; j < n; l4 = l2)
      {
        Object localObject = (AdaptationSet)adaptationSets.get(j);
        long l1;
        int m;
        if ((i != 0) && (type == 3))
        {
          l1 = l3;
          m = k;
          l2 = l4;
        }
        else
        {
          localObject = ((Representation)representations.get(0)).getIndex();
          if (localObject == null) {
            return new PeriodSeekInfo(true, 0L, paramLong);
          }
          boolean bool2 = ((DashSegmentIndex)localObject).isExplicit() | bool1;
          int i1 = ((DashSegmentIndex)localObject).getSegmentCount(paramLong);
          if (i1 == 0)
          {
            m = 1;
            l2 = 0L;
            l1 = 0L;
            bool1 = bool2;
          }
          else
          {
            l1 = l3;
            m = k;
            bool1 = bool2;
            l2 = l4;
            if (k == 0)
            {
              l1 = ((DashSegmentIndex)localObject).getFirstSegmentNum();
              l2 = Math.max(l4, ((DashSegmentIndex)localObject).getTimeUs(l1));
              if (i1 != -1)
              {
                l1 = l1 + i1 - 1L;
                l1 = Math.min(l3, ((DashSegmentIndex)localObject).getTimeUs(l1) + ((DashSegmentIndex)localObject).getDurationUs(l1, paramLong));
                m = k;
                bool1 = bool2;
              }
              else
              {
                l1 = l3;
                m = k;
                bool1 = bool2;
              }
            }
          }
        }
        j += 1;
        l3 = l1;
        k = m;
      }
      return new PeriodSeekInfo(bool1, l4, l3);
    }
  }
  
  private final class UtcTimestampCallback
    implements Loader.Callback<ParsingLoadable<Long>>
  {
    private UtcTimestampCallback() {}
    
    public void onLoadCanceled(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      onLoadCanceled(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public void onLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
    {
      onUtcTimestampLoadCompleted(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public Loader.LoadErrorAction onLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
    {
      return onUtcTimestampLoadError(paramParsingLoadable, paramLong1, paramLong2, paramIOException);
    }
  }
  
  private static final class XsDateTimeParser
    implements ParsingLoadable.Parser<Long>
  {
    private XsDateTimeParser() {}
    
    public Long parse(Uri paramUri, InputStream paramInputStream)
      throws IOException
    {
      return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(paramInputStream)).readLine()));
    }
  }
}
