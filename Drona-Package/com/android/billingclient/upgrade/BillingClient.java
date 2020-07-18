package com.android.billingclient.upgrade;

import android.app.Activity;
import android.content.Context;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BillingClient
{
  public BillingClient() {}
  
  public static Builder newBuilder(Context paramContext)
  {
    return new Builder(null);
  }
  
  public abstract void acknowledgePurchase(AcknowledgePurchaseParams paramAcknowledgePurchaseParams, AcknowledgePurchaseResponseListener paramAcknowledgePurchaseResponseListener);
  
  public abstract void consumeAsync(ConsumeParams paramConsumeParams, ConsumeResponseListener paramConsumeResponseListener);
  
  public abstract void endConnection();
  
  public abstract BillingResult isFeatureSupported(String paramString);
  
  public abstract boolean isReady();
  
  public abstract BillingResult launchBillingFlow(Activity paramActivity, BillingFlowParams paramBillingFlowParams);
  
  public abstract void launchPriceChangeConfirmationFlow(Activity paramActivity, PriceChangeFlowParams paramPriceChangeFlowParams, PriceChangeConfirmationListener paramPriceChangeConfirmationListener);
  
  public abstract void loadRewardedSku(RewardLoadParams paramRewardLoadParams, RewardResponseListener paramRewardResponseListener);
  
  public abstract void queryPurchaseHistoryAsync(String paramString, PurchaseHistoryResponseListener paramPurchaseHistoryResponseListener);
  
  public abstract Purchase.PurchasesResult queryPurchases(String paramString);
  
  public abstract void querySkuDetailsAsync(SkuDetailsParams paramSkuDetailsParams, SkuDetailsResponseListener paramSkuDetailsResponseListener);
  
  public abstract void startConnection(BillingClientStateListener paramBillingClientStateListener);
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface BillingResponseCode
  {
    public static final int BILLING_UNAVAILABLE = 3;
    public static final int DEVELOPER_ERROR = 5;
    public static final int ERROR = 6;
    public static final int FEATURE_NOT_SUPPORTED = -2;
    public static final int ITEM_ALREADY_OWNED = 7;
    public static final int ITEM_NOT_OWNED = 8;
    public static final int ITEM_UNAVAILABLE = 4;
    public static final int SERVICE_DISCONNECTED = -1;
    public static final int SERVICE_TIMEOUT = -3;
    public static final int SERVICE_UNAVAILABLE = 2;
    public static final int USER_CANCELED = 1;
    public static final int VIEW_LIST = 0;
  }
  
  public final class Builder
  {
    private int mChildDirected = 0;
    private boolean mEnablePendingPurchases;
    private PurchasesUpdatedListener mListener;
    private int mUnderAgeOfConsent = 0;
    
    private Builder() {}
    
    public BillingClient build()
    {
      if (BillingClient.this != null)
      {
        if (mListener != null)
        {
          if (mEnablePendingPurchases == true) {
            return new BillingClientImpl(BillingClient.this, mChildDirected, mUnderAgeOfConsent, mEnablePendingPurchases, mListener);
          }
          throw new IllegalArgumentException("Support for pending purchases must be enabled. Enable this by calling 'enablePendingPurchases()' on BillingClientBuilder.");
        }
        throw new IllegalArgumentException("Please provide a valid listener for purchases updates.");
      }
      throw new IllegalArgumentException("Please provide a valid Context.");
    }
    
    public Builder enablePendingPurchases()
    {
      mEnablePendingPurchases = true;
      return this;
    }
    
    public Builder setChildDirected(int paramInt)
    {
      mChildDirected = paramInt;
      return this;
    }
    
    public Builder setListener(PurchasesUpdatedListener paramPurchasesUpdatedListener)
    {
      mListener = paramPurchasesUpdatedListener;
      return this;
    }
    
    public Builder setUnderAgeOfConsent(int paramInt)
    {
      mUnderAgeOfConsent = paramInt;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface ChildDirected
  {
    public static final int CHILD_DIRECTED = 1;
    public static final int NOT_CHILD_DIRECTED = 2;
    public static final int UNSPECIFIED = 0;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface FeatureType
  {
    public static final String IN_APP_ITEMS_ON_VR = "inAppItemsOnVr";
    public static final String PRICE_CHANGE_CONFIRMATION = "priceChangeConfirmation";
    public static final String SUBSCRIPTIONS = "subscriptions";
    public static final String SUBSCRIPTIONS_ON_VR = "subscriptionsOnVr";
    public static final String SUBSCRIPTIONS_UPDATE = "subscriptionsUpdate";
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface SkuType
  {
    public static final String INAPP = "inapp";
    public static final String SUBS = "subs";
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface UnderAgeOfConsent
  {
    public static final int NOT_UNDER_AGE_OF_CONSENT = 2;
    public static final int UNDER_AGE_OF_CONSENT = 1;
    public static final int UNSPECIFIED = 0;
  }
}
