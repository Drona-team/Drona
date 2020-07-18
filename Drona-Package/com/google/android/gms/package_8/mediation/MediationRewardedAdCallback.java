package com.google.android.gms.package_8.mediation;

import com.google.android.gms.package_8.rewarded.RewardItem;

public abstract interface MediationRewardedAdCallback
  extends MediationAdCallback
{
  public abstract void onAdFailedToShow(String paramString);
  
  public abstract void onUserEarnedReward(RewardItem paramRewardItem);
  
  public abstract void onVideoComplete();
  
  public abstract void onVideoStart();
}
