package com.google.android.exoplayer2;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.StandaloneMediaClock;

final class DefaultMediaClock
  implements MediaClock
{
  private final PlaybackParameterListener listener;
  @Nullable
  private MediaClock rendererClock;
  @Nullable
  private Renderer rendererClockSource;
  private final StandaloneMediaClock standaloneMediaClock;
  
  public DefaultMediaClock(PlaybackParameterListener paramPlaybackParameterListener, Clock paramClock)
  {
    listener = paramPlaybackParameterListener;
    standaloneMediaClock = new StandaloneMediaClock(paramClock);
  }
  
  private void ensureSynced()
  {
    long l = rendererClock.getPositionUs();
    standaloneMediaClock.resetPosition(l);
    PlaybackParameters localPlaybackParameters = rendererClock.getPlaybackParameters();
    if (!localPlaybackParameters.equals(standaloneMediaClock.getPlaybackParameters()))
    {
      standaloneMediaClock.setPlaybackParameters(localPlaybackParameters);
      listener.onPlaybackParametersChanged(localPlaybackParameters);
    }
  }
  
  private boolean isUsingRendererClock()
  {
    return (rendererClockSource != null) && (!rendererClockSource.isEnded()) && ((rendererClockSource.isReady()) || (!rendererClockSource.hasReadStreamToEnd()));
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    if (rendererClock != null) {
      return rendererClock.getPlaybackParameters();
    }
    return standaloneMediaClock.getPlaybackParameters();
  }
  
  public long getPositionUs()
  {
    if (isUsingRendererClock()) {
      return rendererClock.getPositionUs();
    }
    return standaloneMediaClock.getPositionUs();
  }
  
  public void onRendererDisabled(Renderer paramRenderer)
  {
    if (paramRenderer == rendererClockSource)
    {
      rendererClock = null;
      rendererClockSource = null;
    }
  }
  
  public void onRendererEnabled(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    MediaClock localMediaClock = paramRenderer.getMediaClock();
    if ((localMediaClock != null) && (localMediaClock != rendererClock))
    {
      if (rendererClock == null)
      {
        rendererClock = localMediaClock;
        rendererClockSource = paramRenderer;
        rendererClock.setPlaybackParameters(standaloneMediaClock.getPlaybackParameters());
        ensureSynced();
        return;
      }
      throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
    }
  }
  
  public void resetPosition(long paramLong)
  {
    standaloneMediaClock.resetPosition(paramLong);
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    PlaybackParameters localPlaybackParameters = paramPlaybackParameters;
    if (rendererClock != null) {
      localPlaybackParameters = rendererClock.setPlaybackParameters(paramPlaybackParameters);
    }
    standaloneMediaClock.setPlaybackParameters(localPlaybackParameters);
    listener.onPlaybackParametersChanged(localPlaybackParameters);
    return localPlaybackParameters;
  }
  
  public void start()
  {
    standaloneMediaClock.start();
  }
  
  public void stop()
  {
    standaloneMediaClock.stop();
  }
  
  public long syncAndGetPositionUs()
  {
    if (isUsingRendererClock())
    {
      ensureSynced();
      return rendererClock.getPositionUs();
    }
    return standaloneMediaClock.getPositionUs();
  }
  
  public static abstract interface PlaybackParameterListener
  {
    public abstract void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters);
  }
}
