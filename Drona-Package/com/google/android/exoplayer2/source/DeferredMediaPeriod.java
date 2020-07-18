package com.google.android.exoplayer2.source;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import java.io.IOException;

public final class DeferredMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  @Nullable
  private PrepareErrorListener listener;
  private MediaPeriod mediaPeriod;
  public final MediaSource mediaSource;
  public final MediaSource.MediaPeriodId move;
  private boolean notifiedPrepareError;
  private long preparePositionOverrideUs;
  private long preparePositionUs;
  
  public DeferredMediaPeriod(MediaSource paramMediaSource, MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    move = paramMediaPeriodId;
    allocator = paramAllocator;
    mediaSource = paramMediaSource;
    preparePositionOverrideUs = -9223372036854775807L;
  }
  
  public boolean continueLoading(long paramLong)
  {
    return (mediaPeriod != null) && (mediaPeriod.continueLoading(paramLong));
  }
  
  public void createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    mediaPeriod = mediaSource.createPeriod(paramMediaPeriodId, allocator);
    if (callback != null)
    {
      long l;
      if (preparePositionOverrideUs != -9223372036854775807L) {
        l = preparePositionOverrideUs;
      } else {
        l = preparePositionUs;
      }
      mediaPeriod.prepare(this, l);
    }
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    mediaPeriod.discardBuffer(paramLong, paramBoolean);
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    return mediaPeriod.getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
  }
  
  public long getBufferedPositionUs()
  {
    return mediaPeriod.getBufferedPositionUs();
  }
  
  public long getNextLoadPositionUs()
  {
    return mediaPeriod.getNextLoadPositionUs();
  }
  
  public long getPreparePositionUs()
  {
    return preparePositionUs;
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return mediaPeriod.getTrackGroups();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    if (mediaPeriod != null) {
      localObject = mediaPeriod;
    }
    try
    {
      ((MediaPeriod)localObject).maybeThrowPrepareError();
      return;
    }
    catch (IOException localIOException)
    {
      if (listener == null) {
        break label66;
      }
      if (notifiedPrepareError) {
        return;
      }
      notifiedPrepareError = true;
      listener.onPrepareError(move, localIOException);
      return;
      throw localIOException;
    }
    Object localObject = mediaSource;
    ((MediaSource)localObject).maybeThrowSourceInfoRefreshError();
    return;
    label66:
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    callback.onContinueLoadingRequested(this);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    callback.onPrepared(this);
  }
  
  public void overridePreparePositionUs(long paramLong)
  {
    preparePositionOverrideUs = paramLong;
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    callback = paramCallback;
    preparePositionUs = paramLong;
    if (mediaPeriod != null) {
      mediaPeriod.prepare(this, paramLong);
    }
  }
  
  public long readDiscontinuity()
  {
    return mediaPeriod.readDiscontinuity();
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    mediaPeriod.reevaluateBuffer(paramLong);
  }
  
  public void releasePeriod()
  {
    if (mediaPeriod != null) {
      mediaSource.releasePeriod(mediaPeriod);
    }
  }
  
  public long seekToUs(long paramLong)
  {
    return mediaPeriod.seekToUs(paramLong);
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    long l = paramLong;
    if (preparePositionOverrideUs != -9223372036854775807L)
    {
      l = paramLong;
      if (paramLong == preparePositionUs)
      {
        l = preparePositionOverrideUs;
        preparePositionOverrideUs = -9223372036854775807L;
      }
    }
    return mediaPeriod.selectTracks(paramArrayOfTrackSelection, paramArrayOfBoolean1, paramArrayOfSampleStream, paramArrayOfBoolean2, l);
  }
  
  public void setPrepareErrorListener(PrepareErrorListener paramPrepareErrorListener)
  {
    listener = paramPrepareErrorListener;
  }
  
  public static abstract interface PrepareErrorListener
  {
    public abstract void onPrepareError(MediaSource.MediaPeriodId paramMediaPeriodId, IOException paramIOException);
  }
}