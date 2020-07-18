package com.android.billingclient.upgrade;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseHistoryRecord
{
  private final String mOriginalJson;
  private final JSONObject mParsedJson;
  private final String mSignature;
  
  public PurchaseHistoryRecord(String paramString1, String paramString2)
    throws JSONException
  {
    mOriginalJson = paramString1;
    mSignature = paramString2;
    mParsedJson = new JSONObject(mOriginalJson);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PurchaseHistoryRecord)) {
      return false;
    }
    paramObject = (PurchaseHistoryRecord)paramObject;
    return (TextUtils.equals(mOriginalJson, paramObject.getOriginalJson())) && (TextUtils.equals(mSignature, paramObject.getSignature()));
  }
  
  public String getDeveloperPayload()
  {
    return mParsedJson.optString("developerPayload");
  }
  
  public String getOriginalJson()
  {
    return mOriginalJson;
  }
  
  public long getPurchaseTime()
  {
    return mParsedJson.optLong("purchaseTime");
  }
  
  public String getPurchaseToken()
  {
    return mParsedJson.optString("token", mParsedJson.optString("purchaseToken"));
  }
  
  public String getSignature()
  {
    return mSignature;
  }
  
  public String getSku()
  {
    return mParsedJson.optString("productId");
  }
  
  public int hashCode()
  {
    return mOriginalJson.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PurchaseHistoryRecord. Json: ");
    localStringBuilder.append(mOriginalJson);
    return localStringBuilder.toString();
  }
}
