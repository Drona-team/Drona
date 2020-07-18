package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;

public abstract interface LoadControl
{
  public abstract Allocator getAllocator();
  
  public abstract long getBackBufferDurationUs();
  
  public abstract void onPrepared();
  
  public abstract void onReleased();
  
  public abstract void onStopped();
  
  public abstract void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);
  
  public abstract boolean retainBackBufferFromKeyframe();
  
  public abstract boolean shouldContinueLoading(long paramLong, float paramFloat);
  
  public abstract boolean shouldStartPlayback(long paramLong, float paramFloat, boolean paramBoolean);
}
