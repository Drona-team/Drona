package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;

final class MergingMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private MediaPeriod.Callback callback;
  private final ArrayList<MediaPeriod> childrenPendingPreparation;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private MediaPeriod[] enabledPeriods;
  public final MediaPeriod[] periods;
  private final IdentityHashMap<SampleStream, Integer> streamPeriodIndices;
  private TrackGroupArray trackGroups;
  
  public MergingMediaPeriod(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, MediaPeriod... paramVarArgs)
  {
    compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    periods = paramVarArgs;
    childrenPendingPreparation = new ArrayList();
    compositeSequenceableLoader = paramCompositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
    streamPeriodIndices = new IdentityHashMap();
  }
  
  public boolean continueLoading(long paramLong)
  {
    if (!childrenPendingPreparation.isEmpty())
    {
      int j = childrenPendingPreparation.size();
      int i = 0;
      while (i < j)
      {
        ((MediaPeriod)childrenPendingPreparation.get(i)).continueLoading(paramLong);
        i += 1;
      }
      return false;
    }
    return compositeSequenceableLoader.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    MediaPeriod[] arrayOfMediaPeriod = enabledPeriods;
    int j = arrayOfMediaPeriod.length;
    int i = 0;
    while (i < j)
    {
      arrayOfMediaPeriod[i].discardBuffer(paramLong, paramBoolean);
      i += 1;
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    return enabledPeriods[0].getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
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
    MediaPeriod[] arrayOfMediaPeriod = periods;
    int j = arrayOfMediaPeriod.length;
    int i = 0;
    while (i < j)
    {
      arrayOfMediaPeriod[i].maybeThrowPrepareError();
      i += 1;
    }
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    callback.onContinueLoadingRequested(this);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    childrenPendingPreparation.remove(paramMediaPeriod);
    if (!childrenPendingPreparation.isEmpty()) {
      return;
    }
    paramMediaPeriod = periods;
    int k = paramMediaPeriod.length;
    int i = 0;
    int j = 0;
    while (i < k)
    {
      j += getTrackGroupslength;
      i += 1;
    }
    paramMediaPeriod = new TrackGroup[j];
    MediaPeriod[] arrayOfMediaPeriod = periods;
    int m = arrayOfMediaPeriod.length;
    j = 0;
    i = 0;
    while (j < m)
    {
      TrackGroupArray localTrackGroupArray = arrayOfMediaPeriod[j].getTrackGroups();
      int n = length;
      k = 0;
      while (k < n)
      {
        paramMediaPeriod[i] = localTrackGroupArray.context(k);
        k += 1;
        i += 1;
      }
      j += 1;
    }
    trackGroups = new TrackGroupArray(paramMediaPeriod);
    callback.onPrepared(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    callback = paramCallback;
    Collections.addAll(childrenPendingPreparation, periods);
    paramCallback = periods;
    int j = paramCallback.length;
    int i = 0;
    while (i < j)
    {
      paramCallback[i].prepare(this, paramLong);
      i += 1;
    }
  }
  
  public long readDiscontinuity()
  {
    MediaPeriod[] arrayOfMediaPeriod = periods;
    MergingMediaPeriod localMergingMediaPeriod = this;
    long l = arrayOfMediaPeriod[0].readDiscontinuity();
    int i = 1;
    while (i < periods.length)
    {
      arrayOfMediaPeriod = periods;
      if (arrayOfMediaPeriod[i].readDiscontinuity() == -9223372036854775807L) {
        i += 1;
      } else {
        throw new IllegalStateException("Child reported discontinuity.");
      }
    }
    if (l != -9223372036854775807L)
    {
      arrayOfMediaPeriod = enabledPeriods;
      int j = arrayOfMediaPeriod.length;
      i = 0;
      while (i < j)
      {
        MediaPeriod localMediaPeriod = arrayOfMediaPeriod[i];
        if ((localMediaPeriod != periods[0]) && (localMediaPeriod.seekToUs(l) != l)) {
          throw new IllegalStateException("Unexpected child seekToUs result.");
        }
        i += 1;
      }
    }
    return l;
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    compositeSequenceableLoader.reevaluateBuffer(paramLong);
  }
  
  public long seekToUs(long paramLong)
  {
    paramLong = enabledPeriods[0].seekToUs(paramLong);
    int i = 1;
    while (i < enabledPeriods.length) {
      if (enabledPeriods[i].seekToUs(paramLong) == paramLong) {
        i += 1;
      } else {
        throw new IllegalStateException("Unexpected child seekToUs result.");
      }
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int[] arrayOfInt1 = new int[paramArrayOfTrackSelection.length];
    int[] arrayOfInt2 = new int[paramArrayOfTrackSelection.length];
    int i = 0;
    int j;
    while (i < paramArrayOfTrackSelection.length)
    {
      if (paramArrayOfSampleStream[i] == null) {
        j = -1;
      } else {
        j = ((Integer)streamPeriodIndices.get(paramArrayOfSampleStream[i])).intValue();
      }
      arrayOfInt1[i] = j;
      arrayOfInt2[i] = -1;
      if (paramArrayOfTrackSelection[i] != null)
      {
        localObject1 = paramArrayOfTrackSelection[i].getTrackGroup();
        j = 0;
        while (j < periods.length)
        {
          if (periods[j].getTrackGroups().indexOf((TrackGroup)localObject1) != -1)
          {
            arrayOfInt2[i] = j;
            break;
          }
          j += 1;
        }
      }
      i += 1;
    }
    streamPeriodIndices.clear();
    SampleStream[] arrayOfSampleStream1 = new SampleStream[paramArrayOfTrackSelection.length];
    SampleStream[] arrayOfSampleStream2 = new SampleStream[paramArrayOfTrackSelection.length];
    Object localObject1 = new TrackSelection[paramArrayOfTrackSelection.length];
    ArrayList localArrayList = new ArrayList(periods.length);
    i = 0;
    while (i < periods.length)
    {
      j = 0;
      while (j < paramArrayOfTrackSelection.length)
      {
        k = arrayOfInt1[j];
        Object localObject3 = null;
        if (k == i) {
          localObject2 = paramArrayOfSampleStream[j];
        } else {
          localObject2 = null;
        }
        arrayOfSampleStream2[j] = localObject2;
        Object localObject2 = localObject3;
        if (arrayOfInt2[j] == i) {
          localObject2 = paramArrayOfTrackSelection[j];
        }
        localObject1[j] = localObject2;
        j += 1;
      }
      long l = periods[i].selectTracks((TrackSelection[])localObject1, paramArrayOfBoolean1, arrayOfSampleStream2, paramArrayOfBoolean2, paramLong);
      if (i == 0) {
        paramLong = l;
      } else {
        if (l != paramLong) {
          break label490;
        }
      }
      j = 0;
      int m;
      for (int k = 0; j < paramArrayOfTrackSelection.length; k = m)
      {
        m = arrayOfInt2[j];
        boolean bool = true;
        if (m == i)
        {
          if (arrayOfSampleStream2[j] != null) {
            bool = true;
          } else {
            bool = false;
          }
          Assertions.checkState(bool);
          arrayOfSampleStream1[j] = arrayOfSampleStream2[j];
          streamPeriodIndices.put(arrayOfSampleStream2[j], Integer.valueOf(i));
          m = 1;
        }
        else
        {
          m = k;
          if (arrayOfInt1[j] == i)
          {
            if (arrayOfSampleStream2[j] != null) {
              bool = false;
            }
            Assertions.checkState(bool);
            m = k;
          }
        }
        j += 1;
      }
      if (k != 0) {
        localArrayList.add(periods[i]);
      }
      i += 1;
      continue;
      label490:
      throw new IllegalStateException("Children enabled at different positions.");
    }
    System.arraycopy(arrayOfSampleStream1, 0, paramArrayOfSampleStream, 0, arrayOfSampleStream1.length);
    enabledPeriods = new MediaPeriod[localArrayList.size()];
    localArrayList.toArray(enabledPeriods);
    compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(enabledPeriods);
    return paramLong;
  }
}
