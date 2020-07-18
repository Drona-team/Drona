package com.android.billingclient.upgrade;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.android.billingclient.api.ProxyBillingActivity;
import com.android.billingclient.util.BillingHelper;
import com.android.vending.billing.IInAppBillingService;
import com.android.vending.billing.IInAppBillingService.Stub;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;

class BillingClientImpl
  extends BillingClient
{
  private static final long ASYNCHRONOUS_TIMEOUT_IN_MILLISECONDS = 30000L;
  private static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
  private static final int MAX_IAP_VERSION = 10;
  private static final int MAX_SKU_DETAILS_ITEMS_PER_REQUEST = 20;
  private static final int MIN_IAP_VERSION = 3;
  private static final String PAGE_KEY = "BillingClient";
  private static final long SYNCHRONOUS_TIMEOUT_IN_MILLISECONDS = 5000L;
  private final Context mApplicationContext;
  private final BillingBroadcastManager mBroadcastManager;
  private final int mChildDirected;
  private int mClientState = 0;
  private final boolean mEnablePendingPurchases;
  private ExecutorService mExecutorService;
  private boolean mIABv10Supported;
  private boolean mIABv6Supported;
  private boolean mIABv8Supported;
  private boolean mIABv9Supported;
  private final String mQualifiedVersionNumber;
  private IInAppBillingService mService;
  private BillingServiceConnection mServiceConnection;
  private boolean mSubscriptionUpdateSupported;
  private boolean mSubscriptionsSupported;
  private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());
  private final int mUnderAgeOfConsent;
  private final ResultReceiver onPurchaseFinishedReceiver = new ResultReceiver(mUiThreadHandler)
  {
    public void onReceiveResult(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      PurchasesUpdatedListener localPurchasesUpdatedListener = mBroadcastManager.getListener();
      if (localPurchasesUpdatedListener == null)
      {
        BillingHelper.logWarn("BillingClient", "PurchasesUpdatedListener is null - no way to return the response.");
        return;
      }
      List localList = BillingHelper.extractPurchases(paramAnonymousBundle);
      localPurchasesUpdatedListener.onPurchasesUpdated(BillingResult.newBuilder().setResponseCode(paramAnonymousInt).setDebugMessage(BillingHelper.getDebugMessageFromBundle(paramAnonymousBundle, "BillingClient")).build(), localList);
    }
  };
  
  private BillingClientImpl(Activity paramActivity, int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
  {
    this(paramActivity.getApplicationContext(), paramInt1, paramInt2, paramBoolean, new BillingClientNativeCallback(), paramString);
  }
  
  BillingClientImpl(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean, PurchasesUpdatedListener paramPurchasesUpdatedListener)
  {
    this(paramContext, paramInt1, paramInt2, paramBoolean, paramPurchasesUpdatedListener, "2.0.3");
  }
  
  private BillingClientImpl(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean, PurchasesUpdatedListener paramPurchasesUpdatedListener, String paramString)
  {
    mApplicationContext = paramContext.getApplicationContext();
    mChildDirected = paramInt1;
    mUnderAgeOfConsent = paramInt2;
    mEnablePendingPurchases = paramBoolean;
    mBroadcastManager = new BillingBroadcastManager(mApplicationContext, paramPurchasesUpdatedListener);
    mQualifiedVersionNumber = paramString;
  }
  
  private void acknowledgePurchase(AcknowledgePurchaseParams paramAcknowledgePurchaseParams, long paramLong)
  {
    acknowledgePurchase(paramAcknowledgePurchaseParams, new BillingClientNativeCallback(paramLong));
  }
  
  private BillingResult broadcastFailureAndReturnBillingResponse(BillingResult paramBillingResult)
  {
    mBroadcastManager.getListener().onPurchasesUpdated(paramBillingResult, null);
    return paramBillingResult;
  }
  
  private void consumeAsync(ConsumeParams paramConsumeParams, long paramLong)
  {
    consumeAsync(paramConsumeParams, new BillingClientNativeCallback(paramLong));
  }
  
  private void consumeInternal(ConsumeParams paramConsumeParams, ConsumeResponseListener paramConsumeResponseListener)
  {
    String str1 = paramConsumeParams.getPurchaseToken();
    try
    {
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Consuming purchase with token: ");
      ((StringBuilder)localObject1).append(str1);
      BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject1).toString());
      int i;
      if (mIABv9Supported)
      {
        localObject1 = mService;
        Object localObject2 = mApplicationContext;
        localObject2 = ((Context)localObject2).getPackageName();
        boolean bool = mIABv9Supported;
        String str2 = mQualifiedVersionNumber;
        paramConsumeParams = ((IInAppBillingService)localObject1).consumePurchaseExtraParams(9, (String)localObject2, str1, BillingHelper.constructExtraParamsForConsume(paramConsumeParams, bool, str2));
        i = paramConsumeParams.getInt("RESPONSE_CODE");
        paramConsumeParams = BillingHelper.getDebugMessageFromBundle(paramConsumeParams, "BillingClient");
      }
      else
      {
        paramConsumeParams = mService;
        localObject1 = mApplicationContext;
        i = paramConsumeParams.consumePurchase(3, ((Context)localObject1).getPackageName(), str1);
        paramConsumeParams = "";
      }
      paramConsumeParams = BillingResult.newBuilder().setResponseCode(i).setDebugMessage(paramConsumeParams).build();
      if (i == 0)
      {
        postToUiThread(new BillingClientImpl.22(this, paramConsumeResponseListener, paramConsumeParams, str1));
        return;
      }
      postToUiThread(new BillingClientImpl.23(this, i, paramConsumeResponseListener, paramConsumeParams, str1));
      return;
    }
    catch (Exception paramConsumeParams)
    {
      postToUiThread(new BillingClientImpl.24(this, paramConsumeParams, paramConsumeResponseListener, str1));
    }
  }
  
  private Future executeAsync(Callable paramCallable, long paramLong, Runnable paramRunnable)
  {
    paramLong = (paramLong * 0.95D);
    if (mExecutorService == null) {
      mExecutorService = Executors.newFixedThreadPool(BillingHelper.NUMBER_OF_CORES);
    }
    ExecutorService localExecutorService = mExecutorService;
    try
    {
      paramCallable = localExecutorService.submit(paramCallable);
      mUiThreadHandler.postDelayed(new BillingClientImpl.20(this, paramCallable, paramRunnable), paramLong);
      return paramCallable;
    }
    catch (Exception paramCallable)
    {
      paramRunnable = new StringBuilder();
      paramRunnable.append("Async task throws exception ");
      paramRunnable.append(paramCallable);
      BillingHelper.logWarn("BillingClient", paramRunnable.toString());
    }
    return null;
  }
  
  private Bundle generateVrBundle()
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("vr", true);
    return localBundle;
  }
  
  private BillingResult getBillingResultForNullFutureResult()
  {
    if ((mClientState != 0) && (mClientState != 3)) {
      return BillingResults.INTERNAL_ERROR;
    }
    return BillingResults.SERVICE_DISCONNECTED;
  }
  
  private BillingResult isBillingSupportedOnVr(String paramString)
  {
    paramString = executeAsync(new BillingClientImpl.21(this, paramString), 5000L, null);
    TimeUnit localTimeUnit = TimeUnit.MILLISECONDS;
    try
    {
      paramString = paramString.get(5000L, localTimeUnit);
      paramString = (Integer)paramString;
      int i = paramString.intValue();
      if (i == 0) {
        return BillingResults.seq;
      }
      return BillingResults.FEATURE_NOT_SUPPORTED;
    }
    catch (Exception paramString)
    {
      for (;;) {}
    }
    BillingHelper.logWarn("BillingClient", "Exception while checking if billing is supported; try to reconnect");
    return BillingResults.SERVICE_DISCONNECTED;
  }
  
  private int launchBillingFlowCpp(Activity paramActivity, BillingFlowParams paramBillingFlowParams)
  {
    return launchBillingFlow(paramActivity, paramBillingFlowParams).getResponseCode();
  }
  
  private void launchPriceChangeConfirmationFlow(Activity paramActivity, PriceChangeFlowParams paramPriceChangeFlowParams, long paramLong)
  {
    launchPriceChangeConfirmationFlow(paramActivity, paramPriceChangeFlowParams, new BillingClientNativeCallback(paramLong));
  }
  
  private void loadRewardedSku(RewardLoadParams paramRewardLoadParams, long paramLong)
  {
    loadRewardedSku(paramRewardLoadParams, new BillingClientNativeCallback(paramLong));
  }
  
  private void postToUiThread(Runnable paramRunnable)
  {
    if (Thread.interrupted()) {
      return;
    }
    mUiThreadHandler.post(paramRunnable);
  }
  
  private void queryPurchaseHistoryAsync(String paramString, long paramLong)
  {
    queryPurchaseHistoryAsync(paramString, new BillingClientNativeCallback(paramLong));
  }
  
  private PurchaseHistoryResult queryPurchaseHistoryInternal(String paramString)
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("Querying purchase history, item type: ");
    ((StringBuilder)localObject1).append(paramString);
    BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject1).toString());
    ArrayList localArrayList1 = new ArrayList();
    Bundle localBundle = BillingHelper.constructExtraParamsForQueryPurchases(mIABv9Supported, mEnablePendingPurchases, mQualifiedVersionNumber);
    localObject1 = null;
    do
    {
      if (!mIABv6Supported) {}
      try
      {
        BillingHelper.logWarn("BillingClient", "getPurchaseHistory is not supported on current device");
        paramString = BillingResults.GET_PURCHASE_HISTORY_NOT_SUPPORTED;
        paramString = new PurchaseHistoryResult(paramString, null);
        return paramString;
      }
      catch (RemoteException paramString)
      {
        Object localObject2;
        Object localObject3;
        ArrayList localArrayList2;
        int i;
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Got exception trying to get purchase history: ");
        ((StringBuilder)localObject1).append(paramString);
        ((StringBuilder)localObject1).append("; try to reconnect");
        BillingHelper.logWarn("BillingClient", ((StringBuilder)localObject1).toString());
      }
      localObject2 = mService;
      localObject3 = mApplicationContext;
      localObject1 = ((IInAppBillingService)localObject2).getPurchaseHistory(6, ((Context)localObject3).getPackageName(), paramString, (String)localObject1, localBundle);
      localObject2 = PurchaseApiResponseChecker.checkPurchasesBundleValidity((Bundle)localObject1, "BillingClient", "getPurchaseHistory()");
      if (localObject2 != BillingResults.seq) {
        return new PurchaseHistoryResult((BillingResult)localObject2, null);
      }
      localObject2 = ((Bundle)localObject1).getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
      localObject3 = ((Bundle)localObject1).getStringArrayList("INAPP_PURCHASE_DATA_LIST");
      localArrayList2 = ((Bundle)localObject1).getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
      i = 0;
      while (i < ((ArrayList)localObject3).size())
      {
        Object localObject4 = (String)((ArrayList)localObject3).get(i);
        String str1 = (String)localArrayList2.get(i);
        String str2 = (String)((ArrayList)localObject2).get(i);
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Purchase record found for sku : ");
        localStringBuilder.append(str2);
        BillingHelper.logVerbose("BillingClient", localStringBuilder.toString());
        try
        {
          localObject4 = new PurchaseHistoryRecord((String)localObject4, str1);
          if (TextUtils.isEmpty(((PurchaseHistoryRecord)localObject4).getPurchaseToken())) {
            BillingHelper.logWarn("BillingClient", "BUG: empty/null token!");
          }
          localArrayList1.add(localObject4);
          i += 1;
        }
        catch (JSONException paramString)
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Got an exception trying to decode the purchase: ");
          ((StringBuilder)localObject1).append(paramString);
          BillingHelper.logWarn("BillingClient", ((StringBuilder)localObject1).toString());
          return new PurchaseHistoryResult(BillingResults.INTERNAL_ERROR, null);
        }
      }
      localObject2 = ((BaseBundle)localObject1).getString("INAPP_CONTINUATION_TOKEN");
      localObject1 = localObject2;
      localObject3 = new StringBuilder();
      ((StringBuilder)localObject3).append("Continuation token: ");
      ((StringBuilder)localObject3).append((String)localObject2);
      BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject3).toString());
    } while (!TextUtils.isEmpty((CharSequence)localObject2));
    return new PurchaseHistoryResult(BillingResults.seq, localArrayList1);
    return new PurchaseHistoryResult(BillingResults.SERVICE_DISCONNECTED, null);
  }
  
  private void queryPurchases(String paramString, long paramLong)
  {
    BillingClientNativeCallback localBillingClientNativeCallback = new BillingClientNativeCallback(paramLong);
    if (!isReady()) {
      localBillingClientNativeCallback.onQueryPurchasesResponse(BillingResults.SERVICE_DISCONNECTED, null);
    }
    if (executeAsync(new BillingClientImpl.8(this, paramString, localBillingClientNativeCallback), 30000L, new BillingClientImpl.9(this, localBillingClientNativeCallback)) == null) {
      localBillingClientNativeCallback.onQueryPurchasesResponse(getBillingResultForNullFutureResult(), null);
    }
  }
  
  private Purchase.PurchasesResult queryPurchasesInternal(String paramString)
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("Querying owned items, item type: ");
    ((StringBuilder)localObject1).append(paramString);
    BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject1).toString());
    ArrayList localArrayList1 = new ArrayList();
    Bundle localBundle = BillingHelper.constructExtraParamsForQueryPurchases(mIABv9Supported, mEnablePendingPurchases, mQualifiedVersionNumber);
    localObject1 = null;
    for (;;)
    {
      Object localObject2;
      Object localObject3;
      if (mIABv9Supported)
      {
        localObject2 = mService;
        localObject3 = mApplicationContext;
      }
      try
      {
        localObject1 = ((IInAppBillingService)localObject2).getPurchasesExtraParams(9, ((Context)localObject3).getPackageName(), paramString, (String)localObject1, localBundle);
        break label128;
        localObject2 = mService;
        localObject3 = mApplicationContext;
        localObject1 = ((IInAppBillingService)localObject2).getPurchases(3, ((Context)localObject3).getPackageName(), paramString, (String)localObject1);
        label128:
        localObject2 = PurchaseApiResponseChecker.checkPurchasesBundleValidity((Bundle)localObject1, "BillingClient", "getPurchase()");
        if (localObject2 != BillingResults.seq) {
          return new Purchase.PurchasesResult((BillingResult)localObject2, null);
        }
        localObject2 = ((Bundle)localObject1).getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
        localObject3 = ((Bundle)localObject1).getStringArrayList("INAPP_PURCHASE_DATA_LIST");
        ArrayList localArrayList2 = ((Bundle)localObject1).getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
        int i = 0;
        while (i < ((ArrayList)localObject3).size())
        {
          Object localObject4 = (String)((ArrayList)localObject3).get(i);
          String str1 = (String)localArrayList2.get(i);
          String str2 = (String)((ArrayList)localObject2).get(i);
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Sku is owned: ");
          localStringBuilder.append(str2);
          BillingHelper.logVerbose("BillingClient", localStringBuilder.toString());
          try
          {
            localObject4 = new Purchase((String)localObject4, str1);
            if (TextUtils.isEmpty(((Purchase)localObject4).getPurchaseToken())) {
              BillingHelper.logWarn("BillingClient", "BUG: empty/null token!");
            }
            localArrayList1.add(localObject4);
            i += 1;
          }
          catch (JSONException paramString)
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("Got an exception trying to decode the purchase: ");
            ((StringBuilder)localObject1).append(paramString);
            BillingHelper.logWarn("BillingClient", ((StringBuilder)localObject1).toString());
            return new Purchase.PurchasesResult(BillingResults.INTERNAL_ERROR, null);
          }
        }
        localObject2 = ((BaseBundle)localObject1).getString("INAPP_CONTINUATION_TOKEN");
        localObject1 = localObject2;
        localObject3 = new StringBuilder();
        ((StringBuilder)localObject3).append("Continuation token: ");
        ((StringBuilder)localObject3).append((String)localObject2);
        BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject3).toString());
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          return new Purchase.PurchasesResult(BillingResults.seq, localArrayList1);
        }
      }
      catch (Exception paramString)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Got exception trying to get purchases: ");
        ((StringBuilder)localObject1).append(paramString);
        ((StringBuilder)localObject1).append("; try to reconnect");
        BillingHelper.logWarn("BillingClient", ((StringBuilder)localObject1).toString());
      }
    }
    return new Purchase.PurchasesResult(BillingResults.SERVICE_DISCONNECTED, null);
  }
  
  private void querySkuDetailsAsync(String paramString, String[] paramArrayOfString, long paramLong)
  {
    querySkuDetailsAsync(SkuDetailsParams.newBuilder().setType(paramString).setSkusList(Arrays.asList(paramArrayOfString)).build(), new BillingClientNativeCallback(paramLong));
  }
  
  private void startConnection(long paramLong)
  {
    startConnection(new BillingClientNativeCallback(paramLong));
  }
  
  public void acknowledgePurchase(AcknowledgePurchaseParams paramAcknowledgePurchaseParams, AcknowledgePurchaseResponseListener paramAcknowledgePurchaseResponseListener)
  {
    if (!isReady())
    {
      paramAcknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(BillingResults.SERVICE_DISCONNECTED);
      return;
    }
    if (TextUtils.isEmpty(paramAcknowledgePurchaseParams.getPurchaseToken()))
    {
      BillingHelper.logWarn("BillingClient", "Please provide a valid purchase token.");
      paramAcknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(BillingResults.INVALID_PURCHASE_TOKEN);
      return;
    }
    if (!mIABv9Supported)
    {
      paramAcknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(BillingResults.API_VERSION_NOT_V9);
      return;
    }
    if (executeAsync(new BillingClientImpl.18(this, paramAcknowledgePurchaseParams, paramAcknowledgePurchaseResponseListener), 30000L, new BillingClientImpl.19(this, paramAcknowledgePurchaseResponseListener)) == null) {
      paramAcknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(getBillingResultForNullFutureResult());
    }
  }
  
  public void consumeAsync(ConsumeParams paramConsumeParams, ConsumeResponseListener paramConsumeResponseListener)
  {
    if (!isReady())
    {
      paramConsumeResponseListener.onConsumeResponse(BillingResults.SERVICE_DISCONNECTED, null);
      return;
    }
    if (executeAsync(new BillingClientImpl.12(this, paramConsumeParams, paramConsumeResponseListener), 30000L, new BillingClientImpl.13(this, paramConsumeResponseListener)) == null) {
      paramConsumeResponseListener.onConsumeResponse(getBillingResultForNullFutureResult(), null);
    }
  }
  
  public void endConnection()
  {
    for (;;)
    {
      try
      {
        localObject1 = mBroadcastManager;
      }
      catch (Throwable localThrowable)
      {
        Object localObject1;
        Object localObject2;
        mClientState = 3;
        throw localException;
      }
      try
      {
        ((BillingBroadcastManager)localObject1).destroy();
        localObject1 = mServiceConnection;
        if (localObject1 != null)
        {
          localObject1 = mServiceConnection;
          ((BillingServiceConnection)localObject1).markDisconnectedAndCleanUp();
        }
        localObject1 = mServiceConnection;
        if (localObject1 != null)
        {
          localObject1 = mService;
          if (localObject1 != null)
          {
            BillingHelper.logVerbose("BillingClient", "Unbinding from service.");
            localObject1 = mApplicationContext;
            localObject2 = mServiceConnection;
            ((Context)localObject1).unbindService((ServiceConnection)localObject2);
            mServiceConnection = null;
          }
        }
        mService = null;
        localObject1 = mExecutorService;
        if (localObject1 != null)
        {
          localObject1 = mExecutorService;
          ((ExecutorService)localObject1).shutdownNow();
          mExecutorService = null;
        }
      }
      catch (Exception localException)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("There was an exception while ending connection: ");
        ((StringBuilder)localObject2).append(localException);
        BillingHelper.logWarn("BillingClient", ((StringBuilder)localObject2).toString());
      }
    }
    mClientState = 3;
  }
  
  public BillingResult isFeatureSupported(String paramString)
  {
    if (!isReady()) {
      return BillingResults.SERVICE_DISCONNECTED;
    }
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 1987365622: 
      if (paramString.equals("subscriptions")) {
        i = 0;
      }
      break;
    case 1219490065: 
      if (paramString.equals("subscriptionsOnVr")) {
        i = 3;
      }
      break;
    case 292218239: 
      if (paramString.equals("inAppItemsOnVr")) {
        i = 2;
      }
      break;
    case 207616302: 
      if (paramString.equals("priceChangeConfirmation")) {
        i = 4;
      }
      break;
    case -422092961: 
      if (paramString.equals("subscriptionsUpdate")) {
        i = 1;
      }
      break;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unsupported feature: ");
      localStringBuilder.append(paramString);
      BillingHelper.logWarn("BillingClient", localStringBuilder.toString());
      return BillingResults.UNKNOWN_FEATURE;
    case 4: 
      if (mIABv8Supported) {
        return BillingResults.seq;
      }
      return BillingResults.FEATURE_NOT_SUPPORTED;
    case 3: 
      return isBillingSupportedOnVr("subs");
    case 2: 
      return isBillingSupportedOnVr("inapp");
    case 1: 
      if (mSubscriptionUpdateSupported) {
        return BillingResults.seq;
      }
      return BillingResults.FEATURE_NOT_SUPPORTED;
    }
    if (mSubscriptionsSupported) {
      return BillingResults.seq;
    }
    return BillingResults.FEATURE_NOT_SUPPORTED;
  }
  
  public boolean isReady()
  {
    return (mClientState == 2) && (mService != null) && (mServiceConnection != null);
  }
  
  public BillingResult launchBillingFlow(Activity paramActivity, BillingFlowParams paramBillingFlowParams)
  {
    if (!isReady()) {
      return broadcastFailureAndReturnBillingResponse(BillingResults.SERVICE_DISCONNECTED);
    }
    Object localObject1 = paramBillingFlowParams.getSkuType();
    String str = paramBillingFlowParams.getSku();
    Object localObject2 = paramBillingFlowParams.getSkuDetails();
    int j = 1;
    int i;
    if ((localObject2 != null) && (((SkuDetails)localObject2).isRewarded())) {
      i = 1;
    } else {
      i = 0;
    }
    if (str == null)
    {
      BillingHelper.logWarn("BillingClient", "Please fix the input params. SKU can't be null.");
      return broadcastFailureAndReturnBillingResponse(BillingResults.NULL_SKU);
    }
    if (localObject1 == null)
    {
      BillingHelper.logWarn("BillingClient", "Please fix the input params. SkuType can't be null.");
      return broadcastFailureAndReturnBillingResponse(BillingResults.NULL_SKU_TYPE);
    }
    if ((((String)localObject1).equals("subs")) && (!mSubscriptionsSupported))
    {
      BillingHelper.logWarn("BillingClient", "Current client doesn't support subscriptions.");
      return broadcastFailureAndReturnBillingResponse(BillingResults.SUBSCRIPTIONS_NOT_SUPPORTED);
    }
    if (paramBillingFlowParams.getOldSku() == null) {
      j = 0;
    }
    if ((j != 0) && (!mSubscriptionUpdateSupported))
    {
      BillingHelper.logWarn("BillingClient", "Current client doesn't support subscriptions update.");
      return broadcastFailureAndReturnBillingResponse(BillingResults.SUBSCRIPTIONS_UPDATE_NOT_SUPPORTED);
    }
    if ((paramBillingFlowParams.hasExtraParams()) && (!mIABv6Supported))
    {
      BillingHelper.logWarn("BillingClient", "Current client doesn't support extra params for buy intent.");
      return broadcastFailureAndReturnBillingResponse(BillingResults.EXTRA_PARAMS_NOT_SUPPORTED);
    }
    if ((i != 0) && (!mIABv6Supported))
    {
      BillingHelper.logWarn("BillingClient", "Current client doesn't support extra params for buy intent.");
      return broadcastFailureAndReturnBillingResponse(BillingResults.EXTRA_PARAMS_NOT_SUPPORTED);
    }
    Object localObject3 = new StringBuilder();
    ((StringBuilder)localObject3).append("Constructing buy intent for ");
    ((StringBuilder)localObject3).append(str);
    ((StringBuilder)localObject3).append(", item type: ");
    ((StringBuilder)localObject3).append((String)localObject1);
    BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject3).toString());
    if (mIABv6Supported)
    {
      localObject3 = BillingHelper.constructExtraParamsForLaunchBillingFlow(paramBillingFlowParams, mIABv9Supported, mEnablePendingPurchases, mQualifiedVersionNumber);
      if (!((SkuDetails)localObject2).getSkuDetailsToken().isEmpty()) {
        ((BaseBundle)localObject3).putString("skuDetailsToken", ((SkuDetails)localObject2).getSkuDetailsToken());
      }
      if (i != 0)
      {
        ((BaseBundle)localObject3).putString("rewardToken", ((SkuDetails)localObject2).rewardToken());
        if (mChildDirected != 0) {
          ((BaseBundle)localObject3).putInt("childDirected", mChildDirected);
        }
        if (mUnderAgeOfConsent != 0) {
          ((BaseBundle)localObject3).putInt("underAgeOfConsent", mUnderAgeOfConsent);
        }
      }
      if (mIABv9Supported) {
        i = 9;
      } else if (paramBillingFlowParams.getVrPurchaseFlow()) {
        i = 7;
      } else {
        i = 6;
      }
      paramBillingFlowParams = executeAsync(new BillingClientImpl.4(this, i, str, (String)localObject1, (Bundle)localObject3), 5000L, null);
    }
    else if (j != 0)
    {
      paramBillingFlowParams = executeAsync(new BillingClientImpl.5(this, paramBillingFlowParams, str), 5000L, null);
    }
    else
    {
      paramBillingFlowParams = executeAsync(new BillingClientImpl.6(this, str, (String)localObject1), 5000L, null);
    }
    localObject1 = TimeUnit.MILLISECONDS;
    try
    {
      paramBillingFlowParams = paramBillingFlowParams.get(5000L, (TimeUnit)localObject1);
      localObject1 = (Bundle)paramBillingFlowParams;
      i = BillingHelper.getResponseCodeFromBundle((Bundle)localObject1, "BillingClient");
      paramBillingFlowParams = BillingHelper.getDebugMessageFromBundle((Bundle)localObject1, "BillingClient");
      if (i != 0)
      {
        paramActivity = new StringBuilder();
        paramActivity.append("Unable to buy item, Error response code: ");
        paramActivity.append(i);
        BillingHelper.logWarn("BillingClient", paramActivity.toString());
        paramActivity = broadcastFailureAndReturnBillingResponse(BillingResult.newBuilder().setResponseCode(i).setDebugMessage(paramBillingFlowParams).build());
        return paramActivity;
      }
      paramBillingFlowParams = new Intent(paramActivity, ProxyBillingActivity.class);
      localObject2 = onPurchaseFinishedReceiver;
      paramBillingFlowParams.putExtra("result_receiver", (Parcelable)localObject2);
      localObject1 = ((Bundle)localObject1).getParcelable("BUY_INTENT");
      localObject1 = (PendingIntent)localObject1;
      paramBillingFlowParams.putExtra("BUY_INTENT", (Parcelable)localObject1);
      paramActivity.startActivity(paramBillingFlowParams);
      return BillingResults.seq;
    }
    catch (TimeoutException paramActivity)
    {
      for (;;) {}
    }
    catch (CancellationException paramActivity)
    {
      for (;;) {}
    }
    catch (Exception paramActivity)
    {
      for (;;) {}
    }
    paramActivity = new StringBuilder();
    paramActivity.append("Exception while launching billing flow: ; for sku: ");
    paramActivity.append(str);
    paramActivity.append("; try to reconnect");
    BillingHelper.logWarn("BillingClient", paramActivity.toString());
    return broadcastFailureAndReturnBillingResponse(BillingResults.SERVICE_DISCONNECTED);
    paramActivity = new StringBuilder();
    paramActivity.append("Time out while launching billing flow: ; for sku: ");
    paramActivity.append(str);
    paramActivity.append("; try to reconnect");
    BillingHelper.logWarn("BillingClient", paramActivity.toString());
    return broadcastFailureAndReturnBillingResponse(BillingResults.SERVICE_TIMEOUT);
  }
  
  public void launchPriceChangeConfirmationFlow(Activity paramActivity, PriceChangeFlowParams paramPriceChangeFlowParams, PriceChangeConfirmationListener paramPriceChangeConfirmationListener)
  {
    if (!isReady())
    {
      paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult(BillingResults.SERVICE_DISCONNECTED);
      return;
    }
    Object localObject1;
    Object localObject2;
    if ((paramPriceChangeFlowParams != null) && (paramPriceChangeFlowParams.getSkuDetails() != null))
    {
      paramPriceChangeFlowParams = paramPriceChangeFlowParams.getSkuDetails().getSku();
      if (paramPriceChangeFlowParams == null)
      {
        BillingHelper.logWarn("BillingClient", "Please fix the input params. priceChangeFlowParams must contain valid sku.");
        paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult(BillingResults.NULL_SKU);
        return;
      }
      if (!mIABv8Supported)
      {
        BillingHelper.logWarn("BillingClient", "Current client doesn't support price change confirmation flow.");
        paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult(BillingResults.FEATURE_NOT_SUPPORTED);
        return;
      }
      localObject1 = new Bundle();
      ((BaseBundle)localObject1).putString("playBillingLibraryVersion", mQualifiedVersionNumber);
      ((Bundle)localObject1).putBoolean("subs_price_change", true);
      localObject1 = executeAsync(new BillingClientImpl.2(this, paramPriceChangeFlowParams, (Bundle)localObject1), 5000L, null);
      localObject2 = TimeUnit.MILLISECONDS;
    }
    try
    {
      localObject1 = ((Future)localObject1).get(5000L, (TimeUnit)localObject2);
      localObject1 = (Bundle)localObject1;
      int i = BillingHelper.getResponseCodeFromBundle((Bundle)localObject1, "BillingClient");
      localObject2 = BillingHelper.getDebugMessageFromBundle((Bundle)localObject1, "BillingClient");
      localObject2 = BillingResult.newBuilder().setResponseCode(i).setDebugMessage((String)localObject2).build();
      if (i != 0)
      {
        paramActivity = new StringBuilder();
        paramActivity.append("Unable to launch price change flow, error response code: ");
        paramActivity.append(i);
        BillingHelper.logWarn("BillingClient", paramActivity.toString());
        paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult((BillingResult)localObject2);
        return;
      }
      localObject2 = mUiThreadHandler;
      localObject2 = new BillingClientImpl.3(this, (Handler)localObject2, paramPriceChangeConfirmationListener);
      Intent localIntent = new Intent(paramActivity, ProxyBillingActivity.class);
      localObject1 = ((Bundle)localObject1).getParcelable("SUBS_MANAGEMENT_INTENT");
      localObject1 = (PendingIntent)localObject1;
      localIntent.putExtra("SUBS_MANAGEMENT_INTENT", (Parcelable)localObject1);
      localIntent.putExtra("result_receiver", (Parcelable)localObject2);
      paramActivity.startActivity(localIntent);
      return;
    }
    catch (TimeoutException paramActivity)
    {
      for (;;) {}
    }
    catch (CancellationException paramActivity)
    {
      for (;;) {}
    }
    catch (Exception paramActivity)
    {
      for (;;) {}
    }
    paramActivity = new StringBuilder();
    paramActivity.append("Exception caught while launching Price Change Flow for sku: ");
    paramActivity.append(paramPriceChangeFlowParams);
    paramActivity.append("; try to reconnect");
    BillingHelper.logWarn("BillingClient", paramActivity.toString());
    paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult(BillingResults.SERVICE_DISCONNECTED);
    return;
    paramActivity = new StringBuilder();
    paramActivity.append("Time out while launching Price Change Flow for sku: ");
    paramActivity.append(paramPriceChangeFlowParams);
    paramActivity.append("; try to reconnect");
    BillingHelper.logWarn("BillingClient", paramActivity.toString());
    paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult(BillingResults.SERVICE_TIMEOUT);
    return;
    BillingHelper.logWarn("BillingClient", "Please fix the input params. priceChangeFlowParams must contain valid sku.");
    paramPriceChangeConfirmationListener.onPriceChangeConfirmationResult(BillingResults.NULL_SKU);
  }
  
  public void loadRewardedSku(RewardLoadParams paramRewardLoadParams, RewardResponseListener paramRewardResponseListener)
  {
    if (!mIABv6Supported)
    {
      paramRewardResponseListener.onRewardResponse(BillingResults.ITEM_UNAVAILABLE);
      return;
    }
    if (executeAsync(new BillingClientImpl.16(this, paramRewardLoadParams, paramRewardResponseListener), 30000L, new BillingClientImpl.17(this, paramRewardResponseListener)) == null) {
      paramRewardResponseListener.onRewardResponse(getBillingResultForNullFutureResult());
    }
  }
  
  public void queryPurchaseHistoryAsync(String paramString, PurchaseHistoryResponseListener paramPurchaseHistoryResponseListener)
  {
    if (!isReady())
    {
      paramPurchaseHistoryResponseListener.onPurchaseHistoryResponse(BillingResults.SERVICE_DISCONNECTED, null);
      return;
    }
    if (executeAsync(new BillingClientImpl.14(this, paramString, paramPurchaseHistoryResponseListener), 30000L, new BillingClientImpl.15(this, paramPurchaseHistoryResponseListener)) == null) {
      paramPurchaseHistoryResponseListener.onPurchaseHistoryResponse(getBillingResultForNullFutureResult(), null);
    }
  }
  
  public Purchase.PurchasesResult queryPurchases(String paramString)
  {
    if (!isReady()) {
      return new Purchase.PurchasesResult(BillingResults.SERVICE_DISCONNECTED, null);
    }
    if (TextUtils.isEmpty(paramString))
    {
      BillingHelper.logWarn("BillingClient", "Please provide a valid SKU type.");
      return new Purchase.PurchasesResult(BillingResults.EMPTY_SKU_TYPE, null);
    }
    paramString = executeAsync(new BillingClientImpl.7(this, paramString), 5000L, null);
    TimeUnit localTimeUnit = TimeUnit.MILLISECONDS;
    try
    {
      paramString = paramString.get(5000L, localTimeUnit);
      return (Purchase.PurchasesResult)paramString;
    }
    catch (TimeoutException paramString)
    {
      for (;;) {}
    }
    catch (CancellationException paramString)
    {
      for (;;) {}
    }
    catch (Exception paramString)
    {
      for (;;) {}
    }
    return new Purchase.PurchasesResult(BillingResults.INTERNAL_ERROR, null);
    return new Purchase.PurchasesResult(BillingResults.SERVICE_TIMEOUT, null);
  }
  
  public void querySkuDetailsAsync(SkuDetailsParams paramSkuDetailsParams, SkuDetailsResponseListener paramSkuDetailsResponseListener)
  {
    if (!isReady())
    {
      paramSkuDetailsResponseListener.onSkuDetailsResponse(BillingResults.SERVICE_DISCONNECTED, null);
      return;
    }
    String str = paramSkuDetailsParams.getSkuType();
    paramSkuDetailsParams = paramSkuDetailsParams.getSkusList();
    if (TextUtils.isEmpty(str))
    {
      BillingHelper.logWarn("BillingClient", "Please fix the input params. SKU type can't be empty.");
      paramSkuDetailsResponseListener.onSkuDetailsResponse(BillingResults.EMPTY_SKU_TYPE, null);
      return;
    }
    if (paramSkuDetailsParams == null)
    {
      BillingHelper.logWarn("BillingClient", "Please fix the input params. The list of SKUs can't be empty.");
      paramSkuDetailsResponseListener.onSkuDetailsResponse(BillingResults.EMPTY_SKU_LIST, null);
      return;
    }
    if (executeAsync(new BillingClientImpl.10(this, str, paramSkuDetailsParams, paramSkuDetailsResponseListener), 30000L, new BillingClientImpl.11(this, paramSkuDetailsResponseListener)) == null) {
      paramSkuDetailsResponseListener.onSkuDetailsResponse(getBillingResultForNullFutureResult(), null);
    }
  }
  
  SkuDetails.SkuDetailsResult querySkuDetailsInternal(String paramString, List paramList)
  {
    ArrayList localArrayList = new ArrayList();
    int m = paramList.size();
    int i;
    for (int j = 0; j < m; j = i)
    {
      i = j + 20;
      int k;
      if (i > m) {
        k = m;
      } else {
        k = i;
      }
      Object localObject2 = new ArrayList(paramList.subList(j, k));
      Object localObject1 = new Bundle();
      ((Bundle)localObject1).putStringArrayList("ITEM_ID_LIST", (ArrayList)localObject2);
      ((BaseBundle)localObject1).putString("playBillingLibraryVersion", mQualifiedVersionNumber);
      Object localObject3;
      if (mIABv10Supported)
      {
        localObject2 = mService;
        localObject3 = mApplicationContext;
      }
      for (;;)
      {
        try
        {
          localObject3 = ((Context)localObject3).getPackageName();
          boolean bool1 = mIABv9Supported;
          boolean bool2 = mEnablePendingPurchases;
          String str = mQualifiedVersionNumber;
          localObject1 = ((IInAppBillingService)localObject2).getSkuDetailsExtraParams(10, (String)localObject3, paramString, (Bundle)localObject1, BillingHelper.constructExtraParamsForGetSkuDetails(bool1, bool2, str));
          continue;
          localObject2 = mService;
          localObject3 = mApplicationContext;
          localObject1 = ((IInAppBillingService)localObject2).getSkuDetails(3, ((Context)localObject3).getPackageName(), paramString, (Bundle)localObject1);
          if (localObject1 == null)
          {
            BillingHelper.logWarn("BillingClient", "querySkuDetailsAsync got null sku details list");
            return new SkuDetails.SkuDetailsResult(4, "Null sku details list", null);
          }
          if (!((BaseBundle)localObject1).containsKey("DETAILS_LIST"))
          {
            i = BillingHelper.getResponseCodeFromBundle((Bundle)localObject1, "BillingClient");
            paramString = BillingHelper.getDebugMessageFromBundle((Bundle)localObject1, "BillingClient");
            if (i != 0)
            {
              paramList = new StringBuilder();
              paramList.append("getSkuDetails() failed. Response code: ");
              paramList.append(i);
              BillingHelper.logWarn("BillingClient", paramList.toString());
              return new SkuDetails.SkuDetailsResult(i, paramString, localArrayList);
            }
            BillingHelper.logWarn("BillingClient", "getSkuDetails() returned a bundle with neither an error nor a detail list.");
            return new SkuDetails.SkuDetailsResult(6, paramString, localArrayList);
          }
          localObject1 = ((Bundle)localObject1).getStringArrayList("DETAILS_LIST");
          if (localObject1 == null)
          {
            BillingHelper.logWarn("BillingClient", "querySkuDetailsAsync got null response list");
            return new SkuDetails.SkuDetailsResult(4, "querySkuDetailsAsync got null response list", null);
          }
          j = 0;
          if (j < ((ArrayList)localObject1).size()) {
            localObject2 = (String)((ArrayList)localObject1).get(j);
          }
        }
        catch (Exception paramString)
        {
          paramList = new StringBuilder();
          paramList.append("querySkuDetailsAsync got a remote exception (try to reconnect).");
          paramList.append(paramString);
          BillingHelper.logWarn("BillingClient", paramList.toString());
          return new SkuDetails.SkuDetailsResult(-1, "Service connection is disconnected.", null);
        }
        try
        {
          localObject2 = new SkuDetails((String)localObject2);
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append("Got sku details: ");
          ((StringBuilder)localObject3).append(localObject2);
          BillingHelper.logVerbose("BillingClient", ((StringBuilder)localObject3).toString());
          localArrayList.add(localObject2);
          j += 1;
        }
        catch (JSONException paramString) {}
      }
      BillingHelper.logWarn("BillingClient", "Got a JSON exception trying to decode SkuDetails.");
      return new SkuDetails.SkuDetailsResult(6, "Error trying to decode SkuDetails.", null);
    }
    return new SkuDetails.SkuDetailsResult(0, "", localArrayList);
  }
  
  void setExecutorService(ExecutorService paramExecutorService)
  {
    mExecutorService = paramExecutorService;
  }
  
  public void startConnection(BillingClientStateListener paramBillingClientStateListener)
  {
    if (isReady())
    {
      BillingHelper.logVerbose("BillingClient", "Service connection is valid. No need to re-initialize.");
      paramBillingClientStateListener.onBillingSetupFinished(BillingResults.seq);
      return;
    }
    if (mClientState == 1)
    {
      BillingHelper.logWarn("BillingClient", "Client is already in the process of connecting to billing service.");
      paramBillingClientStateListener.onBillingSetupFinished(BillingResults.CLIENT_CONNECTING);
      return;
    }
    if (mClientState == 3)
    {
      BillingHelper.logWarn("BillingClient", "Client was already closed and can't be reused. Please create another instance.");
      paramBillingClientStateListener.onBillingSetupFinished(BillingResults.SERVICE_DISCONNECTED);
      return;
    }
    mClientState = 1;
    mBroadcastManager.registerReceiver();
    BillingHelper.logVerbose("BillingClient", "Starting in-app billing setup.");
    mServiceConnection = new BillingServiceConnection(paramBillingClientStateListener, null);
    Intent localIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
    localIntent.setPackage("com.android.vending");
    Object localObject1 = mApplicationContext.getPackageManager().queryIntentServices(localIntent, 0);
    if ((localObject1 != null) && (!((List)localObject1).isEmpty()))
    {
      Object localObject2 = (ResolveInfo)((List)localObject1).get(0);
      if (serviceInfo != null)
      {
        localObject1 = serviceInfo.packageName;
        localObject2 = serviceInfo.name;
        if (("com.android.vending".equals(localObject1)) && (localObject2 != null))
        {
          localObject1 = new ComponentName((String)localObject1, (String)localObject2);
          localIntent = new Intent(localIntent);
          localIntent.setComponent((ComponentName)localObject1);
          localIntent.putExtra("playBillingLibraryVersion", mQualifiedVersionNumber);
          if (mApplicationContext.bindService(localIntent, mServiceConnection, 1))
          {
            BillingHelper.logVerbose("BillingClient", "Service was bonded successfully.");
            return;
          }
          BillingHelper.logWarn("BillingClient", "Connection to Billing service is blocked.");
        }
        else
        {
          BillingHelper.logWarn("BillingClient", "The device doesn't have valid Play Store.");
        }
      }
    }
    mClientState = 0;
    BillingHelper.logVerbose("BillingClient", "Billing service unavailable on device.");
    paramBillingClientStateListener.onBillingSetupFinished(BillingResults.BILLING_UNAVAILABLE);
  }
  
  final class BillingServiceConnection
    implements ServiceConnection
  {
    private boolean disconnected = false;
    private final Object lock = new Object();
    private BillingClientStateListener mListener;
    
    private BillingServiceConnection(BillingClientStateListener paramBillingClientStateListener)
    {
      mListener = paramBillingClientStateListener;
    }
    
    private void notifySetupResult(BillingResult paramBillingResult)
    {
      BillingClientImpl.this.postToUiThread(new BillingClientImpl.BillingServiceConnection.1(this, paramBillingResult));
    }
    
    void markDisconnectedAndCleanUp()
    {
      Object localObject = lock;
      try
      {
        mListener = null;
        disconnected = true;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      BillingHelper.logVerbose("BillingClient", "Billing service connected.");
      BillingClientImpl.access$302(BillingClientImpl.this, IInAppBillingService.Stub.asInterface(paramIBinder));
      if (BillingClientImpl.this.executeAsync(new BillingClientImpl.BillingServiceConnection.2(this), 30000L, new BillingClientImpl.BillingServiceConnection.3(this)) == null) {
        notifySetupResult(BillingClientImpl.this.getBillingResultForNullFutureResult());
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      BillingHelper.logWarn("BillingClient", "Billing service disconnected.");
      BillingClientImpl.access$302(BillingClientImpl.this, null);
      BillingClientImpl.access$1202(BillingClientImpl.this, 0);
      paramComponentName = lock;
      try
      {
        if (mListener != null) {
          mListener.onBillingServiceDisconnected();
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface ClientState
  {
    public static final int CLOSED = 3;
    public static final int CONNECTED = 2;
    public static final int CONNECTING = 1;
    public static final int DISCONNECTED = 0;
  }
  
  class PurchaseHistoryResult
  {
    private List<com.android.billingclient.api.PurchaseHistoryRecord> mPurchaseHistoryRecordList;
    
    PurchaseHistoryResult(List paramList)
    {
      mPurchaseHistoryRecordList = paramList;
    }
    
    BillingResult getBillingResult()
    {
      return BillingClientImpl.this;
    }
    
    List getPurchaseHistoryRecordList()
    {
      return mPurchaseHistoryRecordList;
    }
  }
}
