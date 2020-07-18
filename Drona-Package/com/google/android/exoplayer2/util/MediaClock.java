package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.PlaybackParameters;

public abstract interface MediaClock
{
  public abstract PlaybackParameters getPlaybackParameters();
  
  public abstract long getPositionUs();
  
  public abstract PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters);
}
