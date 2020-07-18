package com.google.android.exoplayer2.source.dash;

import android.util.Pair;
import android.util.SparseIntArray;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream.EmbeddedSampleStream;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream.ReleaseCallback;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Descriptor;
import com.google.android.exoplayer2.source.dash.manifest.EventStream;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

final class DashMediaPeriod
  implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<DashChunkSource>>, ChunkSampleStream.ReleaseCallback<DashChunkSource>
{
  private final Allocator allocator;
  @Nullable
  private MediaPeriod.Callback callback;
  private final DashChunkSource.Factory chunkSourceFactory;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final long elapsedRealtimeOffset;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private EventSampleStream[] eventSampleStreams;
  private List<EventStream> eventStreams;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  final int localIndex;
  private DashManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private boolean notifiedReadingStarted;
  private int periodIndex;
  private final PlayerEmsgHandler playerEmsgHandler;
  private ChunkSampleStream<DashChunkSource>[] sampleStreams;
  private final IdentityHashMap<ChunkSampleStream<DashChunkSource>, PlayerEmsgHandler.PlayerTrackEmsgHandler> trackEmsgHandlerBySampleStream;
  private final TrackGroupInfo[] trackGroupInfos;
  private final TrackGroupArray trackGroups;
  @Nullable
  private final TransferListener transferListener;
  
  public DashMediaPeriod(int paramInt1, DashManifest paramDashManifest, int paramInt2, DashChunkSource.Factory paramFactory, TransferListener paramTransferListener, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher paramEventDispatcher, long paramLong, LoaderErrorThrower paramLoaderErrorThrower, Allocator paramAllocator, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, PlayerEmsgHandler.PlayerEmsgCallback paramPlayerEmsgCallback)
  {
    localIndex = paramInt1;
    manifest = paramDashManifest;
    periodIndex = paramInt2;
    chunkSourceFactory = paramFactory;
    transferListener = paramTransferListener;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    eventDispatcher = paramEventDispatcher;
    elapsedRealtimeOffset = paramLong;
    manifestLoaderErrorThrower = paramLoaderErrorThrower;
    allocator = paramAllocator;
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    playerEmsgHandler = new PlayerEmsgHandler(paramDashManifest, paramPlayerEmsgCallback, paramAllocator);
    sampleStreams = newSampleStreamArray(0);
    eventSampleStreams = new EventSampleStream[0];
    trackEmsgHandlerBySampleStream = new IdentityHashMap();
    compositeSequenceableLoader = paramCompositeSequenceableLoaderFactory.createCompositeSequenceableLoader(sampleStreams);
    paramDashManifest = paramDashManifest.getPeriod(paramInt2);
    eventStreams = eventStreams;
    paramDashManifest = buildTrackGroups(adaptationSets, eventStreams);
    trackGroups = ((TrackGroupArray)first);
    trackGroupInfos = ((TrackGroupInfo[])second);
    paramEventDispatcher.mediaPeriodCreated();
  }
  
  private static void buildManifestEventTrackGroupInfos(List paramList, TrackGroup[] paramArrayOfTrackGroup, TrackGroupInfo[] paramArrayOfTrackGroupInfo, int paramInt)
  {
    int i = 0;
    while (i < paramList.size())
    {
      paramArrayOfTrackGroup[paramInt] = new TrackGroup(new Format[] { Format.createSampleFormat(((EventStream)paramList.get(i)).resolve(), "application/x-emsg", null, -1, null) });
      paramArrayOfTrackGroupInfo[paramInt] = TrackGroupInfo.mpdEventTrack(i);
      i += 1;
      paramInt += 1;
    }
  }
  
