package com.google.android.exoplayer2.source.configurations;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistTracker.PlaylistEventListener;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public final class HlsMediaPeriod
  implements MediaPeriod, HlsSampleStreamWrapper.Callback, HlsPlaylistTracker.PlaylistEventListener
{
  private final Allocator allocator;
  private final boolean allowChunklessPreparation;
  @Nullable
  private MediaPeriod.Callback callback;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final HlsDataSourceFactory dataSourceFactory;
  private HlsSampleStreamWrapper[] enabledSampleStreamWrappers;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final HlsExtractorFactory extractorFactory;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  @Nullable
  private final TransferListener mediaTransferListener;
  private boolean notifiedReadingStarted;
  private int pendingPrepareCount;
  private final HlsPlaylistTracker playlistTracker;
  private HlsSampleStreamWrapper[] sampleStreamWrappers;
  private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private TrackGroupArray trackGroups;
  
  public HlsMediaPeriod(HlsExtractorFactory paramHlsExtractorFactory, HlsPlaylistTracker paramHlsPlaylistTracker, HlsDataSourceFactory paramHlsDataSourceFactory, TransferListener paramTransferListener, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher paramEventDispatcher, Allocator paramAllocator, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, boolean paramBoolean)
  {
    extractorFactory = paramHlsExtractorFactory;
    playlistTracker = paramHlsPlaylistTracker;
    dataSourceFactory = paramHlsDataSourceFactory;
    mediaTransferListener = paramTransferListener;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    eventDispatcher = paramEventDispatcher;
    allocator = paramAllocator;
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    allowChunklessPreparation = paramBoolean;
    compositeSequenceableLoader = paramCompositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
    streamWrapperIndices = new IdentityHashMap();
    timestampAdjusterProvider = new TimestampAdjusterProvider();
    sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    paramEventDispatcher.mediaPeriodCreated();
  }
  
  private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist paramHlsMasterPlaylist, long paramLong)
  {
    Object localObject2 = new ArrayList(variants);
    Object localObject1 = new ArrayList();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < ((List)localObject2).size())
    {
      localObject3 = (HlsMasterPlaylist.HlsUrl)((List)localObject2).get(i);
      localObject4 = format;
      if ((height <= 0) && (Util.getCodecsOfType(codecs, 2) == null))
      {
        if (Util.getCodecsOfType(codecs, 1) != null) {
          localArrayList.add(localObject3);
        }
      }
      else {
        ((ArrayList)localObject1).add(localObject3);
      }
      i += 1;
    }
    if (((ArrayList)localObject1).isEmpty())
    {
      localObject1 = localObject2;
      if (localArrayList.size() < ((List)localObject2).size())
      {
        ((List)localObject2).removeAll(localArrayList);
        localObject1 = localObject2;
      }
    }
    Assertions.checkArgument(((List)localObject1).isEmpty() ^ true);
    Object localObject3 = (HlsMasterPlaylist.HlsUrl[])((List)localObject1).toArray(new HlsMasterPlaylist.HlsUrl[0]);
    Object localObject4 = 0format.codecs;
    localObject2 = buildSampleStreamWrapper(0, (HlsMasterPlaylist.HlsUrl[])localObject3, muxedAudioFormat, muxedCaptionFormats, paramLong);
    sampleStreamWrappers[0] = localObject2;
    if ((allowChunklessPreparation) && (localObject4 != null))
    {
      int j;
      if (Util.getCodecsOfType((String)localObject4, 2) != null) {
        j = 1;
      } else {
        j = 0;
      }
      if (Util.getCodecsOfType((String)localObject4, 1) != null) {
        i = 1;
      } else {
        i = 0;
      }
      localArrayList = new ArrayList();
      if (j != 0)
      {
        localObject1 = new Format[((List)localObject1).size()];
        j = 0;
        while (j < localObject1.length)
        {
          localObject1[j] = deriveVideoFormat(format);
          j += 1;
        }
        localArrayList.add(new TrackGroup((Format[])localObject1));
        if ((i != 0) && ((muxedAudioFormat != null) || (audios.isEmpty()))) {
          localArrayList.add(new TrackGroup(new Format[] { deriveAudioFormat(0format, muxedAudioFormat, false) }));
        }
        paramHlsMasterPlaylist = muxedCaptionFormats;
        if (paramHlsMasterPlaylist != null)
        {
          i = 0;
          while (i < paramHlsMasterPlaylist.size())
          {
            localArrayList.add(new TrackGroup(new Format[] { (Format)paramHlsMasterPlaylist.get(i) }));
            i += 1;
          }
        }
      }
      else
      {
        if (i == 0) {
          break label645;
        }
        localObject1 = new Format[((List)localObject1).size()];
        i = 0;
        while (i < localObject1.length)
        {
          localObject1[i] = deriveAudioFormat(format, muxedAudioFormat, true);
          i += 1;
        }
        localArrayList.add(new TrackGroup((Format[])localObject1));
      }
      paramHlsMasterPlaylist = new TrackGroup(new Format[] { Format.createSampleFormat("ID3", "application/id3", null, -1, null) });
      localArrayList.add(paramHlsMasterPlaylist);
      ((HlsSampleStreamWrapper)localObject2).prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[])localArrayList.toArray(new TrackGroup[0])), 0, new TrackGroupArray(new TrackGroup[] { paramHlsMasterPlaylist }));
      return;
      label645:
      paramHlsMasterPlaylist = new StringBuilder();
      paramHlsMasterPlaylist.append("Unexpected codecs attribute: ");
      paramHlsMasterPlaylist.append((String)localObject4);
      throw new IllegalArgumentException(paramHlsMasterPlaylist.toString());
    }
    ((HlsSampleStreamWrapper)localObject2).setIsTimestampMaster(true);
    ((HlsSampleStreamWrapper)localObject2).continuePreparing();
  }
  
  private void buildAndPrepareSampleStreamWrappers(long paramLong)
  {
    Object localObject2 = playlistTracker.getMasterPlaylist();
    Object localObject1 = audios;
    List localList = subtitles;
    int i = ((List)localObject1).size() + 1 + localList.size();
    sampleStreamWrappers = new HlsSampleStreamWrapper[i];
    pendingPrepareCount = i;
    buildAndPrepareMainSampleStreamWrapper((HlsMasterPlaylist)localObject2, paramLong);
    int j = 0;
    i = 1;
    while (j < ((List)localObject1).size())
    {
      localObject2 = (HlsMasterPlaylist.HlsUrl)((List)localObject1).get(j);
      Object localObject3 = Collections.emptyList();
      localObject3 = buildSampleStreamWrapper(1, new HlsMasterPlaylist.HlsUrl[] { localObject2 }, null, (List)localObject3, paramLong);
      sampleStreamWrappers[i] = localObject3;
      Format localFormat = format;
      if ((allowChunklessPreparation) && (codecs != null)) {
        ((HlsSampleStreamWrapper)localObject3).prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup[] { new TrackGroup(new Format[] { format }) }), 0, TrackGroupArray.EMPTY);
      } else {
        ((HlsSampleStreamWrapper)localObject3).continuePreparing();
      }
      j += 1;
      i += 1;
    }
    j = 0;
    while (j < localList.size())
    {
      localObject1 = (HlsMasterPlaylist.HlsUrl)localList.get(j);
      localObject2 = Collections.emptyList();
      localObject2 = buildSampleStreamWrapper(3, new HlsMasterPlaylist.HlsUrl[] { localObject1 }, null, (List)localObject2, paramLong);
      sampleStreamWrappers[i] = localObject2;
      ((HlsSampleStreamWrapper)localObject2).prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup[] { new TrackGroup(new Format[] { format }) }), 0, TrackGroupArray.EMPTY);
      j += 1;
      i += 1;
    }
    enabledSampleStreamWrappers = sampleStreamWrappers;
  }
  
  private HlsSampleStreamWrapper buildSampleStreamWrapper(int paramInt, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, Format paramFormat, List paramList, long paramLong)
  {
    return new HlsSampleStreamWrapper(paramInt, this, new HlsChunkSource(extractorFactory, playlistTracker, paramArrayOfHlsUrl, dataSourceFactory, mediaTransferListener, timestampAdjusterProvider, paramList), allocator, paramLong, paramFormat, loadErrorHandlingPolicy, eventDispatcher);
  }
  
  private static Format deriveAudioFormat(Format paramFormat1, Format paramFormat2, boolean paramBoolean)
  {
    String str2;
    int i;
    int j;
    String str1;
    if (paramFormat2 != null)
    {
      str3 = label;
      str2 = codecs;
      i = channelCount;
      j = selectionFlags;
      str1 = language;
      paramFormat2 = str3;
    }
    else
    {
      str2 = Util.getCodecsOfType(codecs, 1);
      if (paramBoolean)
      {
        i = channelCount;
        str1 = label;
        j = selectionFlags;
        paramFormat2 = label;
      }
      else
      {
        paramFormat2 = null;
        str1 = null;
        i = -1;
        j = 0;
      }
    }
    String str3 = MimeTypes.getMediaMimeType(str2);
    int k;
    if (paramBoolean) {
      k = bitrate;
    } else {
      k = -1;
    }
    return Format.createAudioContainerFormat(mimeType, paramFormat2, containerMimeType, str3, str2, k, i, -1, null, j, str1);
  }
  
  private static Format deriveVideoFormat(Format paramFormat)
  {
    String str1 = Util.getCodecsOfType(codecs, 2);
    String str2 = MimeTypes.getMediaMimeType(str1);
    return Format.createVideoContainerFormat(mimeType, label, containerMimeType, str2, str1, bitrate, width, height, frameRate, null, selectionFlags);
  }
  
  public boolean continueLoading(long paramLong)
  {
    if (trackGroups == null)
    {
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = sampleStreamWrappers;
      int j = arrayOfHlsSampleStreamWrapper.length;
      int i = 0;
      while (i < j)
      {
        arrayOfHlsSampleStreamWrapper[i].continuePreparing();
        i += 1;
      }
      return false;
    }
    return compositeSequenceableLoader.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = enabledSampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    int i = 0;
    while (i < j)
    {
      arrayOfHlsSampleStreamWrapper[i].discardBuffer(paramLong, paramBoolean);
      i += 1;
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
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
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = sampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    int i = 0;
    while (i < j)
    {
      arrayOfHlsSampleStreamWrapper[i].maybeThrowPrepareError();
      i += 1;
    }
  }
  
  public void onContinueLoadingRequested(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    callback.onContinueLoadingRequested(this);
  }
  
  public void onPlaylistChanged()
  {
    callback.onContinueLoadingRequested(this);
  }
  
  public boolean onPlaylistError(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = sampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    boolean bool = true;
    int i = 0;
    while (i < j)
    {
      bool &= arrayOfHlsSampleStreamWrapper[i].onPlaylistError(paramHlsUrl, paramLong);
      i += 1;
    }
    callback.onContinueLoadingRequested(this);
    return bool;
  }
  
  public void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    playlistTracker.refreshPlaylist(paramHlsUrl);
  }
  
  public void onPrepared()
  {
    int i = pendingPrepareCount - 1;
    pendingPrepareCount = i;
    if (i > 0) {
      return;
    }
    Object localObject = sampleStreamWrappers;
    int k = localObject.length;
    i = 0;
    int j = 0;
    while (i < k)
    {
      j += getTrackGroupslength;
      i += 1;
    }
    localObject = new TrackGroup[j];
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = sampleStreamWrappers;
    int m = arrayOfHlsSampleStreamWrapper.length;
    j = 0;
    i = 0;
    while (j < m)
    {
      HlsSampleStreamWrapper localHlsSampleStreamWrapper = arrayOfHlsSampleStreamWrapper[j];
      int n = getTrackGroupslength;
      k = 0;
      while (k < n)
      {
        localObject[i] = localHlsSampleStreamWrapper.getTrackGroups().context(k);
        k += 1;
        i += 1;
      }
      j += 1;
    }
    trackGroups = new TrackGroupArray((TrackGroup[])localObject);
    callback.onPrepared(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    callback = paramCallback;
    playlistTracker.addListener(this);
    buildAndPrepareSampleStreamWrappers(paramLong);
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
    playlistTracker.removeListener(this);
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = sampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    int i = 0;
    while (i < j)
    {
      arrayOfHlsSampleStreamWrapper[i].release();
      i += 1;
    }
    callback = null;
    eventDispatcher.mediaPeriodReleased();
  }
  
  public long seekToUs(long paramLong)
  {
    if (enabledSampleStreamWrappers.length > 0)
    {
      boolean bool = enabledSampleStreamWrappers[0].seekToUs(paramLong, false);
      int i = 1;
      while (i < enabledSampleStreamWrappers.length)
      {
        enabledSampleStreamWrappers[i].seekToUs(paramLong, bool);
        i += 1;
      }
      if (bool) {
        timestampAdjusterProvider.reset();
      }
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int[] arrayOfInt1 = new int[paramArrayOfTrackSelection.length];
    int[] arrayOfInt2 = new int[paramArrayOfTrackSelection.length];
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if (paramArrayOfSampleStream[i] == null) {
        j = -1;
      } else {
        j = ((Integer)streamWrapperIndices.get(paramArrayOfSampleStream[i])).intValue();
      }
      arrayOfInt1[i] = j;
      arrayOfInt2[i] = -1;
      if (paramArrayOfTrackSelection[i] != null)
      {
        localObject1 = paramArrayOfTrackSelection[i].getTrackGroup();
        j = 0;
        while (j < sampleStreamWrappers.length)
        {
          if (sampleStreamWrappers[j].getTrackGroups().indexOf((TrackGroup)localObject1) != -1)
          {
            arrayOfInt2[i] = j;
            break;
          }
          j += 1;
        }
      }
      i += 1;
    }
    streamWrapperIndices.clear();
    SampleStream[] arrayOfSampleStream1 = new SampleStream[paramArrayOfTrackSelection.length];
    SampleStream[] arrayOfSampleStream2 = new SampleStream[paramArrayOfTrackSelection.length];
    Object localObject1 = new TrackSelection[paramArrayOfTrackSelection.length];
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = new HlsSampleStreamWrapper[sampleStreamWrappers.length];
    int k = 0;
    int j = 0;
    boolean bool2;
    for (boolean bool1 = false; j < sampleStreamWrappers.length; bool1 = bool2)
    {
      i = 0;
      while (i < paramArrayOfTrackSelection.length)
      {
        m = arrayOfInt1[i];
        Object localObject3 = null;
        if (m == j) {
          localObject2 = paramArrayOfSampleStream[i];
        } else {
          localObject2 = null;
        }
        arrayOfSampleStream2[i] = localObject2;
        localObject2 = localObject3;
        if (arrayOfInt2[i] == j) {
          localObject2 = paramArrayOfTrackSelection[i];
        }
        localObject1[i] = localObject2;
        i += 1;
      }
      Object localObject2 = sampleStreamWrappers[j];
      boolean bool3 = ((HlsSampleStreamWrapper)localObject2).selectTracks((TrackSelection[])localObject1, paramArrayOfBoolean1, arrayOfSampleStream2, paramArrayOfBoolean2, paramLong, bool1);
      i = 0;
      int n;
      for (int m = 0;; m = n)
      {
        n = paramArrayOfTrackSelection.length;
        bool2 = true;
        if (i >= n) {
          break;
        }
        if (arrayOfInt2[i] == j)
        {
          if (arrayOfSampleStream2[i] != null) {
            bool2 = true;
          } else {
            bool2 = false;
          }
          Assertions.checkState(bool2);
          arrayOfSampleStream1[i] = arrayOfSampleStream2[i];
          streamWrapperIndices.put(arrayOfSampleStream2[i], Integer.valueOf(j));
          n = 1;
        }
        else
        {
          n = m;
          if (arrayOfInt1[i] == j)
          {
            if (arrayOfSampleStream2[i] != null) {
              bool2 = false;
            }
            Assertions.checkState(bool2);
            n = m;
          }
        }
        i += 1;
      }
      i = k;
      bool2 = bool1;
      if (m != 0)
      {
        arrayOfHlsSampleStreamWrapper[k] = localObject2;
        i = k + 1;
        if (k == 0)
        {
          ((HlsSampleStreamWrapper)localObject2).setIsTimestampMaster(true);
          if ((!bool3) && (enabledSampleStreamWrappers.length != 0)) {
            if (localObject2 == enabledSampleStreamWrappers[0]) {
              break label530;
            }
          }
          timestampAdjusterProvider.reset();
          bool2 = true;
          break label537;
        }
        else
        {
          ((HlsSampleStreamWrapper)localObject2).setIsTimestampMaster(false);
        }
        label530:
        bool2 = bool1;
      }
      label537:
      j += 1;
      k = i;
    }
    System.arraycopy(arrayOfSampleStream1, 0, paramArrayOfSampleStream, 0, arrayOfSampleStream1.length);
    enabledSampleStreamWrappers = ((HlsSampleStreamWrapper[])Arrays.copyOf(arrayOfHlsSampleStreamWrapper, k));
    compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(enabledSampleStreamWrappers);
    return paramLong;
  }
}
