package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public final class ClippingMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private MediaPeriod.Callback callback;
  long endUs;
  public final MediaPeriod mediaPeriod;
  private long pendingInitialDiscontinuityPositionUs;
  private ClippingSampleStream[] sampleStreams;
  long startUs;
  
  public ClippingMediaPeriod(MediaPeriod paramMediaPeriod, boolean paramBoolean, long paramLong1, long paramLong2)
  {
    mediaPeriod = paramMediaPeriod;
    sampleStreams = new ClippingSampleStream[0];
    long l;
    if (paramBoolean) {
      l = paramLong1;
    } else {
      l = -9223372036854775807L;
    }
    pendingInitialDiscontinuityPositionUs = l;
    startUs = paramLong1;
    endUs = paramLong2;
  }
  
  private SeekParameters clipSeekParameters(long paramLong, SeekParameters paramSeekParameters)
  {
    long l1 = Util.constrainValue(toleranceBeforeUs, 0L, paramLong - startUs);
    long l2 = toleranceAfterUs;
    if (endUs == Long.MIN_VALUE) {
      paramLong = Long.MAX_VALUE;
    } else {
      paramLong = endUs - paramLong;
    }
    paramLong = Util.constrainValue(l2, 0L, paramLong);
    if ((l1 == toleranceBeforeUs) && (paramLong == toleranceAfterUs)) {
      return paramSeekParameters;
    }
    return new SeekParameters(l1, paramLong);
  }
  
  private static boolean shouldKeepInitialDiscontinuity(long paramLong, TrackSelection[] paramArrayOfTrackSelection)
  {
    if (paramLong != 0L)
    {
      int j = paramArrayOfTrackSelection.length;
      int i = 0;
      while (i < j)
      {
        TrackSelection localTrackSelection = paramArrayOfTrackSelection[i];
        if ((localTrackSelection != null) && (!MimeTypes.isAudio(getSelectedFormatsampleMimeType))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public boolean continueLoading(long paramLong)
  {
    return mediaPeriod.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    mediaPeriod.discardBuffer(paramLong, paramBoolean);
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    if (paramLong == startUs) {
      return startUs;
    }
    paramSeekParameters = clipSeekParameters(paramLong, paramSeekParameters);
    return mediaPeriod.getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
  }
  
  public long getBufferedPositionUs()
  {
    long l = mediaPeriod.getBufferedPositionUs();
    if (l != Long.MIN_VALUE)
    {
      if (endUs != Long.MIN_VALUE)
      {
        if (l >= endUs) {
          return Long.MIN_VALUE;
        }
      }
      else {
        return l;
      }
    }
    else {
      return Long.MIN_VALUE;
    }
    return l;
  }
  
  public long getNextLoadPositionUs()
  {
    long l = mediaPeriod.getNextLoadPositionUs();
    if (l != Long.MIN_VALUE)
    {
      if (endUs != Long.MIN_VALUE)
      {
        if (l >= endUs) {
          return Long.MIN_VALUE;
        }
      }
      else {
        return l;
      }
    }
    else {
      return Long.MIN_VALUE;
    }
    return l;
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return mediaPeriod.getTrackGroups();
  }
  
  boolean isPendingInitialDiscontinuity()
  {
    return pendingInitialDiscontinuityPositionUs != -9223372036854775807L;
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    mediaPeriod.maybeThrowPrepareError();
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    callback.onContinueLoadingRequested(this);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    callback.onPrepared(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    callback = paramCallback;
    mediaPeriod.prepare(this, paramLong);
  }
  
  public long readDiscontinuity()
  {
    long l1;
    long l2;
    if (isPendingInitialDiscontinuity())
    {
      l1 = pendingInitialDiscontinuityPositionUs;
      pendingInitialDiscontinuityPositionUs = -9223372036854775807L;
      l2 = readDiscontinuity();
      if (l2 != -9223372036854775807L) {
        return l2;
      }
    }
    else
    {
      l1 = mediaPeriod.readDiscontinuity();
      if (l1 == -9223372036854775807L) {
        return -9223372036854775807L;
      }
      l2 = startUs;
      boolean bool2 = false;
      boolean bool1;
      if (l1 >= l2) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      Assertions.checkState(bool1);
      if (endUs != Long.MIN_VALUE)
      {
        bool1 = bool2;
        if (l1 > endUs) {}
      }
      else
      {
        bool1 = true;
      }
      Assertions.checkState(bool1);
      return l1;
    }
    return l1;
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    mediaPeriod.reevaluateBuffer(paramLong);
  }
  
  public long seekToUs(long paramLong)
  {
    pendingInitialDiscontinuityPositionUs = -9223372036854775807L;
    ClippingSampleStream[] arrayOfClippingSampleStream = sampleStreams;
    int j = arrayOfClippingSampleStream.length;
    boolean bool2 = false;
    int i = 0;
    while (i < j)
    {
      ClippingSampleStream localClippingSampleStream = arrayOfClippingSampleStream[i];
      if (localClippingSampleStream != null) {
        localClippingSampleStream.clearSentEos();
      }
      i += 1;
    }
    long l = mediaPeriod.seekToUs(paramLong);
    if (l != paramLong)
    {
      bool1 = bool2;
      if (l < startUs) {
        break label113;
      }
      if (endUs != Long.MIN_VALUE)
      {
        bool1 = bool2;
        if (l > endUs) {
          break label113;
        }
      }
    }
    boolean bool1 = true;
    label113:
    Assertions.checkState(bool1);
    return l;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    sampleStreams = new ClippingSampleStream[paramArrayOfSampleStream.length];
    SampleStream[] arrayOfSampleStream = new SampleStream[paramArrayOfSampleStream.length];
    int j = 0;
    int i = 0;
    for (;;)
    {
      int k = paramArrayOfSampleStream.length;
      SampleStream localSampleStream = null;
      if (i >= k) {
        break;
      }
      sampleStreams[i] = ((ClippingSampleStream)paramArrayOfSampleStream[i]);
      if (sampleStreams[i] != null) {
        localSampleStream = sampleStreams[i].childStream;
      }
      arrayOfSampleStream[i] = localSampleStream;
      i += 1;
    }
    long l2 = mediaPeriod.selectTracks(paramArrayOfTrackSelection, paramArrayOfBoolean1, arrayOfSampleStream, paramArrayOfBoolean2, paramLong);
    long l1;
    if ((isPendingInitialDiscontinuity()) && (paramLong == startUs) && (shouldKeepInitialDiscontinuity(startUs, paramArrayOfTrackSelection))) {
      l1 = l2;
    } else {
      l1 = -9223372036854775807L;
    }
    pendingInitialDiscontinuityPositionUs = l1;
    boolean bool;
    if ((l2 != paramLong) && ((l2 < startUs) || ((endUs != Long.MIN_VALUE) && (l2 > endUs)))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkState(bool);
    i = j;
    while (i < paramArrayOfSampleStream.length)
    {
      if (arrayOfSampleStream[i] == null) {
        sampleStreams[i] = null;
      } else if ((paramArrayOfSampleStream[i] == null) || (sampleStreams[i].childStream != arrayOfSampleStream[i])) {
        sampleStreams[i] = new ClippingSampleStream(arrayOfSampleStream[i]);
      }
      paramArrayOfSampleStream[i] = sampleStreams[i];
      i += 1;
    }
    return l2;
  }
  
  public void updateClipping(long paramLong1, long paramLong2)
  {
    startUs = paramLong1;
    endUs = paramLong2;
  }
  
  private final class ClippingSampleStream
    implements SampleStream
  {
    public final SampleStream childStream;
    private boolean sentEos;
    
    public ClippingSampleStream(SampleStream paramSampleStream)
    {
      childStream = paramSampleStream;
    }
    
    public void clearSentEos()
    {
      sentEos = false;
    }
    
    public boolean isReady()
    {
      return (!isPendingInitialDiscontinuity()) && (childStream.isReady());
    }
    
    public void maybeThrowError()
      throws IOException
    {
      childStream.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
    {
      if (isPendingInitialDiscontinuity()) {
        return -3;
      }
      if (sentEos)
      {
        paramDecoderInputBuffer.setFlags(4);
        return -4;
      }
      int i = childStream.readData(paramFormatHolder, paramDecoderInputBuffer, paramBoolean);
      if (i == -5)
      {
        paramDecoderInputBuffer = format;
        if ((encoderDelay != 0) || (encoderPadding != 0))
        {
          long l = startUs;
          int j = 0;
          if (l != 0L) {
            i = 0;
          } else {
            i = encoderDelay;
          }
          if (endUs == Long.MIN_VALUE) {
            j = encoderPadding;
          }
          format = paramDecoderInputBuffer.copyWithGaplessInfo(i, j);
          return -5;
        }
      }
      else
      {
        if (endUs != Long.MIN_VALUE)
        {
          if (((i != -4) || (timeUs < endUs)) && ((i != -3) || (getBufferedPositionUs() != Long.MIN_VALUE))) {
            break label217;
          }
          paramDecoderInputBuffer.clear();
          paramDecoderInputBuffer.setFlags(4);
          sentEos = true;
          return -4;
        }
        return i;
      }
      return -5;
      label217:
      return i;
    }
    
    public int skipData(long paramLong)
    {
      if (isPendingInitialDiscontinuity()) {
        return -3;
      }
      return childStream.skipData(paramLong);
    }
  }
}
