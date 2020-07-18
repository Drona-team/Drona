package com.google.android.gms.package_8.mediation.customevent;

import com.google.android.gms.package_8.mediation.NativeAdMapper;
import com.google.android.gms.package_8.mediation.UnifiedNativeAdMapper;

public abstract interface CustomEventNativeListener
  extends CustomEventListener
{
  public abstract void onAdImpression();
  
  public abstract void onAdLoaded(NativeAdMapper paramNativeAdMapper);
  
  public abstract void onAdLoaded(UnifiedNativeAdMapper paramUnifiedNativeAdMapper);
}
