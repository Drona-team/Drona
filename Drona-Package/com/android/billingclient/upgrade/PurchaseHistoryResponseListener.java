package com.android.billingclient.upgrade;

import java.util.List;

public abstract interface PurchaseHistoryResponseListener
{
  public abstract void onPurchaseHistoryResponse(BillingResult paramBillingResult, List paramList);
}
