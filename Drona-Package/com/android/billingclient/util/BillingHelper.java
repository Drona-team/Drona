package com.android.billingclient.util;

import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.billingclient.upgrade.AcknowledgePurchaseParams;
import com.android.billingclient.upgrade.BillingFlowParams;
import com.android.billingclient.upgrade.BillingResult;
import com.android.billingclient.upgrade.BillingResult.Builder;
import com.android.billingclient.upgrade.ConsumeParams;
import com.android.billingclient.upgrade.Purchase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;

public final class BillingHelper
{
  public static final String DEBUG_MESSAGE = "DEBUG_MESSAGE";
  public static final String EXTRA_PARAMS_DEVELOPER_PAYLOAD = "developerPayload";
  public static final String EXTRA_PARAMS_ENABLE_PENDING_PURCHASES = "enablePendingPurchases";
  public static final String EXTRA_PARAM_KEY_SKU_DETAILS_TOKEN = "skuDetailsToken";
  public static final String EXTRA_PARAM_KEY_SUBS_PRICE_CHANGE = "subs_price_change";
  public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
  private static final String INTERNAL_ERROR = "An internal error occurred.";
  public static final String LIBRARY_VERSION_KEY = "playBillingLibraryVersion";
  public static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
  private static final String PAGE_KEY = "BillingHelper";
  public static final String RESPONSE_BUY_INTENT_KEY = "BUY_INTENT";
  public static final String RESPONSE_CODE = "RESPONSE_CODE";
  public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
  public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
  private static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
  public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
  private static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
  public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
  public static final String RESPONSE_SUBS_MANAGEMENT_INTENT_KEY = "SUBS_MANAGEMENT_INTENT";
  
  public BillingHelper() {}
  
  public static Bundle constructExtraParamsForAcknowledgePurchase(AcknowledgePurchaseParams paramAcknowledgePurchaseParams, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("playBillingLibraryVersion", paramString);
    paramAcknowledgePurchaseParams = paramAcknowledgePurchaseParams.getDeveloperPayload();
    if (!TextUtils.isEmpty(paramAcknowledgePurchaseParams)) {
      localBundle.putString("developerPayload", paramAcknowledgePurchaseParams);
    }
    return localBundle;
  }
  
  public static Bundle constructExtraParamsForConsume(ConsumeParams paramConsumeParams, boolean paramBoolean, String paramString)
  {
    Bundle localBundle = new Bundle();
    if (paramBoolean) {
      localBundle.putString("playBillingLibraryVersion", paramString);
    }
    paramConsumeParams = paramConsumeParams.getDeveloperPayload();
    if ((paramBoolean) && (!TextUtils.isEmpty(paramConsumeParams))) {
      localBundle.putString("developerPayload", paramConsumeParams);
    }
    return localBundle;
  }
  
  public static Bundle constructExtraParamsForGetSkuDetails(boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    Bundle localBundle = new Bundle();
    if (paramBoolean1) {
      localBundle.putString("playBillingLibraryVersion", paramString);
    }
    if ((paramBoolean1) && (paramBoolean2)) {
      localBundle.putBoolean("enablePendingPurchases", true);
    }
    return localBundle;
  }
  
  public static Bundle constructExtraParamsForLaunchBillingFlow(BillingFlowParams paramBillingFlowParams, boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("playBillingLibraryVersion", paramString);
    if (paramBillingFlowParams.getReplaceSkusProrationMode() != 0) {
      localBundle.putInt("prorationMode", paramBillingFlowParams.getReplaceSkusProrationMode());
    }
    if (!TextUtils.isEmpty(paramBillingFlowParams.getAccountId())) {
      localBundle.putString("accountId", paramBillingFlowParams.getAccountId());
    }
    if (paramBillingFlowParams.getVrPurchaseFlow()) {
      localBundle.putBoolean("vr", true);
    }
    if (!TextUtils.isEmpty(paramBillingFlowParams.getOldSku())) {
      localBundle.putStringArrayList("skusToReplace", new ArrayList(Arrays.asList(new String[] { paramBillingFlowParams.getOldSku() })));
    }
    if (!TextUtils.isEmpty(paramBillingFlowParams.getDeveloperId())) {
      localBundle.putString("developerId", paramBillingFlowParams.getDeveloperId());
    }
    if ((paramBoolean1) && (paramBoolean2)) {
      localBundle.putBoolean("enablePendingPurchases", true);
    }
    return localBundle;
  }
  
