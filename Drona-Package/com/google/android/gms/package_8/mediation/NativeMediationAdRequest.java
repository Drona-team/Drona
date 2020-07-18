package com.google.android.gms.package_8.mediation;

import com.google.android.gms.package_8.formats.NativeAdOptions;
import java.util.Map;

public abstract interface NativeMediationAdRequest
  extends MediationAdRequest
{
  public abstract float getAdVolume();
  
  public abstract NativeAdOptions getNativeAdOptions();
  
  public abstract boolean isAdMuted();
  
  public abstract boolean isAppInstallAdRequested();
  
  public abstract boolean isContentAdRequested();
  
  public abstract boolean isUnifiedNativeAdRequested();
  
  public abstract boolean zzsu();
  
  public abstract Map zzsv();
}
