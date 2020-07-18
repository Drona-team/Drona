package com.google.android.gms.package_8.mediation;

import com.google.android.gms.package_8.formats.NativeCustomTemplateAd;

public abstract interface MediationNativeListener
{
  public abstract void onAdClicked(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdClosed(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdFailedToLoad(MediationNativeAdapter paramMediationNativeAdapter, int paramInt);
  
  public abstract void onAdImpression(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdLeftApplication(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdLoaded(MediationNativeAdapter paramMediationNativeAdapter, NativeAdMapper paramNativeAdMapper);
  
  public abstract void onAdLoaded(MediationNativeAdapter paramMediationNativeAdapter, UnifiedNativeAdMapper paramUnifiedNativeAdMapper);
  
  public abstract void onAdOpened(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onVideoEnd(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void setCurrentTheme(MediationNativeAdapter paramMediationNativeAdapter, NativeCustomTemplateAd paramNativeCustomTemplateAd);
  
  public abstract void setCurrentTheme(MediationNativeAdapter paramMediationNativeAdapter, NativeCustomTemplateAd paramNativeCustomTemplateAd, String paramString);
}
