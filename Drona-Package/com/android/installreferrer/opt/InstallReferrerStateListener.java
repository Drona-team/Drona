package com.android.installreferrer.opt;

public abstract interface InstallReferrerStateListener
{
  public abstract void onInstallReferrerServiceDisconnected();
  
  public abstract void onInstallReferrerSetupFinished(int paramInt);
}
