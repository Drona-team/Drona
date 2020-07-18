package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MediaClock;
import java.io.IOException;

public abstract class NoSampleRenderer
  implements Renderer, RendererCapabilities
{
  private RendererConfiguration configuration;
  private int index;
  private int state;
  private SampleStream stream;
  private boolean streamIsFinal;
  
  public NoSampleRenderer() {}
  
  public final void disable()
  {
    int i = state;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    Assertions.checkState(bool);
    state = 0;
    stream = null;
    streamIsFinal = false;
    onDisabled();
  }
  
  public final void enable(RendererConfiguration paramRendererConfiguration, Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong1, boolean paramBoolean, long paramLong2)
    throws ExoPlaybackException
  {
    boolean bool;
    if (state == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    configuration = paramRendererConfiguration;
    state = 1;
    onEnabled(paramBoolean);
    replaceStream(paramArrayOfFormat, paramSampleStream, paramLong2);
    onPositionReset(paramLong1, paramBoolean);
  }
  
  public final RendererCapabilities getCapabilities()
  {
    return this;
  }
  
  protected final RendererConfiguration getConfiguration()
  {
    return configuration;
  }
  
  protected final int getIndex()
  {
    return index;
  }
  
  public MediaClock getMediaClock()
  {
    return null;
  }
  
  public final int getState()
  {
    return state;
  }
  
  public final SampleStream getStream()
  {
    return stream;
  }
  
  public final int getTrackType()
  {
    return 6;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {}
  
  public final boolean hasReadStreamToEnd()
  {
    return true;
  }
  
  public final boolean isCurrentStreamFinal()
  {
    return streamIsFinal;
  }
  
  public boolean isEnded()
  {
    return true;
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  public final void maybeThrowStreamError()
    throws IOException
  {}
  
  protected void onDisabled() {}
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onRendererOffsetChanged(long paramLong)
    throws ExoPlaybackException
  {}
  
  protected void onStarted()
    throws ExoPlaybackException
  {}
  
  protected void onStopped()
    throws ExoPlaybackException
  {}
  
  public final void replaceStream(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong)
    throws ExoPlaybackException
  {
    Assertions.checkState(streamIsFinal ^ true);
    stream = paramSampleStream;
    onRendererOffsetChanged(paramLong);
  }
  
  public final void resetPosition(long paramLong)
    throws ExoPlaybackException
  {
    streamIsFinal = false;
    onPositionReset(paramLong, false);
  }
  
  public final void setCurrentStreamFinal()
  {
    streamIsFinal = true;
  }
  
  public final void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public final void start()
    throws ExoPlaybackException
  {
    int i = state;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    Assertions.checkState(bool);
    state = 2;
    onStarted();
  }
  
  public final void stop()
    throws ExoPlaybackException
  {
    boolean bool;
    if (state == 2) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    state = 1;
    onStopped();
  }
  
  public int supportsFormat(Format paramFormat)
    throws ExoPlaybackException
  {
    return 0;
  }
  
  public int supportsMixedMimeTypeAdaptation()
    throws ExoPlaybackException
  {
    return 0;
  }
}
