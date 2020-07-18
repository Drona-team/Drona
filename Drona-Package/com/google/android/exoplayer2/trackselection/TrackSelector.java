package com.google.android.exoplayer2.trackselection;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;

public abstract class TrackSelector
{
  @Nullable
  private BandwidthMeter bandwidthMeter;
  @Nullable
  private InvalidationListener listener;
  
  public TrackSelector() {}
  
  protected final BandwidthMeter getBandwidthMeter()
  {
    return (BandwidthMeter)Assertions.checkNotNull(bandwidthMeter);
  }
  
  public final void init(InvalidationListener paramInvalidationListener, BandwidthMeter paramBandwidthMeter)
  {
    listener = paramInvalidationListener;
    bandwidthMeter = paramBandwidthMeter;
  }
  
  protected final void invalidate()
  {
    if (listener != null) {
      listener.onTrackSelectionsInvalidated();
    }
  }
  
  public abstract void onSelectionActivated(Object paramObject);
  
  public abstract TrackSelectorResult selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray paramTrackGroupArray)
    throws ExoPlaybackException;
  
  public static abstract interface InvalidationListener
  {
    public abstract void onTrackSelectionsInvalidated();
  }
}
