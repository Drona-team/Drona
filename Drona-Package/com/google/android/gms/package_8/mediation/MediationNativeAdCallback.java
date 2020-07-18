package com.google.android.gms.package_8.mediation;

public abstract interface MediationNativeAdCallback
  extends MediationAdCallback
{
  public abstract void onAdLeftApplication();
  
  public abstract void onVideoComplete();
  
  public abstract void onVideoMute();
  
  public abstract void onVideoPause();
  
  public abstract void onVideoPlay();
  
  public abstract void onVideoUnmute();
}