  public static Bundle constructExtraParamsForLoadRewardedSku(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("rewardToken", paramString1);
    localBundle.putString("playBillingLibraryVersion", paramString2);
    if (paramInt1 != 0) {
      localBundle.putInt("childDirected", paramInt1);
    }
    if (paramInt2 != 0) {
      localBundle.putInt("underAgeOfConsent", paramInt1);
    }
    return localBundle;
  }
  
  public static Bundle constructExtraParamsForQueryPurchases(boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("playBillingLibraryVersion", paramString);
    if ((paramBoolean1) && (paramBoolean2)) {
      localBundle.putBoolean("enablePendingPurchases", true);
    }
    return localBundle;
  }
  
  private static Purchase extractPurchase(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null)) {
      try
      {
        paramString1 = new Purchase(paramString1, paramString2);
        return paramString1;
      }
      catch (JSONException paramString1)
      {
        paramString2 = new StringBuilder();
        paramString2.append("Got JSONException while parsing purchase data: ");
        paramString2.append(paramString1);
        logWarn("BillingHelper", paramString2.toString());
        return null;
      }
    }
    logWarn("BillingHelper", "Received a bad purchase data.");
    return null;
  }
  
  public static List extractPurchases(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    ArrayList localArrayList2 = paramBundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
    ArrayList localArrayList3 = paramBundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
    ArrayList localArrayList1 = new ArrayList();
    int i;
    if ((localArrayList2 != null) && (localArrayList3 != null)) {
      i = 0;
    }
    while ((i < localArrayList2.size()) && (i < localArrayList3.size()))
    {
      paramBundle = extractPurchase((String)localArrayList2.get(i), (String)localArrayList3.get(i));
      if (paramBundle != null) {
        localArrayList1.add(paramBundle);
      }
      i += 1;
      continue;
      logWarn("BillingHelper", "Couldn't find purchase lists, trying to find single data.");
      paramBundle = extractPurchase(paramBundle.getString("INAPP_PURCHASE_DATA"), paramBundle.getString("INAPP_DATA_SIGNATURE"));
      if (paramBundle == null)
      {
        logWarn("BillingHelper", "Couldn't find single purchase data as well.");
        return null;
      }
      localArrayList1.add(paramBundle);
    }
    return localArrayList1;
  }
  
  public static BillingResult getBillingResultFromIntent(Intent paramIntent, String paramString)
  {
    if (paramIntent == null)
    {
      logWarn("BillingHelper", "Got null intent!");
      return BillingResult.newBuilder().setResponseCode(6).setDebugMessage("An internal error occurred.").build();
    }
    return BillingResult.newBuilder().setResponseCode(getResponseCodeFromBundle(paramIntent.getExtras(), paramString)).setDebugMessage(getDebugMessageFromBundle(paramIntent.getExtras(), paramString)).build();
  }
  
  public static String getDebugMessageFromBundle(Bundle paramBundle, String paramString)
  {
    if (paramBundle == null)
    {
      logWarn(paramString, "Unexpected null bundle received!");
      return "";
    }
    paramBundle = paramBundle.get("DEBUG_MESSAGE");
    if (paramBundle == null)
    {
      logVerbose(paramString, "getDebugMessageFromBundle() got null response code, assuming OK");
      return "";
    }
    if ((paramBundle instanceof String)) {
      return (String)paramBundle;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unexpected type for debug message: ");
    localStringBuilder.append(paramBundle.getClass().getName());
    logWarn(paramString, localStringBuilder.toString());
    return "";
  }
  
  public static int getResponseCodeFromBundle(Bundle paramBundle, String paramString)
  {
    if (paramBundle == null)
    {
      logWarn(paramString, "Unexpected null bundle received!");
      return 6;
    }
    paramBundle = paramBundle.get("RESPONSE_CODE");
    if (paramBundle == null)
    {
      logVerbose(paramString, "getResponseCodeFromBundle() got null response code, assuming OK");
      return 0;
    }
    if ((paramBundle instanceof Integer)) {
      return ((Integer)paramBundle).intValue();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unexpected type for bundle response code: ");
    localStringBuilder.append(paramBundle.getClass().getName());
    logWarn(paramString, localStringBuilder.toString());
    return 6;
  }
  
  public static int getResponseCodeFromIntent(Intent paramIntent, String paramString)
  {
    return getBillingResultFromIntent(paramIntent, paramString).getResponseCode();
  }
  
  public static void logVerbose(String paramString1, String paramString2)
  {
    if (Log.isLoggable(paramString1, 2)) {
      Log.v(paramString1, paramString2);
    }
  }
  
  public static void logWarn(String paramString1, String paramString2)
  {
    if (Log.isLoggable(paramString1, 5)) {
      Log.w(paramString1, paramString2);
    }
  }
}
