package com.google.android.gms.package_8.reward.mediation;

import android.os.Bundle;
import com.google.android.gms.package_8.reward.RewardItem;

public abstract interface MediationRewardedVideoAdListener
{
  public abstract void loadFragment(Bundle paramBundle);
  
  public abstract void onAdClicked(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onAdClosed(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onAdFailedToLoad(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter, int paramInt);
  
  public abstract void onAdLeftApplication(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onAdLoaded(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onAdOpened(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onInitializationFailed(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter, int paramInt);
  
  public abstract void onInitializationSucceeded(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onRewarded(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter, RewardItem paramRewardItem);
  
  public abstract void onVideoCompleted(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
  
  public abstract void onVideoStarted(MediationRewardedVideoAdAdapter paramMediationRewardedVideoAdAdapter);
}
