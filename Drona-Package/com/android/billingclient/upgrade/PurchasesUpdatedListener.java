package com.android.billingclient.upgrade;

import java.util.List;

public abstract interface PurchasesUpdatedListener
{
  public abstract void onPurchasesUpdated(BillingResult paramBillingResult, List paramList);
}
