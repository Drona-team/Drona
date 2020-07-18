package com.android.billingclient.upgrade;

import java.util.Collections;
import java.util.List;

class BillingClientNativeCallback
  implements AcknowledgePurchaseResponseListener, BillingClientStateListener, ConsumeResponseListener, PriceChangeConfirmationListener, PurchaseHistoryResponseListener, PurchasesUpdatedListener, RewardResponseListener, SkuDetailsResponseListener
{
  private final long futureHandle;
  
  BillingClientNativeCallback()
  {
    futureHandle = 0L;
  }
  
  BillingClientNativeCallback(long paramLong)
  {
    futureHandle = paramLong;
  }
  
  public static native void nativeOnAcknowledgePurchaseResponse(int paramInt, String paramString, long paramLong);
  
  public static native void nativeOnBillingServiceDisconnected();
  
  public static native void nativeOnBillingSetupFinished(int paramInt, String paramString, long paramLong);
  
  public static native void nativeOnConsumePurchaseResponse(int paramInt, String paramString1, String paramString2, long paramLong);
  
  public static native void nativeOnPriceChangeConfirmationResult(int paramInt, String paramString, long paramLong);
  
  public static native void nativeOnPurchaseHistoryResponse(int paramInt, String paramString, PurchaseHistoryRecord[] paramArrayOfPurchaseHistoryRecord, long paramLong);
  
  public static native void nativeOnPurchasesUpdated(int paramInt, String paramString, Purchase[] paramArrayOfPurchase);
  
  public static native void nativeOnQueryPurchasesResponse(int paramInt, String paramString, Purchase[] paramArrayOfPurchase, long paramLong);
  
  public static native void nativeOnRewardResponse(int paramInt, String paramString, long paramLong);
  
  public static native void nativeOnSkuDetailsResponse(int paramInt, String paramString, SkuDetails[] paramArrayOfSkuDetails, long paramLong);
  
  public void onAcknowledgePurchaseResponse(BillingResult paramBillingResult)
  {
    nativeOnAcknowledgePurchaseResponse(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), futureHandle);
  }
  
  public void onBillingServiceDisconnected() {}
  
  public void onBillingSetupFinished(BillingResult paramBillingResult)
  {
    nativeOnBillingSetupFinished(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), futureHandle);
  }
  
  public void onConsumeResponse(BillingResult paramBillingResult, String paramString)
  {
    nativeOnConsumePurchaseResponse(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), paramString, futureHandle);
  }
  
  public void onPriceChangeConfirmationResult(BillingResult paramBillingResult)
  {
    nativeOnPriceChangeConfirmationResult(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), futureHandle);
  }
  
  public void onPurchaseHistoryResponse(BillingResult paramBillingResult, List paramList)
  {
    List localList = paramList;
    if (paramList == null) {
      localList = Collections.emptyList();
    }
    paramList = (PurchaseHistoryRecord[])localList.toArray(new PurchaseHistoryRecord[localList.size()]);
    nativeOnPurchaseHistoryResponse(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), paramList, futureHandle);
  }
  
  public void onPurchasesUpdated(BillingResult paramBillingResult, List paramList)
  {
    List localList = paramList;
    if (paramList == null) {
      localList = Collections.emptyList();
    }
    paramList = (Purchase[])localList.toArray(new Purchase[localList.size()]);
    nativeOnPurchasesUpdated(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), paramList);
  }
  
  void onQueryPurchasesResponse(BillingResult paramBillingResult, List paramList)
  {
    List localList = paramList;
    if (paramList == null) {
      localList = Collections.emptyList();
    }
    paramList = (Purchase[])localList.toArray(new Purchase[localList.size()]);
    nativeOnQueryPurchasesResponse(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), paramList, futureHandle);
  }
  
  public void onRewardResponse(BillingResult paramBillingResult)
  {
    nativeOnRewardResponse(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), futureHandle);
  }
  
  public void onSkuDetailsResponse(BillingResult paramBillingResult, List paramList)
  {
    List localList = paramList;
    if (paramList == null) {
      localList = Collections.emptyList();
    }
    paramList = (SkuDetails[])localList.toArray(new SkuDetails[localList.size()]);
    nativeOnSkuDetailsResponse(paramBillingResult.getResponseCode(), paramBillingResult.getDebugMessage(), paramList, futureHandle);
  }
}
