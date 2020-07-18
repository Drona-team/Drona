package com.google.android.gms.package_8.mediation;

import android.content.Context;
import java.util.List;

public abstract class Adapter
  implements MediationExtrasReceiver
{
  public Adapter() {}
  
  public abstract VersionInfo getSDKVersionInfo();
  
  public abstract VersionInfo getVersionInfo();
  
  public abstract void initialize(Context paramContext, InitializationCompleteCallback paramInitializationCompleteCallback, List paramList);
  
  public void loadBannerAd(MediationBannerAdConfiguration paramMediationBannerAdConfiguration, MediationAdLoadCallback paramMediationAdLoadCallback)
  {
    paramMediationAdLoadCallback.onFailure(String.valueOf(getClass().getSimpleName()).concat(" does not support banner ads."));
  }
  
  public void loadInterstitialAd(MediationInterstitialAdConfiguration paramMediationInterstitialAdConfiguration, MediationAdLoadCallback paramMediationAdLoadCallback)
  {
    paramMediationAdLoadCallback.onFailure(String.valueOf(getClass().getSimpleName()).concat(" does not support interstitial ads."));
  }
  
  public void loadNativeAd(MediationNativeAdConfiguration paramMediationNativeAdConfiguration, MediationAdLoadCallback paramMediationAdLoadCallback)
  {
    paramMediationAdLoadCallback.onFailure(String.valueOf(getClass().getSimpleName()).concat(" does not support native ads."));
  }
  
  public void loadRewardedAd(MediationRewardedAdConfiguration paramMediationRewardedAdConfiguration, MediationAdLoadCallback paramMediationAdLoadCallback)
  {
    paramMediationAdLoadCallback.onFailure(String.valueOf(getClass().getSimpleName()).concat(" does not support rewarded ads."));
  }
}
