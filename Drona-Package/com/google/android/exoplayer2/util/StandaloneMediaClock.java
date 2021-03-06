package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.PlaybackParameters;

public final class StandaloneMediaClock
  implements MediaClock
{
  private long baseElapsedMs;
  private long baseUs;
  private final Clock clock;
  private PlaybackParameters playbackParameters;
  private boolean started;
  
  public StandaloneMediaClock(Clock paramClock)
  {
    clock = paramClock;
    playbackParameters = PlaybackParameters.DEFAULT;
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return playbackParameters;
  }
  
  public long getPositionUs()
  {
    long l2 = baseUs;
    long l1 = l2;
    if (started)
    {
      l1 = clock.elapsedRealtime() - baseElapsedMs;
      if (playbackParameters.speed == 1.0F) {
        return l2 + IpAddress.msToUs(l1);
      }
      l1 = l2 + playbackParameters.getMediaTimeUsForPlayoutTimeMs(l1);
    }
    return l1;
  }
  
  public void resetPosition(long paramLong)
  {
    baseUs = paramLong;
    if (started) {
      baseElapsedMs = clock.elapsedRealtime();
    }
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    if (started) {
      resetPosition(getPositionUs());
    }
    playbackParameters = paramPlaybackParameters;
    return paramPlaybackParameters;
  }
  
  public void start()
  {
    if (!started)
    {
      baseElapsedMs = clock.elapsedRealtime();
      started = true;
    }
  }
  
  public void stop()
  {
    if (started)
    {
      resetPosition(getPositionUs());
      started = false;
    }
  }
}
