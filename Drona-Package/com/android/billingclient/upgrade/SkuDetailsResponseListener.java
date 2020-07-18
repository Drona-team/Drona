package com.android.billingclient.upgrade;

import java.util.List;

public abstract interface SkuDetailsResponseListener
{
  public abstract void onSkuDetailsResponse(BillingResult paramBillingResult, List paramList);
}
