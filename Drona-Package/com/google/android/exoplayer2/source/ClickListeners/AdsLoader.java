package com.google.android.exoplayer2.source.ClickListeners;

import android.view.ViewGroup;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;

public abstract interface AdsLoader
{
  public abstract void attachPlayer(ExoPlayer paramExoPlayer, EventListener paramEventListener, ViewGroup paramViewGroup);
  
  public abstract void detachPlayer();
  
  public abstract void handlePrepareError(int paramInt1, int paramInt2, IOException paramIOException);
  
  public abstract void release();
  
  public abstract void setSupportedContentTypes(int... paramVarArgs);
  
  public abstract interface EventListener
  {
    public abstract void onAdClicked();
    
    public abstract void onAdLoadError(AdsMediaSource.AdLoadException paramAdLoadException, DataSpec paramDataSpec);
    
    public abstract void onAdPlaybackState(AdPlaybackState paramAdPlaybackState);
    
    public abstract void onAdTapped();
  }
}
