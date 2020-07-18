package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.upstream.Loader.ReleaseCallback;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ConditionVariable;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

final class ExtractorMediaPeriod
  implements MediaPeriod, ExtractorOutput, Loader.Callback<ExtractingLoadable>, Loader.ReleaseCallback, SampleQueue.UpstreamFormatChangedListener
{
  private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000L;
  private final Allocator allocator;
  @Nullable
  private MediaPeriod.Callback callback;
  private final long continueLoadingCheckIntervalBytes;
  @Nullable
  private final String customCacheKey;
  private final DataSource dataSource;
  private int dataType;
  private long durationUs;
  private int enabledTrackCount;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private int extractedSamplesCountAtStartOfLoad;
  private final ExtractorHolder extractorHolder;
  private final Handler handler;
  private boolean haveAudioVideoTracks;
  private long lastSeekPositionUs;
  private long length;
  private final Listener listener;
  private final ConditionVariable loadCondition;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private boolean notifiedReadingStarted;
  private boolean notifyDiscontinuity;
  private final Runnable onContinueLoadingRequestedRunnable;
  private boolean pendingDeferredRetry;
  private long pendingResetPositionUs;
  private boolean prepared;
  @Nullable
  private PreparedState preparedState;
  private boolean released;
  private int[] sampleQueueTrackIds;
  private SampleQueue[] sampleQueues;
  private boolean sampleQueuesBuilt;
  @Nullable
  private SeekMap seekMap;
  private boolean seenFirstTrackSelection;
  private final Uri uri;
  
  public ExtractorMediaPeriod(Uri paramUri, DataSource paramDataSource, Extractor[] paramArrayOfExtractor, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher paramEventDispatcher, Listener paramListener, Allocator paramAllocator, String paramString, int paramInt)
  {
    uri = paramUri;
    dataSource = paramDataSource;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    eventDispatcher = paramEventDispatcher;
    listener = paramListener;
    allocator = paramAllocator;
    customCacheKey = paramString;
    continueLoadingCheckIntervalBytes = paramInt;
    loader = new Loader("Loader:ExtractorMediaPeriod");
    extractorHolder = new ExtractorHolder(paramArrayOfExtractor);
    loadCondition = new ConditionVariable();
    maybeFinishPrepareRunnable = new -..Lambda.ExtractorMediaPeriod.Ll7lI30pD07GZk92Lo8XgkQMAAY(this);
    onContinueLoadingRequestedRunnable = new -..Lambda.ExtractorMediaPeriod.Hd-sBytb6cpkhM49l8dYCND3wmk(this);
    handler = new Handler();
    sampleQueueTrackIds = new int[0];
    sampleQueues = new SampleQueue[0];
    pendingResetPositionUs = -9223372036854775807L;
    length = -1L;
    durationUs = -9223372036854775807L;
    dataType = 1;
    paramEventDispatcher.mediaPeriodCreated();
  }
  
  private boolean configureRetry(ExtractingLoadable paramExtractingLoadable, int paramInt)
  {
    if ((length == -1L) && ((seekMap == null) || (seekMap.getDurationUs() == -9223372036854775807L)))
    {
      boolean bool = prepared;
      paramInt = 0;
      if ((bool) && (!suppressRead()))
      {
        pendingDeferredRetry = true;
        return false;
      }
      notifyDiscontinuity = prepared;
      lastSeekPositionUs = 0L;
      extractedSamplesCountAtStartOfLoad = 0;
      SampleQueue[] arrayOfSampleQueue = sampleQueues;
      int i = arrayOfSampleQueue.length;
      while (paramInt < i)
      {
        arrayOfSampleQueue[paramInt].reset();
        paramInt += 1;
      }
      paramExtractingLoadable.setLoadPosition(0L, 0L);
      return true;
    }
    extractedSamplesCountAtStartOfLoad = paramInt;
    return true;
  }
  
  private void copyLengthFromLoader(ExtractingLoadable paramExtractingLoadable)
  {
    if (length == -1L) {
      length = length;
    }
  }
  
  private int getExtractedSamplesCount()
  {
    SampleQueue[] arrayOfSampleQueue = sampleQueues;
    int k = arrayOfSampleQueue.length;
    int i = 0;
    int j = 0;
    while (i < k)
    {
      j += arrayOfSampleQueue[i].getWriteIndex();
      i += 1;
    }
    return j;
  }
  
  private long getLargestQueuedTimestampUs()
  {
    SampleQueue[] arrayOfSampleQueue = sampleQueues;
    int j = arrayOfSampleQueue.length;
    long l = Long.MIN_VALUE;
    int i = 0;
    while (i < j)
    {
      l = Math.max(l, arrayOfSampleQueue[i].getLargestQueuedTimestampUs());
      i += 1;
    }
    return l;
  }
  
  private PreparedState getPreparedState()
  {
    return (PreparedState)Assertions.checkNotNull(preparedState);
  }
  
  private boolean isPendingReset()
  {
    return pendingResetPositionUs != -9223372036854775807L;
  }
  
  private void maybeFinishPrepare()
  {
    SeekMap localSeekMap = seekMap;
    if ((!released) && (!prepared) && (sampleQueuesBuilt))
    {
      if (localSeekMap == null) {
        return;
      }
      Object localObject1 = sampleQueues;
      int j = localObject1.length;
      int i = 0;
      while (i < j)
      {
        if (localObject1[i].getUpstreamFormat() == null) {
          return;
        }
        i += 1;
      }
      loadCondition.close();
      j = sampleQueues.length;
      localObject1 = new TrackGroup[j];
      boolean[] arrayOfBoolean = new boolean[j];
      durationUs = localSeekMap.getDurationUs();
      i = 0;
      for (;;)
      {
        int m = 1;
        if (i >= j) {
          break;
        }
        Object localObject2 = sampleQueues[i].getUpstreamFormat();
        localObject1[i] = new TrackGroup(new Format[] { localObject2 });
        localObject2 = sampleMimeType;
        int k = m;
        if (!MimeTypes.isVideo((String)localObject2)) {
          if (MimeTypes.isAudio((String)localObject2)) {
            k = m;
          } else {
            k = 0;
          }
        }
        arrayOfBoolean[i] = k;
        haveAudioVideoTracks = (k | haveAudioVideoTracks);
        i += 1;
      }
      if ((length == -1L) && (localSeekMap.getDurationUs() == -9223372036854775807L)) {
        i = 7;
      } else {
        i = 1;
      }
      dataType = i;
      preparedState = new PreparedState(localSeekMap, new TrackGroupArray((TrackGroup[])localObject1), arrayOfBoolean);
      prepared = true;
      listener.onSourceInfoRefreshed(durationUs, localSeekMap.isSeekable());
      ((MediaPeriod.Callback)Assertions.checkNotNull(callback)).onPrepared(this);
    }
  }
  
  private void maybeNotifyDownstreamFormat(int paramInt)
  {
    Object localObject = getPreparedState();
    boolean[] arrayOfBoolean = trackNotifiedDownstreamFormats;
    if (arrayOfBoolean[paramInt] == 0)
    {
      localObject = tracks.context(paramInt).getFormat(0);
      eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(sampleMimeType), (Format)localObject, 0, null, lastSeekPositionUs);
      arrayOfBoolean[paramInt] = true;
    }
  }
  
  private void maybeStartDeferredRetry(int paramInt)
  {
    Object localObject = getPreparedStatetrackIsAudioVideoFlags;
    if ((pendingDeferredRetry) && (localObject[paramInt] != 0))
    {
      if (sampleQueues[paramInt].hasNextSample()) {
        return;
      }
      pendingResetPositionUs = 0L;
      paramInt = 0;
      pendingDeferredRetry = false;
      notifyDiscontinuity = true;
      lastSeekPositionUs = 0L;
      extractedSamplesCountAtStartOfLoad = 0;
      localObject = sampleQueues;
      int i = localObject.length;
      while (paramInt < i)
      {
        localObject[paramInt].reset();
        paramInt += 1;
      }
      ((MediaPeriod.Callback)Assertions.checkNotNull(callback)).onContinueLoadingRequested(this);
    }
  }
  
  private boolean seekInsideBufferUs(boolean[] paramArrayOfBoolean, long paramLong)
  {
    int k = sampleQueues.length;
    int i = 0;
    for (;;)
    {
      int j = 1;
      if (i >= k) {
        break;
      }
      SampleQueue localSampleQueue = sampleQueues[i];
      localSampleQueue.rewind();
      if (localSampleQueue.advanceTo(paramLong, true, false) == -1) {
        j = 0;
      }
      if (j == 0)
      {
        if (paramArrayOfBoolean[i] != 0) {
          break label84;
        }
        if (!haveAudioVideoTracks) {
          return false;
        }
      }
      i += 1;
    }
    return true;
    label84:
    return false;
  }
  
  private void startLoading()
  {
    ExtractingLoadable localExtractingLoadable = new ExtractingLoadable(uri, dataSource, extractorHolder, this, loadCondition);
    if (prepared)
    {
      SeekMap localSeekMap = getPreparedStateseekMap;
      Assertions.checkState(isPendingReset());
      if ((durationUs != -9223372036854775807L) && (pendingResetPositionUs >= durationUs))
      {
        loadingFinished = true;
        pendingResetPositionUs = -9223372036854775807L;
        return;
      }
      localExtractingLoadable.setLoadPosition(getSeekPointspendingResetPositionUs).first.position, pendingResetPositionUs);
      pendingResetPositionUs = -9223372036854775807L;
    }
    extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
    long l = loader.startLoading(localExtractingLoadable, this, loadErrorHandlingPolicy.getMinimumLoadableRetryCount(dataType));
    eventDispatcher.loadStarted(dataSpec, 1, -1, null, 0, null, seekTimeUs, durationUs, l);
  }
  
  private boolean suppressRead()
  {
    return (notifyDiscontinuity) || (isPendingReset());
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool;
    if ((!loadingFinished) && (!pendingDeferredRetry) && ((!prepared) || (enabledTrackCount != 0)))
    {
      bool = loadCondition.open();
      if (!loader.isLoading())
      {
        startLoading();
        return true;
      }
    }
    else
    {
      return false;
    }
    return bool;
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    if (isPendingReset()) {
      return;
    }
    boolean[] arrayOfBoolean = getPreparedStatetrackEnabledStates;
    int j = sampleQueues.length;
    int i = 0;
    while (i < j)
    {
      sampleQueues[i].discardTo(paramLong, paramBoolean, arrayOfBoolean[i]);
      i += 1;
    }
  }
  
  public void endTracks()
  {
    sampleQueuesBuilt = true;
    handler.post(maybeFinishPrepareRunnable);
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    Object localObject = getPreparedStateseekMap;
    if (!((SeekMap)localObject).isSeekable()) {
      return 0L;
    }
    localObject = ((SeekMap)localObject).getSeekPoints(paramLong);
    return Util.resolveSeekPositionUs(paramLong, paramSeekParameters, first.timeUs, second.timeUs);
  }
  
  public long getBufferedPositionUs()
  {
    boolean[] arrayOfBoolean = getPreparedStatetrackIsAudioVideoFlags;
    if (loadingFinished) {
      return Long.MIN_VALUE;
    }
    if (isPendingReset()) {
      return pendingResetPositionUs;
    }
    if (haveAudioVideoTracks)
    {
      l1 = Long.MAX_VALUE;
      int j = sampleQueues.length;
      int i = 0;
      for (;;)
      {
        l2 = l1;
        if (i >= j) {
          break;
        }
        l2 = l1;
        if (arrayOfBoolean[i] != 0) {
          l2 = Math.min(l1, sampleQueues[i].getLargestQueuedTimestampUs());
        }
        i += 1;
        l1 = l2;
      }
    }
    long l2 = getLargestQueuedTimestampUs();
    long l1 = l2;
    if (l2 == Long.MIN_VALUE) {
      l1 = lastSeekPositionUs;
    }
    return l1;
  }
  
  public long getNextLoadPositionUs()
  {
    if (enabledTrackCount == 0) {
      return Long.MIN_VALUE;
    }
    return getBufferedPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return getPreparedStatetracks;
  }
  
  boolean isReady(int paramInt)
  {
    return (!suppressRead()) && ((loadingFinished) || (sampleQueues[paramInt].hasNextSample()));
  }
  
  void maybeThrowError()
    throws IOException
  {
    loader.maybeThrowError(loadErrorHandlingPolicy.getMinimumLoadableRetryCount(dataType));
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    maybeThrowError();
  }
  
  public void onLoadCanceled(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    eventDispatcher.loadCanceled(dataSpec, dataSource.getLastOpenedUri(), dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, seekTimeUs, durationUs, paramLong1, paramLong2, dataSource.getBytesRead());
    if (!paramBoolean)
    {
      copyLengthFromLoader(paramExtractingLoadable);
      paramExtractingLoadable = sampleQueues;
      int j = paramExtractingLoadable.length;
      int i = 0;
      while (i < j)
      {
        paramExtractingLoadable[i].reset();
        i += 1;
      }
      if (enabledTrackCount > 0) {
        ((MediaPeriod.Callback)Assertions.checkNotNull(callback)).onContinueLoadingRequested(this);
      }
    }
  }
  
  public void onLoadCompleted(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2)
  {
    if (durationUs == -9223372036854775807L)
    {
      SeekMap localSeekMap = (SeekMap)Assertions.checkNotNull(seekMap);
      long l = getLargestQueuedTimestampUs();
      if (l == Long.MIN_VALUE) {
        l = 0L;
      } else {
        l += 10000L;
      }
      durationUs = l;
      listener.onSourceInfoRefreshed(durationUs, localSeekMap.isSeekable());
    }
    eventDispatcher.loadCompleted(dataSpec, dataSource.getLastOpenedUri(), dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, seekTimeUs, durationUs, paramLong1, paramLong2, dataSource.getBytesRead());
    copyLengthFromLoader(paramExtractingLoadable);
    loadingFinished = true;
    ((MediaPeriod.Callback)Assertions.checkNotNull(callback)).onContinueLoadingRequested(this);
  }
  
  public Loader.LoadErrorAction onLoadError(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
  {
    copyLengthFromLoader(paramExtractingLoadable);
    long l = loadErrorHandlingPolicy.getRetryDelayMsFor(dataType, durationUs, paramIOException, paramInt);
    Loader.LoadErrorAction localLoadErrorAction;
    if (l == -9223372036854775807L)
    {
      localLoadErrorAction = Loader.DONT_RETRY_FATAL;
    }
    else
    {
      paramInt = getExtractedSamplesCount();
      boolean bool;
      if (paramInt > extractedSamplesCountAtStartOfLoad) {
        bool = true;
      } else {
        bool = false;
      }
      if (configureRetry(paramExtractingLoadable, paramInt)) {
        localLoadErrorAction = Loader.createRetryAction(bool, l);
      } else {
        localLoadErrorAction = Loader.DONT_RETRY;
      }
    }
    eventDispatcher.loadError(dataSpec, dataSource.getLastOpenedUri(), dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, seekTimeUs, durationUs, paramLong1, paramLong2, dataSource.getBytesRead(), paramIOException, localLoadErrorAction.isRetry() ^ true);
    return localLoadErrorAction;
  }
  
  public void onLoaderReleased()
  {
    SampleQueue[] arrayOfSampleQueue = sampleQueues;
    int j = arrayOfSampleQueue.length;
    int i = 0;
    while (i < j)
    {
      arrayOfSampleQueue[i].reset();
      i += 1;
    }
    extractorHolder.release();
  }
  
  public void onUpstreamFormatChanged(Format paramFormat)
  {
    handler.post(maybeFinishPrepareRunnable);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    callback = paramCallback;
    loadCondition.open();
    startLoading();
  }
  
  int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    if (suppressRead()) {
      return -3;
    }
    maybeNotifyDownstreamFormat(paramInt);
    int i = sampleQueues[paramInt].read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean, loadingFinished, lastSeekPositionUs);
    if (i == -3) {
      maybeStartDeferredRetry(paramInt);
    }
    return i;
  }
  
  public long readDiscontinuity()
  {
    if (!notifiedReadingStarted)
    {
      eventDispatcher.readingStarted();
      notifiedReadingStarted = true;
    }
    if ((notifyDiscontinuity) && ((loadingFinished) || (getExtractedSamplesCount() > extractedSamplesCountAtStartOfLoad)))
    {
      notifyDiscontinuity = false;
      return lastSeekPositionUs;
    }
    return -9223372036854775807L;
  }
  
  public void reevaluateBuffer(long paramLong) {}
  
  public void release()
  {
    if (prepared)
    {
      SampleQueue[] arrayOfSampleQueue = sampleQueues;
      int j = arrayOfSampleQueue.length;
      int i = 0;
      while (i < j)
      {
        arrayOfSampleQueue[i].discardToEnd();
        i += 1;
      }
    }
    loader.release(this);
    handler.removeCallbacksAndMessages(null);
    callback = null;
    released = true;
    eventDispatcher.mediaPeriodReleased();
  }
  
  public void seekMap(SeekMap paramSeekMap)
  {
    seekMap = paramSeekMap;
    handler.post(maybeFinishPrepareRunnable);
  }
  
  public long seekToUs(long paramLong)
  {
    Object localObject2 = getPreparedState();
    Object localObject1 = seekMap;
    localObject2 = trackIsAudioVideoFlags;
    if (!((SeekMap)localObject1).isSeekable()) {
      paramLong = 0L;
    }
    int i = 0;
    notifyDiscontinuity = false;
    lastSeekPositionUs = paramLong;
    if (isPendingReset())
    {
      pendingResetPositionUs = paramLong;
      return paramLong;
    }
    if ((dataType != 7) && (seekInsideBufferUs((boolean[])localObject2, paramLong))) {
      return paramLong;
    }
    pendingDeferredRetry = false;
    pendingResetPositionUs = paramLong;
    loadingFinished = false;
    if (loader.isLoading())
    {
      loader.cancelLoading();
      return paramLong;
    }
    localObject1 = sampleQueues;
    int j = localObject1.length;
    while (i < j)
    {
      localObject1[i].reset();
      i += 1;
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    Object localObject = getPreparedState();
    TrackGroupArray localTrackGroupArray = tracks;
    localObject = trackEnabledStates;
    int j = enabledTrackCount;
    int n = 0;
    int i1 = 0;
    int m = 0;
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] != null) && ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean1[i] == 0)))
      {
        k = track;
        Assertions.checkState(localObject[k]);
        enabledTrackCount -= 1;
        localObject[k] = 0;
        paramArrayOfSampleStream[i] = null;
      }
      i += 1;
    }
    if (seenFirstTrackSelection) {
      if (j != 0) {}
    }
    for (;;)
    {
      i = 1;
      break;
      do
      {
        i = 0;
        break;
      } while (paramLong == 0L);
    }
    j = 0;
    for (int k = i; j < paramArrayOfTrackSelection.length; k = i)
    {
      i = k;
      if (paramArrayOfSampleStream[j] == null)
      {
        i = k;
        if (paramArrayOfTrackSelection[j] != null)
        {
          paramArrayOfBoolean1 = paramArrayOfTrackSelection[j];
          boolean bool;
          if (paramArrayOfBoolean1.length() == 1) {
            bool = true;
          } else {
            bool = false;
          }
          Assertions.checkState(bool);
          if (paramArrayOfBoolean1.getIndexInTrackGroup(0) == 0) {
            bool = true;
          } else {
            bool = false;
          }
          Assertions.checkState(bool);
          int i2 = localTrackGroupArray.indexOf(paramArrayOfBoolean1.getTrackGroup());
          Assertions.checkState(localObject[i2] ^ 0x1);
          enabledTrackCount += 1;
          localObject[i2] = 1;
          paramArrayOfSampleStream[j] = new SampleStreamImpl(i2);
          paramArrayOfBoolean2[j] = true;
          i = k;
          if (k == 0)
          {
            paramArrayOfBoolean1 = sampleQueues[i2];
            paramArrayOfBoolean1.rewind();
            if ((paramArrayOfBoolean1.advanceTo(paramLong, true, true) == -1) && (paramArrayOfBoolean1.getReadIndex() != 0)) {
              i = 1;
            } else {
              i = 0;
            }
          }
        }
      }
      j += 1;
    }
    long l;
    if (enabledTrackCount == 0)
    {
      pendingDeferredRetry = false;
      notifyDiscontinuity = false;
      if (loader.isLoading())
      {
        paramArrayOfTrackSelection = sampleQueues;
        j = paramArrayOfTrackSelection.length;
        i = m;
        while (i < j)
        {
          paramArrayOfTrackSelection[i].discardToEnd();
          i += 1;
        }
        loader.cancelLoading();
        l = paramLong;
      }
      else
      {
        paramArrayOfTrackSelection = sampleQueues;
        j = paramArrayOfTrackSelection.length;
        i = n;
        for (;;)
        {
          l = paramLong;
          if (i >= j) {
            break;
          }
          paramArrayOfTrackSelection[i].reset();
          i += 1;
        }
      }
    }
    else
    {
      l = paramLong;
      if (k != 0)
      {
        paramLong = seekToUs(paramLong);
        i = i1;
        for (;;)
        {
          l = paramLong;
          if (i >= paramArrayOfSampleStream.length) {
            break;
          }
          if (paramArrayOfSampleStream[i] != null) {
            paramArrayOfBoolean2[i] = true;
          }
          i += 1;
        }
      }
    }
    seenFirstTrackSelection = true;
    return l;
  }
  
  int skipData(int paramInt, long paramLong)
  {
    boolean bool = suppressRead();
    int i = 0;
    if (bool) {
      return 0;
    }
    maybeNotifyDownstreamFormat(paramInt);
    SampleQueue localSampleQueue = sampleQueues[paramInt];
    if ((loadingFinished) && (paramLong > localSampleQueue.getLargestQueuedTimestampUs()))
    {
      i = localSampleQueue.advanceToEnd();
    }
    else
    {
      int j = localSampleQueue.advanceTo(paramLong, true, true);
      if (j != -1) {
        i = j;
      }
    }
    if (i == 0) {
      maybeStartDeferredRetry(paramInt);
    }
    return i;
  }
  
  public TrackOutput track(int paramInt1, int paramInt2)
  {
    int i = sampleQueues.length;
    paramInt2 = 0;
    while (paramInt2 < i)
    {
      if (sampleQueueTrackIds[paramInt2] == paramInt1) {
        return sampleQueues[paramInt2];
      }
      paramInt2 += 1;
    }
    SampleQueue localSampleQueue = new SampleQueue(allocator);
    localSampleQueue.setUpstreamFormatChangeListener(this);
    Object localObject = sampleQueueTrackIds;
    paramInt2 = i + 1;
    sampleQueueTrackIds = Arrays.copyOf((int[])localObject, paramInt2);
    sampleQueueTrackIds[i] = paramInt1;
    localObject = (SampleQueue[])Arrays.copyOf(sampleQueues, paramInt2);
    localObject[i] = localSampleQueue;
    sampleQueues = ((SampleQueue[])Util.castNonNullTypeArray((Object[])localObject));
    return localSampleQueue;
  }
  
  final class ExtractingLoadable
    implements Loader.Loadable
  {
    private final StatsDataSource dataSource;
    private DataSpec dataSpec;
    private final ExtractorMediaPeriod.ExtractorHolder extractorHolder;
    private final ExtractorOutput extractorOutput;
    private long length;
    private volatile boolean loadCanceled;
    private final ConditionVariable loadCondition;
    private boolean pendingExtractorSeek;
    private final PositionHolder positionHolder;
    private long seekTimeUs;
    private final Uri uri;
    
    public ExtractingLoadable(Uri paramUri, DataSource paramDataSource, ExtractorMediaPeriod.ExtractorHolder paramExtractorHolder, ExtractorOutput paramExtractorOutput, ConditionVariable paramConditionVariable)
    {
      uri = paramUri;
      dataSource = new StatsDataSource(paramDataSource);
      extractorHolder = paramExtractorHolder;
      extractorOutput = paramExtractorOutput;
      loadCondition = paramConditionVariable;
      positionHolder = new PositionHolder();
      pendingExtractorSeek = true;
      length = -1L;
      dataSpec = new DataSpec(paramUri, positionHolder.position, -1L, customCacheKey);
    }
    
    private void setLoadPosition(long paramLong1, long paramLong2)
    {
      positionHolder.position = paramLong1;
      seekTimeUs = paramLong2;
      pendingExtractorSeek = true;
    }
    
    public void cancelLoad()
    {
      loadCanceled = true;
    }
    
    public void load()
      throws IOException, InterruptedException
    {
      int i = 0;
      Object localObject2;
      for (;;)
      {
        if ((i != 0) || (loadCanceled)) {
          return;
        }
        Object localObject3 = null;
        try
        {
          long l2 = positionHolder.position;
          dataSpec = new DataSpec(uri, l2, -1L, customCacheKey);
          length = dataSource.open(dataSpec);
          long l1 = length;
          if (l1 != -1L)
          {
            l1 = length;
            length = (l1 + l2);
          }
          Object localObject1 = (Uri)Assertions.checkNotNull(dataSource.getUri());
          localObject2 = new DefaultExtractorInput(dataSource, l2, length);
          int k = i;
          try
          {
            localObject1 = extractorHolder.selectExtractor((ExtractorInput)localObject2, extractorOutput, (Uri)localObject1);
            k = i;
            boolean bool = pendingExtractorSeek;
            int j = i;
            l1 = l2;
            if (bool)
            {
              k = i;
              ((Extractor)localObject1).seek(l2, seekTimeUs);
              k = i;
              pendingExtractorSeek = false;
              l1 = l2;
              j = i;
            }
            for (;;)
            {
              if (j == 0)
              {
                k = j;
                bool = loadCanceled;
                if (!bool)
                {
                  k = j;
                  loadCondition.block();
                  k = j;
                  j = ((Extractor)localObject1).read((ExtractorInput)localObject2, positionHolder);
                  try
                  {
                    long l3 = ((ExtractorInput)localObject2).getPosition();
                    long l4 = continueLoadingCheckIntervalBytes;
                    l2 = l1;
                    if (l3 > l4 + l1)
                    {
                      l2 = ((ExtractorInput)localObject2).getPosition();
                      loadCondition.close();
                      handler.post(onContinueLoadingRequestedRunnable);
                    }
                    l1 = l2;
                  }
                  catch (Throwable localThrowable1)
                  {
                    k = j;
                    break label359;
                  }
                }
              }
            }
            if (j == 1)
            {
              i = 0;
            }
            else
            {
              positionHolder.position = ((ExtractorInput)localObject2).getPosition();
              i = j;
            }
            Util.closeQuietly(dataSource);
          }
          catch (Throwable localThrowable2)
          {
            label359:
            i = k;
          }
          if (i == 1) {
            break label394;
          }
        }
        catch (Throwable localThrowable3)
        {
          localObject2 = localObject3;
        }
      }
      if (localObject2 != null) {
        positionHolder.position = ((ExtractorInput)localObject2).getPosition();
      }
      label394:
      Util.closeQuietly(dataSource);
      throw localThrowable3;
    }
  }
  
  private static final class ExtractorHolder
  {
    @Nullable
    private Extractor extractor;
    private final Extractor[] extractors;
    
    public ExtractorHolder(Extractor[] paramArrayOfExtractor)
    {
      extractors = paramArrayOfExtractor;
    }
    
    public void release()
    {
      if (extractor != null)
      {
        extractor.release();
        extractor = null;
      }
    }
    
    public Extractor selectExtractor(ExtractorInput paramExtractorInput, ExtractorOutput paramExtractorOutput, Uri paramUri)
      throws IOException, InterruptedException
    {
      if (extractor != null) {
        return extractor;
      }
      Extractor[] arrayOfExtractor = extractors;
      int j = arrayOfExtractor.length;
      int i = 0;
      while (i < j)
      {
        Extractor localExtractor = arrayOfExtractor[i];
        try
        {
          try
          {
            boolean bool = localExtractor.sniff(paramExtractorInput);
            if (bool)
            {
              extractor = localExtractor;
              paramExtractorInput.resetPeekPosition();
            }
          }
          catch (Throwable paramExtractorOutput)
          {
            paramExtractorInput.resetPeekPosition();
            throw paramExtractorOutput;
          }
        }
        catch (EOFException localEOFException)
        {
          for (;;) {}
        }
        paramExtractorInput.resetPeekPosition();
        i += 1;
      }
      if (extractor != null)
      {
        extractor.init(paramExtractorOutput);
        return extractor;
      }
      paramExtractorInput = new StringBuilder();
      paramExtractorInput.append("None of the available extractors (");
      paramExtractorInput.append(Util.getCommaDelimitedSimpleClassNames(extractors));
      paramExtractorInput.append(") could read the stream.");
      throw new UnrecognizedInputFormatException(paramExtractorInput.toString(), paramUri);
    }
  }
  
  static abstract interface Listener
  {
    public abstract void onSourceInfoRefreshed(long paramLong, boolean paramBoolean);
  }
  
  private static final class PreparedState
  {
    public final SeekMap seekMap;
    public final boolean[] trackEnabledStates;
    public final boolean[] trackIsAudioVideoFlags;
    public final boolean[] trackNotifiedDownstreamFormats;
    public final TrackGroupArray tracks;
    
    public PreparedState(SeekMap paramSeekMap, TrackGroupArray paramTrackGroupArray, boolean[] paramArrayOfBoolean)
    {
      seekMap = paramSeekMap;
      tracks = paramTrackGroupArray;
      trackIsAudioVideoFlags = paramArrayOfBoolean;
      trackEnabledStates = new boolean[length];
      trackNotifiedDownstreamFormats = new boolean[length];
    }
  }
  
  private final class SampleStreamImpl
    implements SampleStream
  {
    private final int track;
    
    public SampleStreamImpl(int paramInt)
    {
      track = paramInt;
    }
    
    public boolean isReady()
    {
      return isReady(track);
    }
    
    public void maybeThrowError()
      throws IOException
    {
      ExtractorMediaPeriod.this.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
    {
      return readData(track, paramFormatHolder, paramDecoderInputBuffer, paramBoolean);
    }
    
    public int skipData(long paramLong)
    {
      return skipData(track, paramLong);
    }
  }
}
