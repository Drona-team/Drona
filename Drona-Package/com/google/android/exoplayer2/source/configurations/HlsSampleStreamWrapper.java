package com.google.android.exoplayer2.source.configurations;

import android.os.Handler;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.configurations.PrivFrame;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.upstream.Loader.ReleaseCallback;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class HlsSampleStreamWrapper
  implements Loader.Callback<Chunk>, Loader.ReleaseCallback, SequenceableLoader, ExtractorOutput, SampleQueue.UpstreamFormatChangedListener
{
  private static final String PAGE_KEY = "HlsSampleStreamWrapper";
  public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_FATAL = -2;
  public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_NON_FATAL = -3;
  public static final int SAMPLE_QUEUE_INDEX_PENDING = -1;
  private final Allocator allocator;
  private int audioSampleQueueIndex;
  private boolean audioSampleQueueMappingDone;
  private final Callback callback;
  private final HlsChunkSource chunkSource;
  private int chunkUid;
  private Format downstreamTrackFormat;
  private int enabledTrackGroupCount;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Handler handler;
  private boolean haveAudioVideoSampleQueues;
  private final ArrayList<com.google.android.exoplayer2.source.hls.HlsSampleStream> hlsSampleStreams;
  private long lastSeekPositionUs;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private final ArrayList<com.google.android.exoplayer2.source.hls.HlsMediaChunk> mediaChunks;
  private final Format muxedAudioFormat;
  private final HlsChunkSource.HlsChunkHolder nextChunkHolder;
  private final Runnable onTracksEndedRunnable;
  private TrackGroupArray optionalTrackGroups;
  private long pendingResetPositionUs;
  private boolean pendingResetUpstreamFormats;
  private boolean prepared;
  private int primarySampleQueueIndex;
  private int primarySampleQueueType;
  private int primaryTrackGroupIndex;
  private final List<com.google.android.exoplayer2.source.hls.HlsMediaChunk> readOnlyMediaChunks;
  private boolean released;
  private long sampleOffsetUs;
  private boolean[] sampleQueueIsAudioVideoFlags;
  private int[] sampleQueueTrackIds;
  private SampleQueue[] sampleQueues;
  private boolean sampleQueuesBuilt;
  private boolean[] sampleQueuesEnabledStates;
  private boolean seenFirstTrackSelection;
  private int[] trackGroupToSampleQueueIndex;
  private TrackGroupArray trackGroups;
  private final int trackType;
  private boolean tracksEnded;
  private Format upstreamTrackFormat;
  private int videoSampleQueueIndex;
  private boolean videoSampleQueueMappingDone;
  
  public HlsSampleStreamWrapper(int paramInt, Callback paramCallback, HlsChunkSource paramHlsChunkSource, Allocator paramAllocator, long paramLong, Format paramFormat, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher paramEventDispatcher)
  {
    trackType = paramInt;
    callback = paramCallback;
    chunkSource = paramHlsChunkSource;
    allocator = paramAllocator;
    muxedAudioFormat = paramFormat;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    eventDispatcher = paramEventDispatcher;
    loader = new Loader("Loader:HlsSampleStreamWrapper");
    nextChunkHolder = new HlsChunkSource.HlsChunkHolder();
    sampleQueueTrackIds = new int[0];
    audioSampleQueueIndex = -1;
    videoSampleQueueIndex = -1;
    sampleQueues = new SampleQueue[0];
    sampleQueueIsAudioVideoFlags = new boolean[0];
    sampleQueuesEnabledStates = new boolean[0];
    mediaChunks = new ArrayList();
    readOnlyMediaChunks = Collections.unmodifiableList(mediaChunks);
    hlsSampleStreams = new ArrayList();
    maybeFinishPrepareRunnable = new -..Lambda.HlsSampleStreamWrapper.8JyeEr0irIOShv9LlAxAmgzl5vY(this);
    onTracksEndedRunnable = new -..Lambda.HlsSampleStreamWrapper.afhkI3tagC_-MAOTh7FzBWzQsno(this);
    handler = new Handler();
    lastSeekPositionUs = paramLong;
    pendingResetPositionUs = paramLong;
  }
  
  private void buildTracksFromSampleStreams()
  {
    int i2 = sampleQueues.length;
    boolean bool = false;
    int j = 0;
    int m = 6;
    Object localObject;
    for (int n = -1;; n = k)
    {
      i = 2;
      if (j >= i2) {
        break;
      }
      localObject = sampleQueues[j].getUpstreamFormat().sampleMimeType;
      if (!MimeTypes.isVideo((String)localObject)) {
        if (MimeTypes.isAudio((String)localObject)) {
          i = 1;
        } else if (MimeTypes.isText((String)localObject)) {
          i = 3;
        } else {
          i = 6;
        }
      }
      int i1;
      if (getTrackTypeScore(i) > getTrackTypeScore(m))
      {
        k = j;
        i1 = i;
      }
      else
      {
        i1 = m;
        k = n;
        if (i == m)
        {
          i1 = m;
          k = n;
          if (n != -1)
          {
            k = -1;
            i1 = m;
          }
        }
      }
      j += 1;
      m = i1;
    }
    TrackGroup localTrackGroup = chunkSource.getTrackGroup();
    int k = length;
    primaryTrackGroupIndex = -1;
    trackGroupToSampleQueueIndex = new int[i2];
    int i = 0;
    while (i < i2)
    {
      trackGroupToSampleQueueIndex[i] = i;
      i += 1;
    }
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[i2];
    i = 0;
    while (i < i2)
    {
      Format localFormat = sampleQueues[i].getUpstreamFormat();
      if (i == n)
      {
        localObject = new Format[k];
        if (k == 1)
        {
          localObject[0] = localFormat.copyWithManifestFormatInfo(localTrackGroup.getFormat(0));
        }
        else
        {
          j = 0;
          while (j < k)
          {
            localObject[j] = deriveFormat(localTrackGroup.getFormat(j), localFormat, true);
            j += 1;
          }
        }
        arrayOfTrackGroup[i] = new TrackGroup((Format[])localObject);
        primaryTrackGroupIndex = i;
      }
      else
      {
        if ((m == 2) && (MimeTypes.isAudio(sampleMimeType))) {
          localObject = muxedAudioFormat;
        } else {
          localObject = null;
        }
        arrayOfTrackGroup[i] = new TrackGroup(new Format[] { deriveFormat((Format)localObject, localFormat, false) });
      }
      i += 1;
    }
    trackGroups = new TrackGroupArray(arrayOfTrackGroup);
    if (optionalTrackGroups == null) {
      bool = true;
    }
    Assertions.checkState(bool);
    optionalTrackGroups = TrackGroupArray.EMPTY;
  }
  
  private static DummyTrackOutput createDummyTrackOutput(int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unmapped track with id ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(" of type ");
    localStringBuilder.append(paramInt2);
    Log.w("HlsSampleStreamWrapper", localStringBuilder.toString());
    return new DummyTrackOutput();
  }
  
  private static Format deriveFormat(Format paramFormat1, Format paramFormat2, boolean paramBoolean)
  {
    if (paramFormat1 == null) {
      return paramFormat2;
    }
    int i;
    if (paramBoolean) {
      i = bitrate;
    } else {
      i = -1;
    }
    int j = MimeTypes.getTrackType(sampleMimeType);
    String str3 = Util.getCodecsOfType(codecs, j);
    String str2 = MimeTypes.getMediaMimeType(str3);
    String str1 = str2;
    if (str2 == null) {
      str1 = sampleMimeType;
    }
    return paramFormat2.copyWithContainerInfo(mimeType, label, str1, str3, i, width, height, selectionFlags, language);
  }
  
  private boolean finishedReadingChunk(HlsMediaChunk paramHlsMediaChunk)
  {
    int j = sampleQueue;
    int k = sampleQueues.length;
    int i = 0;
    while (i < k)
    {
      if ((sampleQueuesEnabledStates[i] != 0) && (sampleQueues[i].peekSourceId() == j)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private static boolean formatsMatch(Format paramFormat1, Format paramFormat2)
  {
    String str1 = sampleMimeType;
    String str2 = sampleMimeType;
    int i = MimeTypes.getTrackType(str1);
    if (i != 3)
    {
      if (i == MimeTypes.getTrackType(str2)) {
        return true;
      }
    }
    else
    {
      if (!Util.areEqual(str1, str2)) {
        return false;
      }
      if ((!"application/cea-608".equals(str1)) && (!"application/cea-708".equals(str1))) {
        return true;
      }
      if (accessibilityChannel == accessibilityChannel) {
        return true;
      }
    }
    return false;
  }
  
  private HlsMediaChunk getLastMediaChunk()
  {
    return (HlsMediaChunk)mediaChunks.get(mediaChunks.size() - 1);
  }
  
  private static int getTrackTypeScore(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 3: 
      return 1;
    case 2: 
      return 3;
    }
    return 2;
  }
  
  private static boolean isMediaChunk(Chunk paramChunk)
  {
    return paramChunk instanceof HlsMediaChunk;
  }
  
  private boolean isPendingReset()
  {
    return pendingResetPositionUs != -9223372036854775807L;
  }
  
  private void mapSampleQueuesToMatchTrackGroups()
  {
    Object localObject2 = trackGroups;
    Object localObject1 = this;
    int k = length;
    trackGroupToSampleQueueIndex = new int[k];
    Arrays.fill(trackGroupToSampleQueueIndex, -1);
    int i = 0;
    while (i < k)
    {
      int j = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (j >= sampleQueues.length) {
          break;
        }
        localObject2 = sampleQueues[j].getUpstreamFormat();
        TrackGroupArray localTrackGroupArray = trackGroups;
        if (formatsMatch((Format)localObject2, localTrackGroupArray.context(i).getFormat(0)))
        {
          trackGroupToSampleQueueIndex[i] = j;
          localObject2 = localObject1;
          break;
        }
        j += 1;
      }
      i += 1;
      localObject1 = localObject2;
    }
    localObject1 = hlsSampleStreams.iterator();
    while (((Iterator)localObject1).hasNext()) {
      ((HlsSampleStream)((Iterator)localObject1).next()).bindSampleQueue();
    }
  }
  
  private void maybeFinishPrepare()
  {
    if ((!released) && (trackGroupToSampleQueueIndex == null))
    {
      if (!sampleQueuesBuilt) {
        return;
      }
      SampleQueue[] arrayOfSampleQueue = sampleQueues;
      int j = arrayOfSampleQueue.length;
      int i = 0;
      while (i < j)
      {
        if (arrayOfSampleQueue[i].getUpstreamFormat() == null) {
          return;
        }
        i += 1;
      }
      if (trackGroups != null)
      {
        mapSampleQueuesToMatchTrackGroups();
        return;
      }
      buildTracksFromSampleStreams();
      prepared = true;
      callback.onPrepared();
    }
  }
  
  private void onTracksEnded()
  {
    sampleQueuesBuilt = true;
    maybeFinishPrepare();
  }
  
  private void resetSampleQueues()
  {
    SampleQueue[] arrayOfSampleQueue = sampleQueues;
    int j = arrayOfSampleQueue.length;
    int i = 0;
    while (i < j)
    {
      arrayOfSampleQueue[i].reset(pendingResetUpstreamFormats);
      i += 1;
    }
    pendingResetUpstreamFormats = false;
  }
  
  private boolean seekInsideBufferUs(long paramLong)
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
        if (sampleQueueIsAudioVideoFlags[i] != 0) {
          break label81;
        }
        if (!haveAudioVideoSampleQueues) {
          return false;
        }
      }
      i += 1;
    }
    return true;
    label81:
    return false;
  }
  
  private void updateSampleStreams(SampleStream[] paramArrayOfSampleStream)
  {
    hlsSampleStreams.clear();
    int j = paramArrayOfSampleStream.length;
    int i = 0;
    while (i < j)
    {
      SampleStream localSampleStream = paramArrayOfSampleStream[i];
      if (localSampleStream != null) {
        hlsSampleStreams.add((HlsSampleStream)localSampleStream);
      }
      i += 1;
    }
  }
  
  public int bindSampleQueueToSampleStream(int paramInt)
  {
    int i = trackGroupToSampleQueueIndex[paramInt];
    if (i == -1)
    {
      if (optionalTrackGroups.indexOf(trackGroups.context(paramInt)) == -1) {
        return -2;
      }
      return -3;
    }
    if (sampleQueuesEnabledStates[i] != 0) {
      return -2;
    }
    sampleQueuesEnabledStates[i] = true;
    return i;
  }
  
  public boolean continueLoading(long paramLong)
  {
    if (!loadingFinished)
    {
      if (loader.isLoading()) {
        return false;
      }
      long l;
      if (isPendingReset())
      {
        localObject1 = Collections.emptyList();
        l = pendingResetPositionUs;
      }
      for (;;)
      {
        break;
        localObject1 = readOnlyMediaChunks;
        localObject2 = getLastMediaChunk();
        if (((HlsMediaChunk)localObject2).isLoadCompleted()) {
          l = endTimeUs;
        } else {
          l = Math.max(lastSeekPositionUs, startTimeUs);
        }
      }
      chunkSource.getNextChunk(paramLong, l, (List)localObject1, nextChunkHolder);
      boolean bool = nextChunkHolder.endOfStream;
      Object localObject1 = nextChunkHolder.chunk;
      Object localObject2 = nextChunkHolder.playlist;
      nextChunkHolder.clear();
      if (bool)
      {
        pendingResetPositionUs = -9223372036854775807L;
        loadingFinished = true;
        return true;
      }
      if (localObject1 == null)
      {
        if (localObject2 != null)
        {
          callback.onPlaylistRefreshRequired((HlsMasterPlaylist.HlsUrl)localObject2);
          return false;
        }
      }
      else
      {
        if (isMediaChunk((Chunk)localObject1))
        {
          pendingResetPositionUs = -9223372036854775807L;
          localObject2 = (HlsMediaChunk)localObject1;
          ((HlsMediaChunk)localObject2).init(this);
          mediaChunks.add(localObject2);
          upstreamTrackFormat = trackFormat;
        }
        paramLong = loader.startLoading((Loader.Loadable)localObject1, this, loadErrorHandlingPolicy.getMinimumLoadableRetryCount(type));
        eventDispatcher.loadStarted(dataSpec, type, trackType, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, paramLong);
        return true;
      }
    }
    return false;
  }
  
  public void continuePreparing()
  {
    if (!prepared) {
      continueLoading(lastSeekPositionUs);
    }
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    if (sampleQueuesBuilt)
    {
      if (isPendingReset()) {
        return;
      }
      int j = sampleQueues.length;
      int i = 0;
      while (i < j)
      {
        sampleQueues[i].discardTo(paramLong, paramBoolean, sampleQueuesEnabledStates[i]);
        i += 1;
      }
    }
  }
  
  public void endTracks()
  {
    tracksEnded = true;
    handler.post(onTracksEndedRunnable);
  }
  
  public long getBufferedPositionUs()
  {
    if (loadingFinished) {
      return Long.MIN_VALUE;
    }
    if (isPendingReset()) {
      return pendingResetPositionUs;
    }
    long l2 = lastSeekPositionUs;
    HlsMediaChunk localHlsMediaChunk = getLastMediaChunk();
    Object localObject = localHlsMediaChunk;
    if (!localHlsMediaChunk.isLoadCompleted()) {
      if (mediaChunks.size() > 1) {
        localObject = (HlsMediaChunk)mediaChunks.get(mediaChunks.size() - 2);
      } else {
        localObject = null;
      }
    }
    long l1 = l2;
    if (localObject != null) {
      l1 = Math.max(l2, endTimeUs);
    }
    l2 = l1;
    if (sampleQueuesBuilt)
    {
      localObject = sampleQueues;
      int j = localObject.length;
      int i = 0;
      for (;;)
      {
        l2 = l1;
        if (i >= j) {
          break;
        }
        l1 = Math.max(l1, localObject[i].getLargestQueuedTimestampUs());
        i += 1;
      }
    }
    return l2;
  }
  
  public long getNextLoadPositionUs()
  {
    if (isPendingReset()) {
      return pendingResetPositionUs;
    }
    if (loadingFinished) {
      return Long.MIN_VALUE;
    }
    return getLastMediaChunkendTimeUs;
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return trackGroups;
  }
  
  public void init(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    if (!paramBoolean2)
    {
      audioSampleQueueMappingDone = false;
      videoSampleQueueMappingDone = false;
    }
    chunkUid = paramInt;
    SampleQueue[] arrayOfSampleQueue = sampleQueues;
    int k = arrayOfSampleQueue.length;
    int i = 0;
    while (i < k)
    {
      arrayOfSampleQueue[i].sourceId(paramInt);
      i += 1;
    }
    if (paramBoolean1)
    {
      arrayOfSampleQueue = sampleQueues;
      i = arrayOfSampleQueue.length;
      paramInt = j;
      while (paramInt < i)
      {
        arrayOfSampleQueue[paramInt].splice();
        paramInt += 1;
      }
    }
  }
  
  public boolean isReady(int paramInt)
  {
    return (loadingFinished) || ((!isPendingReset()) && (sampleQueues[paramInt].hasNextSample()));
  }
  
  public void maybeThrowError()
    throws IOException
  {
    loader.maybeThrowError();
    chunkSource.maybeThrowError();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    maybeThrowError();
  }
  
  public void onLoadCanceled(Chunk paramChunk, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    eventDispatcher.loadCanceled(dataSpec, paramChunk.getUri(), paramChunk.getResponseHeaders(), type, trackType, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!paramBoolean)
    {
      resetSampleQueues();
      if (enabledTrackGroupCount > 0) {
        callback.onContinueLoadingRequested(this);
      }
    }
  }
  
  public void onLoadCompleted(Chunk paramChunk, long paramLong1, long paramLong2)
  {
    chunkSource.onChunkLoadCompleted(paramChunk);
    eventDispatcher.loadCompleted(dataSpec, paramChunk.getUri(), paramChunk.getResponseHeaders(), type, trackType, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!prepared)
    {
      continueLoading(lastSeekPositionUs);
      return;
    }
    callback.onContinueLoadingRequested(this);
  }
  
  public Loader.LoadErrorAction onLoadError(Chunk paramChunk, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
  {
    long l1 = paramChunk.bytesLoaded();
    boolean bool3 = isMediaChunk(paramChunk);
    long l2 = loadErrorHandlingPolicy.getBlacklistDurationMsFor(type, paramLong2, paramIOException, paramInt);
    boolean bool2 = false;
    boolean bool1;
    if (l2 != -9223372036854775807L) {
      bool1 = chunkSource.maybeBlacklistTrack(paramChunk, l2);
    } else {
      bool1 = false;
    }
    Loader.LoadErrorAction localLoadErrorAction;
    if (bool1)
    {
      if ((bool3) && (l1 == 0L))
      {
        if ((HlsMediaChunk)mediaChunks.remove(mediaChunks.size() - 1) == paramChunk) {
          bool2 = true;
        }
        Assertions.checkState(bool2);
        if (mediaChunks.isEmpty()) {
          pendingResetPositionUs = lastSeekPositionUs;
        }
      }
      localLoadErrorAction = Loader.DONT_RETRY;
    }
    for (;;)
    {
      break;
      l2 = loadErrorHandlingPolicy.getRetryDelayMsFor(type, paramLong2, paramIOException, paramInt);
      if (l2 != -9223372036854775807L) {
        localLoadErrorAction = Loader.createRetryAction(false, l2);
      } else {
        localLoadErrorAction = Loader.DONT_RETRY_FATAL;
      }
    }
    eventDispatcher.loadError(dataSpec, paramChunk.getUri(), paramChunk.getResponseHeaders(), type, trackType, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, paramLong1, paramLong2, l1, paramIOException, localLoadErrorAction.isRetry() ^ true);
    if (bool1)
    {
      if (!prepared)
      {
        continueLoading(lastSeekPositionUs);
        return localLoadErrorAction;
      }
      callback.onContinueLoadingRequested(this);
    }
    return localLoadErrorAction;
  }
  
  public void onLoaderReleased()
  {
    resetSampleQueues();
  }
  
  public boolean onPlaylistError(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    return chunkSource.onPlaylistError(paramHlsUrl, paramLong);
  }
  
  public void onUpstreamFormatChanged(Format paramFormat)
  {
    handler.post(maybeFinishPrepareRunnable);
  }
  
  public void prepareWithMasterPlaylistInfo(TrackGroupArray paramTrackGroupArray1, int paramInt, TrackGroupArray paramTrackGroupArray2)
  {
    prepared = true;
    trackGroups = paramTrackGroupArray1;
    optionalTrackGroups = paramTrackGroupArray2;
    primaryTrackGroupIndex = paramInt;
    callback.onPrepared();
  }
  
  public int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    if (isPendingReset()) {
      return -3;
    }
    boolean bool = mediaChunks.isEmpty();
    int j = 0;
    if (!bool)
    {
      i = 0;
      while ((i < mediaChunks.size() - 1) && (finishedReadingChunk((HlsMediaChunk)mediaChunks.get(i)))) {
        i += 1;
      }
      Util.removeRange(mediaChunks, 0, i);
      HlsMediaChunk localHlsMediaChunk = (HlsMediaChunk)mediaChunks.get(0);
      Format localFormat = trackFormat;
      if (!localFormat.equals(downstreamTrackFormat)) {
        eventDispatcher.downstreamFormatChanged(trackType, localFormat, trackSelectionReason, trackSelectionData, startTimeUs);
      }
      downstreamTrackFormat = localFormat;
    }
    int i = sampleQueues[paramInt].read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean, loadingFinished, lastSeekPositionUs);
    if ((i == -5) && (paramInt == primarySampleQueueIndex))
    {
      int k = sampleQueues[paramInt].peekSourceId();
      paramInt = j;
      while ((paramInt < mediaChunks.size()) && (mediaChunks.get(paramInt)).sampleQueue != k)) {
        paramInt += 1;
      }
      if (paramInt < mediaChunks.size()) {
        paramDecoderInputBuffer = mediaChunks.get(paramInt)).trackFormat;
      } else {
        paramDecoderInputBuffer = upstreamTrackFormat;
      }
      format = format.copyWithManifestFormatInfo(paramDecoderInputBuffer);
    }
    return i;
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
    released = true;
    hlsSampleStreams.clear();
  }
  
  public void seekMap(SeekMap paramSeekMap) {}
  
  public boolean seekToUs(long paramLong, boolean paramBoolean)
  {
    lastSeekPositionUs = paramLong;
    if (isPendingReset())
    {
      pendingResetPositionUs = paramLong;
      return true;
    }
    if ((sampleQueuesBuilt) && (!paramBoolean) && (seekInsideBufferUs(paramLong))) {
      return false;
    }
    pendingResetPositionUs = paramLong;
    loadingFinished = false;
    mediaChunks.clear();
    if (loader.isLoading())
    {
      loader.cancelLoading();
      return true;
    }
    resetSampleQueues();
    return true;
  }
  
  public boolean selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong, boolean paramBoolean)
  {
    Assertions.checkState(prepared);
    int j = enabledTrackGroupCount;
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] != null) && ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean1[i] == 0)))
      {
        enabledTrackGroupCount -= 1;
        ((HlsSampleStream)paramArrayOfSampleStream[i]).unbindSampleQueue();
        paramArrayOfSampleStream[i] = null;
      }
      i += 1;
    }
    boolean bool1;
    if ((!paramBoolean) && (seenFirstTrackSelection ? j != 0 : paramLong == lastSeekPositionUs)) {
      bool1 = false;
    } else {
      bool1 = true;
    }
    TrackSelection localTrackSelection = chunkSource.getTrackSelection();
    paramArrayOfBoolean1 = localTrackSelection;
    i = 0;
    boolean bool2;
    Object localObject;
    while (i < paramArrayOfTrackSelection.length)
    {
      bool2 = bool1;
      localObject = paramArrayOfBoolean1;
      if (paramArrayOfSampleStream[i] == null)
      {
        bool2 = bool1;
        localObject = paramArrayOfBoolean1;
        if (paramArrayOfTrackSelection[i] != null)
        {
          enabledTrackGroupCount += 1;
          localObject = paramArrayOfTrackSelection[i];
          j = trackGroups.indexOf(((TrackSelection)localObject).getTrackGroup());
          if (j == primaryTrackGroupIndex)
          {
            chunkSource.selectTracks((TrackSelection)localObject);
            paramArrayOfBoolean1 = (boolean[])localObject;
          }
          paramArrayOfSampleStream[i] = new HlsSampleStream(this, j);
          paramArrayOfBoolean2[i] = true;
          if (trackGroupToSampleQueueIndex != null) {
            ((HlsSampleStream)paramArrayOfSampleStream[i]).bindSampleQueue();
          }
          bool2 = bool1;
          localObject = paramArrayOfBoolean1;
          if (sampleQueuesBuilt)
          {
            bool2 = bool1;
            localObject = paramArrayOfBoolean1;
            if (!bool1)
            {
              localObject = sampleQueues[trackGroupToSampleQueueIndex[j]];
              ((SampleQueue)localObject).rewind();
              if ((((SampleQueue)localObject).advanceTo(paramLong, true, true) == -1) && (((SampleQueue)localObject).getReadIndex() != 0))
              {
                bool2 = true;
                localObject = paramArrayOfBoolean1;
              }
              else
              {
                bool2 = false;
                localObject = paramArrayOfBoolean1;
              }
            }
          }
        }
      }
      i += 1;
      bool1 = bool2;
      paramArrayOfBoolean1 = (boolean[])localObject;
    }
    if (enabledTrackGroupCount == 0)
    {
      chunkSource.reset();
      downstreamTrackFormat = null;
      mediaChunks.clear();
      if (loader.isLoading())
      {
        if (sampleQueuesBuilt)
        {
          paramArrayOfTrackSelection = sampleQueues;
          j = paramArrayOfTrackSelection.length;
          i = 0;
          while (i < j)
          {
            paramArrayOfTrackSelection[i].discardToEnd();
            i += 1;
          }
        }
        loader.cancelLoading();
      }
      else
      {
        resetSampleQueues();
      }
    }
    else
    {
      bool2 = bool1;
      boolean bool3 = paramBoolean;
      if (!mediaChunks.isEmpty())
      {
        bool2 = bool1;
        bool3 = paramBoolean;
        if (!Util.areEqual(paramArrayOfBoolean1, localTrackSelection))
        {
          if (!seenFirstTrackSelection)
          {
            long l = 0L;
            if (paramLong < 0L) {
              l = -paramLong;
            }
            paramArrayOfTrackSelection = getLastMediaChunk();
            localObject = chunkSource.createMediaChunkIterators(paramArrayOfTrackSelection, paramLong);
            paramArrayOfBoolean1.updateSelectedTrack(paramLong, l, -9223372036854775807L, readOnlyMediaChunks, (MediaChunkIterator[])localObject);
            i = chunkSource.getTrackGroup().indexOf(trackFormat);
            if (paramArrayOfBoolean1.getSelectedIndexInTrackGroup() == i)
            {
              i = 0;
              break label584;
            }
          }
          i = 1;
          label584:
          bool2 = bool1;
          bool3 = paramBoolean;
          if (i != 0)
          {
            pendingResetUpstreamFormats = true;
            bool3 = true;
            bool2 = true;
          }
        }
      }
      bool1 = bool2;
      if (bool2)
      {
        seekToUs(paramLong, bool3);
        i = 0;
        for (;;)
        {
          bool1 = bool2;
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
    updateSampleStreams(paramArrayOfSampleStream);
    seenFirstTrackSelection = true;
    return bool1;
  }
  
  public void setIsTimestampMaster(boolean paramBoolean)
  {
    chunkSource.setIsTimestampMaster(paramBoolean);
  }
  
  public void setSampleOffsetUs(long paramLong)
  {
    sampleOffsetUs = paramLong;
    SampleQueue[] arrayOfSampleQueue = sampleQueues;
    int j = arrayOfSampleQueue.length;
    int i = 0;
    while (i < j)
    {
      arrayOfSampleQueue[i].setSampleOffsetUs(paramLong);
      i += 1;
    }
  }
  
  public int skipData(int paramInt, long paramLong)
  {
    if (isPendingReset()) {
      return 0;
    }
    SampleQueue localSampleQueue = sampleQueues[paramInt];
    if ((loadingFinished) && (paramLong > localSampleQueue.getLargestQueuedTimestampUs())) {
      return localSampleQueue.advanceToEnd();
    }
    paramInt = localSampleQueue.advanceTo(paramLong, true, true);
    if (paramInt == -1) {
      return 0;
    }
    return paramInt;
  }
  
  public TrackOutput track(int paramInt1, int paramInt2)
  {
    int j = sampleQueues.length;
    int k = 0;
    if (paramInt2 == 1)
    {
      if (audioSampleQueueIndex != -1)
      {
        if (audioSampleQueueMappingDone)
        {
          if (sampleQueueTrackIds[audioSampleQueueIndex] == paramInt1) {
            return sampleQueues[audioSampleQueueIndex];
          }
          return createDummyTrackOutput(paramInt1, paramInt2);
        }
        audioSampleQueueMappingDone = true;
        sampleQueueTrackIds[audioSampleQueueIndex] = paramInt1;
        return sampleQueues[audioSampleQueueIndex];
      }
      if (tracksEnded) {
        return createDummyTrackOutput(paramInt1, paramInt2);
      }
    }
    else if (paramInt2 == 2)
    {
      if (videoSampleQueueIndex != -1)
      {
        if (videoSampleQueueMappingDone)
        {
          if (sampleQueueTrackIds[videoSampleQueueIndex] == paramInt1) {
            return sampleQueues[videoSampleQueueIndex];
          }
          return createDummyTrackOutput(paramInt1, paramInt2);
        }
        videoSampleQueueMappingDone = true;
        sampleQueueTrackIds[videoSampleQueueIndex] = paramInt1;
        return sampleQueues[videoSampleQueueIndex];
      }
      if (tracksEnded) {
        return createDummyTrackOutput(paramInt1, paramInt2);
      }
    }
    else
    {
      i = 0;
      while (i < j)
      {
        if (sampleQueueTrackIds[i] == paramInt1) {
          return sampleQueues[i];
        }
        i += 1;
      }
      if (tracksEnded) {
        return createDummyTrackOutput(paramInt1, paramInt2);
      }
    }
    PrivTimestampStrippingSampleQueue localPrivTimestampStrippingSampleQueue = new PrivTimestampStrippingSampleQueue(allocator);
    localPrivTimestampStrippingSampleQueue.setSampleOffsetUs(sampleOffsetUs);
    localPrivTimestampStrippingSampleQueue.sourceId(chunkUid);
    localPrivTimestampStrippingSampleQueue.setUpstreamFormatChangeListener(this);
    Object localObject = sampleQueueTrackIds;
    int i = j + 1;
    sampleQueueTrackIds = Arrays.copyOf((int[])localObject, i);
    sampleQueueTrackIds[j] = paramInt1;
    sampleQueues = ((SampleQueue[])Arrays.copyOf(sampleQueues, i));
    sampleQueues[j] = localPrivTimestampStrippingSampleQueue;
    sampleQueueIsAudioVideoFlags = Arrays.copyOf(sampleQueueIsAudioVideoFlags, i);
    localObject = sampleQueueIsAudioVideoFlags;
    if ((paramInt2 == 1) || (paramInt2 == 2)) {
      k = 1;
    }
    localObject[j] = k;
    haveAudioVideoSampleQueues |= sampleQueueIsAudioVideoFlags[j];
    if (paramInt2 == 1)
    {
      audioSampleQueueMappingDone = true;
      audioSampleQueueIndex = j;
    }
    else if (paramInt2 == 2)
    {
      videoSampleQueueMappingDone = true;
      videoSampleQueueIndex = j;
    }
    if (getTrackTypeScore(paramInt2) > getTrackTypeScore(primarySampleQueueType))
    {
      primarySampleQueueIndex = j;
      primarySampleQueueType = paramInt2;
    }
    sampleQueuesEnabledStates = Arrays.copyOf(sampleQueuesEnabledStates, i);
    return localPrivTimestampStrippingSampleQueue;
  }
  
  public void unbindSampleQueue(int paramInt)
  {
    paramInt = trackGroupToSampleQueueIndex[paramInt];
    Assertions.checkState(sampleQueuesEnabledStates[paramInt]);
    sampleQueuesEnabledStates[paramInt] = false;
  }
  
  public abstract interface Callback
    extends SequenceableLoader.Callback<com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper>
  {
    public abstract void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl paramHlsUrl);
    
    public abstract void onPrepared();
  }
  
  final class PrivTimestampStrippingSampleQueue
    extends SampleQueue
  {
    public PrivTimestampStrippingSampleQueue()
    {
      super();
    }
    
    private Metadata getAdjustedMetadata(Metadata paramMetadata)
    {
      if (paramMetadata == null) {
        return null;
      }
      int m = paramMetadata.length();
      int k = 0;
      int i = 0;
      while (i < m)
      {
        localObject = paramMetadata.getFormat(i);
        if (((localObject instanceof PrivFrame)) && ("com.apple.streaming.transportStreamTimestamp".equals(owner)))
        {
          j = i;
          break label68;
        }
        i += 1;
      }
      int j = -1;
      label68:
      if (j == -1) {
        return paramMetadata;
      }
      if (m == 1) {
        return null;
      }
      Object localObject = new Metadata.Entry[m - 1];
      i = k;
      while (i < m)
      {
        if (i != j)
        {
          if (i < j) {
            k = i;
          } else {
            k = i - 1;
          }
          localObject[k] = paramMetadata.getFormat(i);
        }
        i += 1;
      }
      return new Metadata((Metadata.Entry[])localObject);
    }
    
    public void format(Format paramFormat)
    {
      super.format(paramFormat.copyWithMetadata(getAdjustedMetadata(metadata)));
    }
  }
}
