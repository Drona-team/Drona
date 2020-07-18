package com.google.android.exoplayer2.source.smoothstreaming;

import android.util.Base64;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.extractor.ClickListeners.TrackEncryptionBox;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.ProtectionElement;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.IOException;
import java.util.ArrayList;

final class SsMediaPeriod
  implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<SsChunkSource>>
{
  private static final int INITIALIZATION_VECTOR_SIZE = 8;
  private final Allocator allocator;
  @Nullable
  private MediaPeriod.Callback callback;
  private final SsChunkSource.Factory chunkSourceFactory;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  private SsManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private boolean notifiedReadingStarted;
  private ChunkSampleStream<SsChunkSource>[] sampleStreams;
  private final TrackEncryptionBox[] trackEncryptionBoxes;
  private final TrackGroupArray trackGroups;
  @Nullable
  private final TransferListener transferListener;
  
  public SsMediaPeriod(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, TransferListener paramTransferListener, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher paramEventDispatcher, LoaderErrorThrower paramLoaderErrorThrower, Allocator paramAllocator)
  {
    chunkSourceFactory = paramFactory;
    transferListener = paramTransferListener;
    manifestLoaderErrorThrower = paramLoaderErrorThrower;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    eventDispatcher = paramEventDispatcher;
    allocator = paramAllocator;
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    trackGroups = buildTrackGroups(paramSsManifest);
    paramFactory = protectionElement;
    if (paramFactory != null) {
      trackEncryptionBoxes = new TrackEncryptionBox[] { new TrackEncryptionBox(true, null, 8, getProtectionElementKeyId(data), 0, 0, null) };
    } else {
      trackEncryptionBoxes = null;
    }
    manifest = paramSsManifest;
    sampleStreams = newSampleStreamArray(0);
    compositeSequenceableLoader = paramCompositeSequenceableLoaderFactory.createCompositeSequenceableLoader(sampleStreams);
    paramEventDispatcher.mediaPeriodCreated();
  }
  
  private ChunkSampleStream buildSampleStream(TrackSelection paramTrackSelection, long paramLong)
  {
    int i = trackGroups.indexOf(paramTrackSelection.getTrackGroup());
    paramTrackSelection = chunkSourceFactory.createChunkSource(manifestLoaderErrorThrower, manifest, i, paramTrackSelection, trackEncryptionBoxes, transferListener);
    return new ChunkSampleStream(manifest.streamElements[i].type, null, null, paramTrackSelection, this, allocator, paramLong, loadErrorHandlingPolicy, eventDispatcher);
  }
  
  private static TrackGroupArray buildTrackGroups(SsManifest paramSsManifest)
  {
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[streamElements.length];
    int i = 0;
    while (i < streamElements.length)
    {
      arrayOfTrackGroup[i] = new TrackGroup(streamElements[i].formats);
      i += 1;
    }
    return new TrackGroupArray(arrayOfTrackGroup);
  }
  
  private static byte[] getProtectionElementKeyId(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      localStringBuilder.append((char)paramArrayOfByte[i]);
      i += 2;
    }
    paramArrayOfByte = localStringBuilder.toString();
    paramArrayOfByte = Base64.decode(paramArrayOfByte.substring(paramArrayOfByte.indexOf("<KID>") + 5, paramArrayOfByte.indexOf("</KID>")), 0);
    swap(paramArrayOfByte, 0, 3);
    swap(paramArrayOfByte, 1, 2);
    swap(paramArrayOfByte, 4, 5);
    swap(paramArrayOfByte, 6, 7);
    return paramArrayOfByte;
  }
  
  private static ChunkSampleStream[] newSampleStreamArray(int paramInt)
  {
    return new ChunkSampleStream[paramInt];
  }
  
  private static void swap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfByte[paramInt1];
    paramArrayOfByte[paramInt1] = paramArrayOfByte[paramInt2];
    paramArrayOfByte[paramInt2] = i;
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
    ChunkSampleStream[] arrayOfChunkSampleStream = sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      arrayOfChunkSampleStream[i].release();
      i += 1;
    }
    callback = null;
    eventDispatcher.mediaPeriodReleased();
  }
  
  public long seekToUs(long paramLong)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      arrayOfChunkSampleStream[i].seekToUs(paramLong);
      i += 1;
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      ChunkSampleStream localChunkSampleStream;
      if (paramArrayOfSampleStream[i] != null)
      {
        localChunkSampleStream = (ChunkSampleStream)paramArrayOfSampleStream[i];
        if ((paramArrayOfTrackSelection[i] != null) && (paramArrayOfBoolean1[i] != 0))
        {
          localArrayList.add(localChunkSampleStream);
        }
        else
        {
          localChunkSampleStream.release();
          paramArrayOfSampleStream[i] = null;
        }
      }
      if ((paramArrayOfSampleStream[i] == null) && (paramArrayOfTrackSelection[i] != null))
      {
        localChunkSampleStream = buildSampleStream(paramArrayOfTrackSelection[i], paramLong);
        localArrayList.add(localChunkSampleStream);
        paramArrayOfSampleStream[i] = localChunkSampleStream;
        paramArrayOfBoolean2[i] = true;
      }
      i += 1;
    }
    sampleStreams = newSampleStreamArray(localArrayList.size());
    localArrayList.toArray(sampleStreams);
    compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(sampleStreams);
    return paramLong;
  }
  
  public void updateManifest(SsManifest paramSsManifest)
  {
    manifest = paramSsManifest;
    ChunkSampleStream[] arrayOfChunkSampleStream = sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      ((SsChunkSource)arrayOfChunkSampleStream[i].getChunkSource()).updateManifest(paramSsManifest);
      i += 1;
    }
    callback.onContinueLoadingRequested(this);
  }
}
