package com.dooboolab.RNIap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.android.billingclient.upgrade.AcknowledgePurchaseParams;
import com.android.billingclient.upgrade.AcknowledgePurchaseParams.Builder;
import com.android.billingclient.upgrade.AcknowledgePurchaseResponseListener;
import com.android.billingclient.upgrade.BillingClient;
import com.android.billingclient.upgrade.BillingClient.Builder;
import com.android.billingclient.upgrade.BillingClientStateListener;
import com.android.billingclient.upgrade.BillingFlowParams;
import com.android.billingclient.upgrade.BillingFlowParams.Builder;
import com.android.billingclient.upgrade.BillingResult;
import com.android.billingclient.upgrade.ConsumeParams;
import com.android.billingclient.upgrade.ConsumeParams.Builder;
import com.android.billingclient.upgrade.ConsumeResponseListener;
import com.android.billingclient.upgrade.Purchase;
import com.android.billingclient.upgrade.Purchase.PurchasesResult;
import com.android.billingclient.upgrade.PurchaseHistoryRecord;
import com.android.billingclient.upgrade.PurchaseHistoryResponseListener;
import com.android.billingclient.upgrade.PurchasesUpdatedListener;
import com.android.billingclient.upgrade.SkuDetailsParams;
import com.android.billingclient.upgrade.SkuDetailsParams.Builder;
import com.android.billingclient.upgrade.SkuDetailsResponseListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ObjectAlreadyConsumedException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RNIapModule
  extends ReactContextBaseJavaModule
  implements PurchasesUpdatedListener
{
  private static final String PROMISE_BUY_ITEM = "PROMISE_BUY_ITEM";
  final String SAVEFILE = "RNIapModule";
  private BillingClient billingClient;
  private LifecycleEventListener lifecycleEventListener = new LifecycleEventListener()
  {
    public void onHostDestroy()
    {
      if (billingClient != null)
      {
        billingClient.endConnection();
        RNIapModule.access$002(RNIapModule.this, null);
      }
    }
    
    public void onHostPause() {}
    
    public void onHostResume() {}
  };
  private HashMap<String, ArrayList<Promise>> promises = new HashMap();
  private ReactContext reactContext;
  private List<com.android.billingclient.api.SkuDetails> skus;
  
  public RNIapModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    reactContext = paramReactApplicationContext;
    skus = new ArrayList();
    paramReactApplicationContext.addLifecycleEventListener(lifecycleEventListener);
  }
  
  private void ensureConnection(final Promise paramPromise, final Runnable paramRunnable)
  {
    if ((billingClient != null) && (billingClient.isReady()))
    {
      paramRunnable.run();
      return;
    }
    paramRunnable = new BillingClientStateListener()
    {
      private boolean bSetupCallbackConsumed = false;
      
      public void onBillingServiceDisconnected()
      {
        Log.d("RNIapModule", "billing client disconnected");
      }
      
      public void onBillingSetupFinished(BillingResult paramAnonymousBillingResult)
      {
        if (!bSetupCallbackConsumed)
        {
          bSetupCallbackConsumed = true;
          if (paramAnonymousBillingResult.getResponseCode() == 0)
          {
            if ((billingClient != null) && (billingClient.isReady())) {
              paramRunnable.run();
            }
          }
          else
          {
            WritableMap localWritableMap = Arguments.createMap();
            localWritableMap.putInt("responseCode", paramAnonymousBillingResult.getResponseCode());
            localWritableMap.putString("debugMessage", paramAnonymousBillingResult.getDebugMessage());
            String[] arrayOfString = DoobooUtils.getInstance().getBillingResponseData(paramAnonymousBillingResult.getResponseCode());
            localWritableMap.putString("code", arrayOfString[0]);
            localWritableMap.putString("message", arrayOfString[1]);
            RNIapModule.this.sendEvent(reactContext, "purchase-error", localWritableMap);
            DoobooUtils.getInstance().rejectPromiseWithBillingError(paramPromise, paramAnonymousBillingResult.getResponseCode());
          }
        }
      }
    };
    Object localObject = reactContext;
    try
    {
      localObject = BillingClient.newBuilder((Context)localObject).enablePendingPurchases().setListener(this).build();
      billingClient = ((BillingClient)localObject);
      localObject = billingClient;
      ((BillingClient)localObject).startConnection(paramRunnable);
      return;
    }
    catch (Exception paramRunnable)
    {
      paramPromise.reject("E_NOT_PREPARED", paramRunnable.getMessage(), paramRunnable);
    }
  }
  
  private void sendEvent(ReactContext paramReactContext, String paramString, WritableMap paramWritableMap)
  {
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)paramReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(paramString, paramWritableMap);
  }
  
  private void sendUnconsumedPurchases(Promise paramPromise)
  {
    ensureConnection(paramPromise, new Runnable()
    {
      public void run()
      {
        Purchase.PurchasesResult localPurchasesResult = billingClient.queryPurchases("inapp");
        ArrayList localArrayList = new ArrayList();
        if (localPurchasesResult != null)
        {
          if (localPurchasesResult.getPurchasesList() == null) {
            return;
          }
          Iterator localIterator = localPurchasesResult.getPurchasesList().iterator();
          while (localIterator.hasNext())
          {
            Purchase localPurchase = (Purchase)localIterator.next();
            if (!localPurchase.isAcknowledged()) {
              localArrayList.add(localPurchase);
            }
          }
          onPurchasesUpdated(localPurchasesResult.getBillingResult(), localArrayList);
        }
      }
    });
  }
  
  public void acknowledgePurchase(String paramString1, String paramString2, final Promise paramPromise)
  {
    paramString1 = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(paramString1).setDeveloperPayload(paramString2).build();
    billingClient.acknowledgePurchase(paramString1, new AcknowledgePurchaseResponseListener()
    {
      public void onAcknowledgePurchaseResponse(BillingResult paramAnonymousBillingResult)
      {
        if (paramAnonymousBillingResult.getResponseCode() != 0) {
          DoobooUtils.getInstance().rejectPromiseWithBillingError(paramPromise, paramAnonymousBillingResult.getResponseCode());
        }
        try
        {
          WritableMap localWritableMap = Arguments.createMap();
          localWritableMap.putInt("responseCode", paramAnonymousBillingResult.getResponseCode());
          localWritableMap.putString("debugMessage", paramAnonymousBillingResult.getDebugMessage());
          paramAnonymousBillingResult = DoobooUtils.getInstance().getBillingResponseData(paramAnonymousBillingResult.getResponseCode());
          String str = paramAnonymousBillingResult[0];
          localWritableMap.putString("code", str);
          paramAnonymousBillingResult = paramAnonymousBillingResult[1];
          localWritableMap.putString("message", paramAnonymousBillingResult);
          paramAnonymousBillingResult = paramPromise;
          paramAnonymousBillingResult.resolve(localWritableMap);
          return;
        }
        catch (ObjectAlreadyConsumedException paramAnonymousBillingResult)
        {
          Log.e("RNIapModule", paramAnonymousBillingResult.getMessage());
        }
      }
    });
  }
  
  public void buyItemByType(final String paramString1, final String paramString2, final String paramString3, final Integer paramInteger, final Promise paramPromise)
  {
    final Activity localActivity = getCurrentActivity();
    if (localActivity == null)
    {
      paramPromise.reject("E_UNKNOWN", "getCurrentActivity returned null");
      return;
    }
    ensureConnection(paramPromise, new Runnable()
    {
      public void run()
      {
        DoobooUtils.getInstance().addPromiseForKey("PROMISE_BUY_ITEM", paramPromise);
        Object localObject2 = BillingFlowParams.newBuilder();
        if ((paramString1.equals("subs")) && (paramString3 != null) && (!paramString3.isEmpty())) {
          if ((paramInteger != null) && (paramInteger.intValue() != -1))
          {
            ((BillingFlowParams.Builder)localObject2).setOldSku(paramString3);
            if (paramInteger.intValue() == 2) {
              ((BillingFlowParams.Builder)localObject2).setReplaceSkusProrationMode(2);
            } else if (paramInteger.intValue() == 3) {
              ((BillingFlowParams.Builder)localObject2).setReplaceSkusProrationMode(3);
            } else {
              ((BillingFlowParams.Builder)localObject2).setOldSku(paramString3);
            }
          }
          else
          {
            ((BillingFlowParams.Builder)localObject2).setOldSku(paramString3);
          }
        }
        if ((paramInteger.intValue() != 0) && (paramInteger.intValue() != -1)) {
          ((BillingFlowParams.Builder)localObject2).setReplaceSkusProrationMode(paramInteger.intValue());
        }
        String[] arrayOfString = null;
        Iterator localIterator = skus.iterator();
        do
        {
          localObject1 = arrayOfString;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject1 = (com.android.billingclient.upgrade.SkuDetails)localIterator.next();
        } while (!((com.android.billingclient.upgrade.SkuDetails)localObject1).getSku().equals(paramString2));
        if (localObject1 == null)
        {
          localObject1 = Arguments.createMap();
          ((WritableMap)localObject1).putString("debugMessage", "The sku was not found. Please fetch products first by calling getItems");
          ((WritableMap)localObject1).putString("code", "PROMISE_BUY_ITEM");
          ((WritableMap)localObject1).putString("message", "The sku was not found. Please fetch products first by calling getItems");
          RNIapModule.this.sendEvent(reactContext, "purchase-error", (WritableMap)localObject1);
          paramPromise.reject("PROMISE_BUY_ITEM", "The sku was not found. Please fetch products first by calling getItems");
          return;
        }
        Object localObject1 = ((BillingFlowParams.Builder)localObject2).setSkuDetails((com.android.billingclient.upgrade.SkuDetails)localObject1).build();
        localObject1 = billingClient.launchBillingFlow(localActivity, (BillingFlowParams)localObject1);
        arrayOfString = DoobooUtils.getInstance().getBillingResponseData(((BillingResult)localObject1).getResponseCode());
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("buyItemByType (type: ");
        ((StringBuilder)localObject2).append(paramString1);
        ((StringBuilder)localObject2).append(", sku: ");
        ((StringBuilder)localObject2).append(paramString2);
        ((StringBuilder)localObject2).append(", oldSku: ");
        ((StringBuilder)localObject2).append(paramString3);
        ((StringBuilder)localObject2).append(", prorationMode: ");
        ((StringBuilder)localObject2).append(paramInteger);
        ((StringBuilder)localObject2).append(") responseCode: ");
        ((StringBuilder)localObject2).append(((BillingResult)localObject1).getResponseCode());
        ((StringBuilder)localObject2).append("(");
        ((StringBuilder)localObject2).append(arrayOfString[0]);
        ((StringBuilder)localObject2).append(")");
        Log.d("RNIapModule", ((StringBuilder)localObject2).toString());
      }
    });
  }
  
  public void consumeProduct(String paramString1, String paramString2, final Promise paramPromise)
  {
    paramString1 = ConsumeParams.newBuilder().setPurchaseToken(paramString1).setDeveloperPayload(paramString2).build();
    billingClient.consumeAsync(paramString1, new ConsumeResponseListener()
    {
      public void onConsumeResponse(BillingResult paramAnonymousBillingResult, String paramAnonymousString)
      {
        if (paramAnonymousBillingResult.getResponseCode() != 0) {
          DoobooUtils.getInstance().rejectPromiseWithBillingError(paramPromise, paramAnonymousBillingResult.getResponseCode());
        }
        try
        {
          paramAnonymousString = Arguments.createMap();
          paramAnonymousString.putInt("responseCode", paramAnonymousBillingResult.getResponseCode());
          paramAnonymousString.putString("debugMessage", paramAnonymousBillingResult.getDebugMessage());
          paramAnonymousBillingResult = DoobooUtils.getInstance().getBillingResponseData(paramAnonymousBillingResult.getResponseCode());
          String str = paramAnonymousBillingResult[0];
          paramAnonymousString.putString("code", str);
          paramAnonymousBillingResult = paramAnonymousBillingResult[1];
          paramAnonymousString.putString("message", paramAnonymousBillingResult);
          paramAnonymousBillingResult = paramPromise;
          paramAnonymousBillingResult.resolve(paramAnonymousString);
          return;
        }
        catch (ObjectAlreadyConsumedException paramAnonymousBillingResult)
        {
          paramPromise.reject(paramAnonymousBillingResult.getMessage());
        }
      }
    });
  }
  
  public void endConnection(Promise paramPromise)
  {
    if (billingClient != null)
    {
      BillingClient localBillingClient = billingClient;
      try
      {
        localBillingClient.endConnection();
      }
      catch (Exception localException)
      {
        paramPromise.reject("endConnection", localException.getMessage());
        return;
      }
    }
    try
    {
      paramPromise.resolve(Boolean.valueOf(true));
      return;
    }
    catch (ObjectAlreadyConsumedException paramPromise)
    {
      Log.e("RNIapModule", paramPromise.getMessage());
    }
  }
  
  public void getAvailableItemsByType(final String paramString, final Promise paramPromise)
  {
    ensureConnection(paramPromise, new Runnable()
    {
      public void run()
      {
        WritableNativeArray localWritableNativeArray = new WritableNativeArray();
        Object localObject2 = billingClient;
        if (paramString.equals("subs")) {
          localObject1 = "subs";
        } else {
          localObject1 = "inapp";
        }
        Object localObject1 = ((BillingClient)localObject2).queryPurchases((String)localObject1).getPurchasesList();
        if (localObject1 != null)
        {
          localObject1 = ((List)localObject1).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (Purchase)((Iterator)localObject1).next();
            WritableNativeMap localWritableNativeMap = new WritableNativeMap();
            localWritableNativeMap.putString("productId", ((Purchase)localObject2).getSku());
            localWritableNativeMap.putString("transactionId", ((Purchase)localObject2).getOrderId());
            localWritableNativeMap.putDouble("transactionDate", ((Purchase)localObject2).getPurchaseTime());
            localWritableNativeMap.putString("transactionReceipt", ((Purchase)localObject2).getOriginalJson());
            localWritableNativeMap.putString("orderId", ((Purchase)localObject2).getOrderId());
            localWritableNativeMap.putString("purchaseToken", ((Purchase)localObject2).getPurchaseToken());
            localWritableNativeMap.putString("developerPayloadAndroid", ((Purchase)localObject2).getDeveloperPayload());
            localWritableNativeMap.putString("signatureAndroid", ((Purchase)localObject2).getSignature());
            localWritableNativeMap.putInt("purchaseStateAndroid", ((Purchase)localObject2).getPurchaseState());
            localWritableNativeMap.putBoolean("isAcknowledgedAndroid", ((Purchase)localObject2).isAcknowledged());
            if (paramString.equals("subs")) {
              localWritableNativeMap.putBoolean("autoRenewingAndroid", ((Purchase)localObject2).isAutoRenewing());
            }
            localWritableNativeArray.pushMap(localWritableNativeMap);
          }
        }
        localObject1 = paramPromise;
        try
        {
          ((Promise)localObject1).resolve(localWritableNativeArray);
          return;
        }
        catch (ObjectAlreadyConsumedException localObjectAlreadyConsumedException)
        {
          Log.e("RNIapModule", localObjectAlreadyConsumedException.getMessage());
        }
      }
    });
  }
  
  public void getItemsByType(final String paramString, final ReadableArray paramReadableArray, final Promise paramPromise)
  {
    ensureConnection(paramPromise, new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        while (i < paramReadableArray.size())
        {
          localArrayList.add(paramReadableArray.getString(i));
          i += 1;
        }
        SkuDetailsParams.Builder localBuilder = SkuDetailsParams.newBuilder();
        localBuilder.setSkusList(localArrayList).setType(paramString);
        billingClient.querySkuDetailsAsync(localBuilder.build(), new SkuDetailsResponseListener()
        {
          public void onSkuDetailsResponse(BillingResult paramAnonymous2BillingResult, List paramAnonymous2List)
          {
            Object localObject = new StringBuilder();
            ((StringBuilder)localObject).append("responseCode: ");
            ((StringBuilder)localObject).append(paramAnonymous2BillingResult.getResponseCode());
            Log.d("RNIapModule", ((StringBuilder)localObject).toString());
            if (paramAnonymous2BillingResult.getResponseCode() != 0)
            {
              DoobooUtils.getInstance().rejectPromiseWithBillingError(val$promise, paramAnonymous2BillingResult.getResponseCode());
              return;
            }
            paramAnonymous2BillingResult = paramAnonymous2List.iterator();
            while (paramAnonymous2BillingResult.hasNext())
            {
              localObject = (com.android.billingclient.upgrade.SkuDetails)paramAnonymous2BillingResult.next();
              if (!skus.contains(localObject)) {
                skus.add(localObject);
              }
            }
            paramAnonymous2BillingResult = new WritableNativeArray();
            paramAnonymous2List = paramAnonymous2List.iterator();
            while (paramAnonymous2List.hasNext())
            {
              localObject = (com.android.billingclient.upgrade.SkuDetails)paramAnonymous2List.next();
              WritableMap localWritableMap = Arguments.createMap();
              localWritableMap.putString("productId", ((com.android.billingclient.upgrade.SkuDetails)localObject).getSku());
              localWritableMap.putDouble("price", (float)((com.android.billingclient.upgrade.SkuDetails)localObject).getPriceAmountMicros() / 1000000.0F);
              localWritableMap.putString("currency", ((com.android.billingclient.upgrade.SkuDetails)localObject).getPriceCurrencyCode());
              localWritableMap.putString("type", ((com.android.billingclient.upgrade.SkuDetails)localObject).getType());
              localWritableMap.putString("localizedPrice", ((com.android.billingclient.upgrade.SkuDetails)localObject).getPrice());
              localWritableMap.putString("title", ((com.android.billingclient.upgrade.SkuDetails)localObject).getTitle());
              localWritableMap.putString("description", ((com.android.billingclient.upgrade.SkuDetails)localObject).getDescription());
              localWritableMap.putString("introductoryPrice", ((com.android.billingclient.upgrade.SkuDetails)localObject).getIntroductoryPrice());
              localWritableMap.putString("subscriptionPeriodAndroid", ((com.android.billingclient.upgrade.SkuDetails)localObject).getSubscriptionPeriod());
              localWritableMap.putString("freeTrialPeriodAndroid", ((com.android.billingclient.upgrade.SkuDetails)localObject).getFreeTrialPeriod());
              localWritableMap.putString("introductoryPriceCyclesAndroid", ((com.android.billingclient.upgrade.SkuDetails)localObject).getIntroductoryPriceCycles());
              localWritableMap.putString("introductoryPricePeriodAndroid", ((com.android.billingclient.upgrade.SkuDetails)localObject).getIntroductoryPricePeriod());
              localWritableMap.putString("iconUrl", ((com.android.billingclient.upgrade.SkuDetails)localObject).getIconUrl());
              localWritableMap.putString("originalJson", ((com.android.billingclient.upgrade.SkuDetails)localObject).getOriginalJson());
              localWritableMap.putDouble("originalPrice", (float)((com.android.billingclient.upgrade.SkuDetails)localObject).getOriginalPriceAmountMicros() / 1000000.0F);
              paramAnonymous2BillingResult.pushMap(localWritableMap);
            }
            paramAnonymous2List = val$promise;
            try
            {
              paramAnonymous2List.resolve(paramAnonymous2BillingResult);
              return;
            }
            catch (ObjectAlreadyConsumedException paramAnonymous2BillingResult)
            {
              Log.e("RNIapModule", paramAnonymous2BillingResult.getMessage());
            }
          }
        });
      }
    });
  }
  
  public String getName()
  {
    return "RNIapModule";
  }
  
  public void getPurchaseHistoryByType(final String paramString, final Promise paramPromise)
  {
    ensureConnection(paramPromise, new Runnable()
    {
      public void run()
      {
        BillingClient localBillingClient = billingClient;
        String str;
        if (paramString.equals("subs")) {
          str = "subs";
        } else {
          str = "inapp";
        }
        localBillingClient.queryPurchaseHistoryAsync(str, new PurchaseHistoryResponseListener()
        {
          public void onPurchaseHistoryResponse(BillingResult paramAnonymous2BillingResult, List paramAnonymous2List)
          {
            if (paramAnonymous2BillingResult.getResponseCode() != 0)
            {
              DoobooUtils.getInstance().rejectPromiseWithBillingError(val$promise, paramAnonymous2BillingResult.getResponseCode());
              return;
            }
            Log.d("RNIapModule", paramAnonymous2List.toString());
            paramAnonymous2BillingResult = Arguments.createArray();
            paramAnonymous2List = paramAnonymous2List.iterator();
            while (paramAnonymous2List.hasNext())
            {
              PurchaseHistoryRecord localPurchaseHistoryRecord = (PurchaseHistoryRecord)paramAnonymous2List.next();
              WritableMap localWritableMap = Arguments.createMap();
              localWritableMap.putString("productId", localPurchaseHistoryRecord.getSku());
              localWritableMap.putDouble("transactionDate", localPurchaseHistoryRecord.getPurchaseTime());
              localWritableMap.putString("transactionReceipt", localPurchaseHistoryRecord.getOriginalJson());
              localWritableMap.putString("purchaseToken", localPurchaseHistoryRecord.getPurchaseToken());
              localWritableMap.putString("dataAndroid", localPurchaseHistoryRecord.getOriginalJson());
              localWritableMap.putString("signatureAndroid", localPurchaseHistoryRecord.getSignature());
              localWritableMap.putString("developerPayload", localPurchaseHistoryRecord.getDeveloperPayload());
              paramAnonymous2BillingResult.pushMap(localWritableMap);
            }
            paramAnonymous2List = val$promise;
            try
            {
              paramAnonymous2List.resolve(paramAnonymous2BillingResult);
              return;
            }
            catch (ObjectAlreadyConsumedException paramAnonymous2BillingResult)
            {
              Log.e("RNIapModule", paramAnonymous2BillingResult.getMessage());
            }
          }
        });
      }
    });
  }
  
  public void initConnection(final Promise paramPromise)
  {
    billingClient = BillingClient.newBuilder(reactContext).enablePendingPurchases().setListener(this).build();
    billingClient.startConnection(new BillingClientStateListener()
    {
      public void onBillingServiceDisconnected()
      {
        Promise localPromise = paramPromise;
        try
        {
          localPromise.reject("initConnection", "Billing service disconnected");
          return;
        }
        catch (ObjectAlreadyConsumedException localObjectAlreadyConsumedException)
        {
          Log.e("RNIapModule", localObjectAlreadyConsumedException.getMessage());
        }
      }
      
      public void onBillingSetupFinished(BillingResult paramAnonymousBillingResult)
      {
        int i = paramAnonymousBillingResult.getResponseCode();
        if (i == 0)
        {
          paramAnonymousBillingResult = paramPromise;
          try
          {
            paramAnonymousBillingResult.resolve(Boolean.valueOf(true));
            return;
          }
          catch (ObjectAlreadyConsumedException paramAnonymousBillingResult) {}
        }
        else
        {
          paramAnonymousBillingResult = DoobooUtils.getInstance();
          Promise localPromise = paramPromise;
          paramAnonymousBillingResult.rejectPromiseWithBillingError(localPromise, i);
          return;
        }
        Log.e("RNIapModule", paramAnonymousBillingResult.getMessage());
      }
    });
  }
  
  public void onPurchasesUpdated(BillingResult paramBillingResult, List paramList)
  {
    Object localObject;
    if (paramBillingResult.getResponseCode() != 0)
    {
      paramList = Arguments.createMap();
      paramList.putInt("responseCode", paramBillingResult.getResponseCode());
      paramList.putString("debugMessage", paramBillingResult.getDebugMessage());
      localObject = DoobooUtils.getInstance().getBillingResponseData(paramBillingResult.getResponseCode());
      paramList.putString("code", localObject[0]);
      paramList.putString("message", localObject[1]);
      sendEvent(reactContext, "purchase-error", paramList);
      DoobooUtils.getInstance().rejectPromisesWithBillingError("PROMISE_BUY_ITEM", paramBillingResult.getResponseCode());
      return;
    }
    if (paramList != null)
    {
      paramBillingResult = paramList.iterator();
      while (paramBillingResult.hasNext())
      {
        localObject = (Purchase)paramBillingResult.next();
        WritableMap localWritableMap = Arguments.createMap();
        localWritableMap.putString("productId", ((Purchase)localObject).getSku());
        localWritableMap.putString("transactionId", ((Purchase)localObject).getOrderId());
        localWritableMap.putDouble("transactionDate", ((Purchase)localObject).getPurchaseTime());
        localWritableMap.putString("transactionReceipt", ((Purchase)localObject).getOriginalJson());
        localWritableMap.putString("purchaseToken", ((Purchase)localObject).getPurchaseToken());
        localWritableMap.putString("dataAndroid", ((Purchase)localObject).getOriginalJson());
        localWritableMap.putString("signatureAndroid", ((Purchase)localObject).getSignature());
        localWritableMap.putBoolean("autoRenewingAndroid", ((Purchase)localObject).isAutoRenewing());
        localWritableMap.putBoolean("isAcknowledgedAndroid", ((Purchase)localObject).isAcknowledged());
        localWritableMap.putInt("purchaseStateAndroid", ((Purchase)localObject).getPurchaseState());
        sendEvent(reactContext, "purchase-updated", localWritableMap);
      }
      DoobooUtils.getInstance().resolvePromisesForKey("PROMISE_BUY_ITEM", paramList.get(0));
      return;
    }
    paramList = Arguments.createMap();
    paramList.putInt("responseCode", paramBillingResult.getResponseCode());
    paramList.putString("debugMessage", paramBillingResult.getDebugMessage());
    paramList.putString("code", DoobooUtils.getInstance().getBillingResponseData(paramBillingResult.getResponseCode())[0]);
    paramList.putString("message", "purchases are null.");
    sendEvent(reactContext, "purchase-error", paramList);
    DoobooUtils.getInstance().rejectPromisesWithBillingError("PROMISE_BUY_ITEM", paramBillingResult.getResponseCode());
  }
  
  public void refreshItems(final Promise paramPromise)
  {
    ensureConnection(paramPromise, new Runnable()
    {
      public void run()
      {
        final WritableNativeArray localWritableNativeArray = new WritableNativeArray();
        Object localObject1 = billingClient.queryPurchases("inapp");
        if (localObject1 == null)
        {
          paramPromise.reject("refreshItem", "No results for query");
          return;
        }
        localObject1 = ((Purchase.PurchasesResult)localObject1).getPurchasesList();
        if ((localObject1 != null) && (((List)localObject1).size() != 0))
        {
          localObject1 = ((List)localObject1).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            Object localObject2 = (Purchase)((Iterator)localObject1).next();
            localObject2 = ConsumeParams.newBuilder().setPurchaseToken(((Purchase)localObject2).getPurchaseToken()).setDeveloperPayload(((Purchase)localObject2).getDeveloperPayload()).build();
            ConsumeResponseListener local1 = new ConsumeResponseListener()
            {
              public void onConsumeResponse(BillingResult paramAnonymous2BillingResult, String paramAnonymous2String)
              {
                if (paramAnonymous2BillingResult.getResponseCode() != 0)
                {
                  DoobooUtils.getInstance().rejectPromiseWithBillingError(val$promise, paramAnonymous2BillingResult.getResponseCode());
                  return;
                }
                localWritableNativeArray.pushString(paramAnonymous2String);
                paramAnonymous2BillingResult = val$promise;
                try
                {
                  paramAnonymous2BillingResult.resolve(Boolean.valueOf(true));
                  return;
                }
                catch (ObjectAlreadyConsumedException paramAnonymous2BillingResult)
                {
                  val$promise.reject(paramAnonymous2BillingResult.getMessage());
                }
              }
            };
            billingClient.consumeAsync((ConsumeParams)localObject2, local1);
          }
          return;
        }
        paramPromise.reject("refreshItem", "No purchases found");
      }
    });
  }
  
  public void startListening(Promise paramPromise)
  {
    sendUnconsumedPurchases(paramPromise);
  }
}
