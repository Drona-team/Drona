package com.android.billingclient.upgrade;

public abstract interface BillingClientStateListener
{
  public abstract void onBillingServiceDisconnected();
  
  public abstract void onBillingSetupFinished(BillingResult paramBillingResult);
}
