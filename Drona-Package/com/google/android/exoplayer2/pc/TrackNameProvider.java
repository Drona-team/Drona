package com.google.android.exoplayer2.pc;

import com.google.android.exoplayer2.Format;

public abstract interface TrackNameProvider
{
  public abstract String getTrackName(Format paramFormat);
}
