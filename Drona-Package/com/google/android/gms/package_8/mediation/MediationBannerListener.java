package com.google.android.gms.package_8.mediation;

public abstract interface MediationBannerListener
{
  public abstract void onAdClicked(MediationBannerAdapter paramMediationBannerAdapter);
  
  public abstract void onAdClosed(MediationBannerAdapter paramMediationBannerAdapter);
  
  public abstract void onAdFailedToLoad(MediationBannerAdapter paramMediationBannerAdapter, int paramInt);
  
  public abstract void onAdLeftApplication(MediationBannerAdapter paramMediationBannerAdapter);
  
  public abstract void onAdLoaded(MediationBannerAdapter paramMediationBannerAdapter);
  
  public abstract void onAdOpened(MediationBannerAdapter paramMediationBannerAdapter);
  
  public abstract void onClick(MediationBannerAdapter paramMediationBannerAdapter, String paramString1, String paramString2);
}