  private static int buildPrimaryAndEmbeddedTrackGroupInfos(List paramList, int[][] paramArrayOfInt, int paramInt, boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2, TrackGroup[] paramArrayOfTrackGroup, TrackGroupInfo[] paramArrayOfTrackGroupInfo)
  {
    int j = 0;
    int i;
    for (int k = 0; j < paramInt; k = i)
    {
      int[] arrayOfInt = paramArrayOfInt[j];
      Object localObject1 = new ArrayList();
      int m = arrayOfInt.length;
      i = 0;
      while (i < m)
      {
        ((List)localObject1).addAll(getrepresentations);
        i += 1;
      }
      Object localObject2 = new Format[((List)localObject1).size()];
      i = 0;
      while (i < localObject2.length)
      {
        localObject2[i] = getformat;
        i += 1;
      }
      localObject1 = (AdaptationSet)paramList.get(arrayOfInt[0]);
      i = k + 1;
      int n;
      if (paramArrayOfBoolean1[j] != 0)
      {
        n = i + 1;
        m = i;
        i = n;
      }
      else
      {
        m = -1;
      }
      if (paramArrayOfBoolean2[j] != 0)
      {
        int i1 = i + 1;
        n = i;
        i = i1;
      }
      else
      {
        n = -1;
      }
      paramArrayOfTrackGroup[k] = new TrackGroup((Format[])localObject2);
      paramArrayOfTrackGroupInfo[k] = TrackGroupInfo.primaryTrack(type, arrayOfInt, k, m, n);
      if (m != -1)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(nodeId);
        ((StringBuilder)localObject2).append(":emsg");
        paramArrayOfTrackGroup[m] = new TrackGroup(new Format[] { Format.createSampleFormat(((StringBuilder)localObject2).toString(), "application/x-emsg", null, -1, null) });
        paramArrayOfTrackGroupInfo[m] = TrackGroupInfo.embeddedEmsgTrack(arrayOfInt, k);
      }
      if (n != -1)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(nodeId);
        ((StringBuilder)localObject2).append(":cea608");
        paramArrayOfTrackGroup[n] = new TrackGroup(new Format[] { Format.createTextSampleFormat(((StringBuilder)localObject2).toString(), "application/cea-608", 0, null) });
        paramArrayOfTrackGroupInfo[n] = TrackGroupInfo.embeddedCea608Track(arrayOfInt, k);
      }
      j += 1;
    }
    return k;
  }
  
  private ChunkSampleStream buildSampleStream(TrackGroupInfo paramTrackGroupInfo, TrackSelection paramTrackSelection, long paramLong)
  {
    Object localObject2 = new int[2];
    Format[] arrayOfFormat2 = new Format[2];
    boolean bool1;
    if (embeddedEventMessageTrackGroupIndex != -1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    int i;
    if (bool1)
    {
      arrayOfFormat2[0] = trackGroups.context(embeddedEventMessageTrackGroupIndex).getFormat(0);
      localObject2[0] = 4;
      i = 1;
    }
    else
    {
      i = 0;
    }
    boolean bool2;
    if (embeddedCea608TrackGroupIndex != -1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    int j = i;
    if (bool2)
    {
      arrayOfFormat2[i] = trackGroups.context(embeddedCea608TrackGroupIndex).getFormat(0);
      localObject2[i] = 3;
      j = i + 1;
    }
    Object localObject1 = localObject2;
    Format[] arrayOfFormat1 = arrayOfFormat2;
    if (j < localObject2.length)
    {
      arrayOfFormat1 = (Format[])Arrays.copyOf(arrayOfFormat2, j);
      localObject1 = Arrays.copyOf((int[])localObject2, j);
    }
    if ((manifest.dynamic) && (bool1)) {
      localObject2 = playerEmsgHandler.newPlayerTrackEmsgHandler();
    } else {
      localObject2 = null;
    }
    paramTrackSelection = chunkSourceFactory.createDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndices, paramTrackSelection, trackType, elapsedRealtimeOffset, bool1, bool2, (PlayerEmsgHandler.PlayerTrackEmsgHandler)localObject2, transferListener);
    paramTrackGroupInfo = new ChunkSampleStream(trackType, (int[])localObject1, arrayOfFormat1, paramTrackSelection, this, allocator, paramLong, loadErrorHandlingPolicy, eventDispatcher);
    try
    {
      trackEmsgHandlerBySampleStream.put(paramTrackGroupInfo, localObject2);
      return paramTrackGroupInfo;
    }
    catch (Throwable paramTrackGroupInfo)
    {
      throw paramTrackGroupInfo;
    }
  }
  
  private static Pair buildTrackGroups(List paramList1, List paramList2)
  {
    int[][] arrayOfInt = getGroupedAdaptationSetIndices(paramList1);
    int i = arrayOfInt.length;
    boolean[] arrayOfBoolean1 = new boolean[i];
    boolean[] arrayOfBoolean2 = new boolean[i];
    int j = identifyEmbeddedTracks(i, paramList1, arrayOfInt, arrayOfBoolean1, arrayOfBoolean2) + i + paramList2.size();
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[j];
    TrackGroupInfo[] arrayOfTrackGroupInfo = new TrackGroupInfo[j];
    buildManifestEventTrackGroupInfos(paramList2, arrayOfTrackGroup, arrayOfTrackGroupInfo, buildPrimaryAndEmbeddedTrackGroupInfos(paramList1, arrayOfInt, i, arrayOfBoolean1, arrayOfBoolean2, arrayOfTrackGroup, arrayOfTrackGroupInfo));
    return Pair.create(new TrackGroupArray(arrayOfTrackGroup), arrayOfTrackGroupInfo);
  }
  
  private static Descriptor findAdaptationSetSwitchingProperty(List paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      Descriptor localDescriptor = (Descriptor)paramList.get(i);
      if ("urn:mpeg:dash:adaptation-set-switching:2016".equals(schemeIdUri)) {
        return localDescriptor;
      }
      i += 1;
    }
    return null;
  }
  
  private static int[][] getGroupedAdaptationSetIndices(List paramList)
  {
    int m = paramList.size();
    SparseIntArray localSparseIntArray = new SparseIntArray(m);
    int i = 0;
    while (i < m)
    {
      localSparseIntArray.put(getnodeId, i);
      i += 1;
    }
    int[][] arrayOfInt = new int[m][];
    boolean[] arrayOfBoolean = new boolean[m];
    int j = 0;
    i = 0;
    while (j < m)
    {
      if (arrayOfBoolean[j] == 0)
      {
        arrayOfBoolean[j] = true;
        Object localObject = findAdaptationSetSwitchingProperty(getsupplementalProperties);
        if (localObject == null)
        {
          arrayOfInt[i] = { j };
          i += 1;
        }
        else
        {
          localObject = value.split(",");
          int[] arrayOfInt1 = new int[localObject.length + 1];
          arrayOfInt1[0] = j;
          int k = 0;
          while (k < localObject.length)
          {
            int n = localSparseIntArray.get(Integer.parseInt(localObject[k]));
            arrayOfBoolean[n] = true;
            k += 1;
            arrayOfInt1[k] = n;
          }
          arrayOfInt[i] = arrayOfInt1;
          i += 1;
        }
      }
      j += 1;
    }
    paramList = arrayOfInt;
    if (i < m) {
      paramList = (int[][])Arrays.copyOf(arrayOfInt, i);
    }
    return paramList;
  }
  
  private int getPrimaryStreamIndex(int paramInt, int[] paramArrayOfInt)
  {
    paramInt = paramArrayOfInt[paramInt];
    if (paramInt == -1) {
      return -1;
    }
    int i = trackGroupInfos[paramInt].primaryTrackGroupIndex;
    paramInt = 0;
    while (paramInt < paramArrayOfInt.length)
    {
      int j = paramArrayOfInt[paramInt];
      if ((j == i) && (trackGroupInfos[j].trackGroupCategory == 0)) {
        return paramInt;
      }
      paramInt += 1;
    }
    return -1;
  }
  
  private int[] getStreamIndexToTrackGroupIndex(TrackSelection[] paramArrayOfTrackSelection)
  {
    int[] arrayOfInt = new int[paramArrayOfTrackSelection.length];
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if (paramArrayOfTrackSelection[i] != null) {
        arrayOfInt[i] = trackGroups.indexOf(paramArrayOfTrackSelection[i].getTrackGroup());
      } else {
        arrayOfInt[i] = -1;
      }
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static boolean hasCea608Track(List paramList, int[] paramArrayOfInt)
  {
    int k = paramArrayOfInt.length;
    int i = 0;
    while (i < k)
    {
      List localList = getaccessibilityDescriptors;
      int j = 0;
      while (j < localList.size())
      {
        if ("urn:scte:dash:cc:cea-608:2015".equals(getschemeIdUri)) {
          return true;
        }
        j += 1;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean hasEventMessageTrack(List paramList, int[] paramArrayOfInt)
  {
    int k = paramArrayOfInt.length;
    int i = 0;
    while (i < k)
    {
      List localList = getrepresentations;
      int j = 0;
      while (j < localList.size())
      {
        if (!getinbandEventStreams.isEmpty()) {
          return true;
        }
        j += 1;
      }
      i += 1;
    }
    return false;
  }
  
  private static int identifyEmbeddedTracks(int paramInt, List paramList, int[][] paramArrayOfInt, boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    int k = 0;
    int i = 0;
    while (k < paramInt)
    {
      int j = i;
      if (hasEventMessageTrack(paramList, paramArrayOfInt[k]))
      {
        paramArrayOfBoolean1[k] = true;
        j = i + 1;
      }
      i = j;
      if (hasCea608Track(paramList, paramArrayOfInt[k]))
      {
        paramArrayOfBoolean2[k] = true;
        i = j + 1;
      }
      k += 1;
    }
    return i;
  }
  
  private static ChunkSampleStream[] newSampleStreamArray(int paramInt)
  {
    return new ChunkSampleStream[paramInt];
  }
  
  private void releaseDisabledStreams(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean, SampleStream[] paramArrayOfSampleStream)
  {
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean[i] == 0))
      {
        if ((paramArrayOfSampleStream[i] instanceof ChunkSampleStream)) {
          ((ChunkSampleStream)paramArrayOfSampleStream[i]).release(this);
        } else if ((paramArrayOfSampleStream[i] instanceof ChunkSampleStream.EmbeddedSampleStream)) {
          ((ChunkSampleStream.EmbeddedSampleStream)paramArrayOfSampleStream[i]).release();
        }
        paramArrayOfSampleStream[i] = null;
      }
      i += 1;
    }
  }
  
  private void releaseOrphanEmbeddedStreams(TrackSelection[] paramArrayOfTrackSelection, SampleStream[] paramArrayOfSampleStream, int[] paramArrayOfInt)
  {
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if (((paramArrayOfSampleStream[i] instanceof EmptySampleStream)) || ((paramArrayOfSampleStream[i] instanceof ChunkSampleStream.EmbeddedSampleStream)))
      {
        int j = getPrimaryStreamIndex(i, paramArrayOfInt);
        boolean bool;
        if (j == -1) {
          bool = paramArrayOfSampleStream[i] instanceof EmptySampleStream;
        } else if (((paramArrayOfSampleStream[i] instanceof ChunkSampleStream.EmbeddedSampleStream)) && (parent == paramArrayOfSampleStream[j])) {
          bool = true;
        } else {
          bool = false;
        }
        if (!bool)
        {
          if ((paramArrayOfSampleStream[i] instanceof ChunkSampleStream.EmbeddedSampleStream)) {
            ((ChunkSampleStream.EmbeddedSampleStream)paramArrayOfSampleStream[i]).release();
          }
          paramArrayOfSampleStream[i] = null;
        }
      }
      i += 1;
    }
  }
  
  private void selectNewStreams(TrackSelection[] paramArrayOfTrackSelection, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean, long paramLong, int[] paramArrayOfInt)
  {
    int k = 0;
    int j = 0;
    int i;
    for (;;)
    {
      i = k;
      if (j >= paramArrayOfTrackSelection.length) {
        break;
      }
      if ((paramArrayOfSampleStream[j] == null) && (paramArrayOfTrackSelection[j] != null))
      {
        paramArrayOfBoolean[j] = true;
        i = paramArrayOfInt[j];
        TrackGroupInfo localTrackGroupInfo = trackGroupInfos[i];
        if (trackGroupCategory == 0) {
          paramArrayOfSampleStream[j] = buildSampleStream(localTrackGroupInfo, paramArrayOfTrackSelection[j], paramLong);
        } else if (trackGroupCategory == 2) {
          paramArrayOfSampleStream[j] = new EventSampleStream((EventStream)eventStreams.get(eventStreamGroupIndex), paramArrayOfTrackSelection[j].getTrackGroup().getFormat(0), manifest.dynamic);
        }
      }
      j += 1;
    }
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] == null) && (paramArrayOfTrackSelection[i] != null))
      {
        j = paramArrayOfInt[i];
        paramArrayOfBoolean = trackGroupInfos[j];
        if (trackGroupCategory == 1)
        {
          j = getPrimaryStreamIndex(i, paramArrayOfInt);
          if (j == -1) {
            paramArrayOfSampleStream[i] = new EmptySampleStream();
          } else {
            paramArrayOfSampleStream[i] = ((ChunkSampleStream)paramArrayOfSampleStream[j]).selectEmbeddedTrack(paramLong, trackType);
          }
        }
      }
      i += 1;
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    return compositeSequenceableLoader.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      arrayOfChunkSampleStream[i].discardBuffer(paramLong, paramBoolean);
      i += 1;
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      ChunkSampleStream localChunkSampleStream = arrayOfChunkSampleStream[i];
      if (primaryTrackType == 2) {
        return localChunkSampleStream.getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
      }
      i += 1;
    }
    return paramLong;
  }
  
  public long getBufferedPositionUs()
  {
    return compositeSequenceableLoader.getBufferedPositionUs();
  }
  
  public long getNextLoadPositionUs()
  {
    return compositeSequenceableLoader.getNextLoadPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return trackGroups;
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    manifestLoaderErrorThrower.maybeThrowError();
  }
  
  public void onContinueLoadingRequested(ChunkSampleStream paramChunkSampleStream)
  {
    callback.onContinueLoadingRequested(this);
  }
  
  public void onSampleStreamReleased(ChunkSampleStream paramChunkSampleStream)
  {
    try
    {
      paramChunkSampleStream = (PlayerEmsgHandler.PlayerTrackEmsgHandler)trackEmsgHandlerBySampleStream.remove(paramChunkSampleStream);
      if (paramChunkSampleStream != null) {
        paramChunkSampleStream.release();
      }
      return;
    }
    catch (Throwable paramChunkSampleStream)
    {
      throw paramChunkSampleStream;
    }
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    callback = paramCallback;
    paramCallback.onPrepared(this);
  }
  
  public long readDiscontinuity()
  {
    if (!notifiedReadingStarted)
    {
      eventDispatcher.readingStarted();
      notifiedReadingStarted = true;
    }
    return -9223372036854775807L;
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    compositeSequenceableLoader.reevaluateBuffer(paramLong);
  }
  
  public void release()
  {
    playerEmsgHandler.release();
    ChunkSampleStream[] arrayOfChunkSampleStream = sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      arrayOfChunkSampleStream[i].release(this);
      i += 1;
    }
    callback = null;
    eventDispatcher.mediaPeriodReleased();
  }
  
  public long seekToUs(long paramLong)
  {
    Object localObject = sampleStreams;
    int k = localObject.length;
    int j = 0;
    int i = 0;
    while (i < k)
    {
      localObject[i].seekToUs(paramLong);
      i += 1;
    }
    localObject = eventSampleStreams;
    k = localObject.length;
    i = j;
    while (i < k)
    {
      localObject[i].seekToUs(paramLong);
      i += 1;
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int[] arrayOfInt = getStreamIndexToTrackGroupIndex(paramArrayOfTrackSelection);
    releaseDisabledStreams(paramArrayOfTrackSelection, paramArrayOfBoolean1, paramArrayOfSampleStream);
    releaseOrphanEmbeddedStreams(paramArrayOfTrackSelection, paramArrayOfSampleStream, arrayOfInt);
    selectNewStreams(paramArrayOfTrackSelection, paramArrayOfSampleStream, paramArrayOfBoolean2, paramLong, arrayOfInt);
    paramArrayOfTrackSelection = new ArrayList();
    paramArrayOfBoolean1 = new ArrayList();
    int j = paramArrayOfSampleStream.length;
    int i = 0;
    while (i < j)
    {
      paramArrayOfBoolean2 = paramArrayOfSampleStream[i];
      if ((paramArrayOfBoolean2 instanceof ChunkSampleStream)) {
        paramArrayOfTrackSelection.add((ChunkSampleStream)paramArrayOfBoolean2);
      } else if ((paramArrayOfBoolean2 instanceof EventSampleStream)) {
        paramArrayOfBoolean1.add((EventSampleStream)paramArrayOfBoolean2);
      }
      i += 1;
    }
    sampleStreams = newSampleStreamArray(paramArrayOfTrackSelection.size());
    paramArrayOfTrackSelection.toArray(sampleStreams);
    eventSampleStreams = new EventSampleStream[paramArrayOfBoolean1.size()];
    paramArrayOfBoolean1.toArray(eventSampleStreams);
    compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(sampleStreams);
    return paramLong;
  }
  
  public void updateManifest(DashManifest paramDashManifest, int paramInt)
  {
    manifest = paramDashManifest;
    periodIndex = paramInt;
    playerEmsgHandler.updateManifest(paramDashManifest);
    if (sampleStreams != null)
    {
      localObject1 = sampleStreams;
      j = localObject1.length;
      i = 0;
      while (i < j)
      {
        ((DashChunkSource)localObject1[i].getChunkSource()).updateManifest(paramDashManifest, paramInt);
        i += 1;
      }
      callback.onContinueLoadingRequested(this);
    }
    eventStreams = getPeriodeventStreams;
    Object localObject1 = eventSampleStreams;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      Iterator localIterator = eventStreams.iterator();
      while (localIterator.hasNext())
      {
        EventStream localEventStream = (EventStream)localIterator.next();
        if (localEventStream.resolve().equals(localObject2.eventStreamId()))
        {
          int k = paramDashManifest.getPeriodCount();
          boolean bool = true;
          if ((!dynamic) || (paramInt != k - 1)) {
            bool = false;
          }
          localObject2.updateEventStream(localEventStream, bool);
        }
      }
      i += 1;
    }
  }
  
  private static final class TrackGroupInfo
  {
    private static final int CATEGORY_EMBEDDED = 1;
    private static final int CATEGORY_MANIFEST_EVENTS = 2;
    private static final int CATEGORY_PRIMARY = 0;
    public final int[] adaptationSetIndices;
    public final int embeddedCea608TrackGroupIndex;
    public final int embeddedEventMessageTrackGroupIndex;
    public final int eventStreamGroupIndex;
    public final int primaryTrackGroupIndex;
    public final int trackGroupCategory;
    public final int trackType;
    
    private TrackGroupInfo(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      trackType = paramInt1;
      adaptationSetIndices = paramArrayOfInt;
      trackGroupCategory = paramInt2;
      primaryTrackGroupIndex = paramInt3;
      embeddedEventMessageTrackGroupIndex = paramInt4;
      embeddedCea608TrackGroupIndex = paramInt5;
      eventStreamGroupIndex = paramInt6;
    }
    
    public static TrackGroupInfo embeddedCea608Track(int[] paramArrayOfInt, int paramInt)
    {
      return new TrackGroupInfo(3, 1, paramArrayOfInt, paramInt, -1, -1, -1);
    }
    
    public static TrackGroupInfo embeddedEmsgTrack(int[] paramArrayOfInt, int paramInt)
    {
      return new TrackGroupInfo(4, 1, paramArrayOfInt, paramInt, -1, -1, -1);
    }
    
    public static TrackGroupInfo mpdEventTrack(int paramInt)
    {
      return new TrackGroupInfo(4, 2, null, -1, -1, -1, paramInt);
    }
    
    public static TrackGroupInfo primaryTrack(int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3, int paramInt4)
    {
      return new TrackGroupInfo(paramInt1, 0, paramArrayOfInt, paramInt2, paramInt3, paramInt4, -1);
    }
    
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public static @interface TrackGroupCategory {}
  }
}
