package com.google.android.gms.package_8.rewarded;

public class RewardedAdCallback
{
  public static final int ERROR_CODE_AD_REUSED = 1;
  public static final int ERROR_CODE_APP_NOT_FOREGROUND = 3;
  public static final int ERROR_CODE_INTERNAL_ERROR = 0;
  public static final int ERROR_CODE_NOT_READY = 2;
  
  public RewardedAdCallback() {}
  
  public void onRewardedAdClosed() {}
  
  public void onRewardedAdFailedToShow(int paramInt) {}
  
  public void onRewardedAdOpened() {}
  
  public void onUserEarnedReward(RewardItem paramRewardItem) {}
}
