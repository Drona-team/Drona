package com.google.android.gms.package_8.rewarded;

public abstract interface RewardItem
{
  public static final RewardItem DEFAULT_REWARD = new Money();
  
  public abstract int getAmount();
  
  public abstract String getType();
}
