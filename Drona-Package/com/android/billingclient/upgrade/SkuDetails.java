package com.android.billingclient.upgrade;

import android.text.TextUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails
{
  private final String mOriginalJson;
  private final JSONObject mParsedJson;
  
  public SkuDetails(String paramString)
    throws JSONException
  {
    mOriginalJson = paramString;
    mParsedJson = new JSONObject(mOriginalJson);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (SkuDetails)paramObject;
      return TextUtils.equals(mOriginalJson, mOriginalJson);
    }
    return false;
  }
  
  public String getDescription()
  {
    return mParsedJson.optString("description");
  }
  
  public String getFreeTrialPeriod()
  {
    return mParsedJson.optString("freeTrialPeriod");
  }
  
  public String getIconUrl()
  {
    return mParsedJson.optString("iconUrl");
  }
  
  public String getIntroductoryPrice()
  {
    return mParsedJson.optString("introductoryPrice");
  }
  
  public long getIntroductoryPriceAmountMicros()
  {
    return mParsedJson.optLong("introductoryPriceAmountMicros");
  }
  
  public String getIntroductoryPriceCycles()
  {
    return mParsedJson.optString("introductoryPriceCycles");
  }
  
  public String getIntroductoryPricePeriod()
  {
    return mParsedJson.optString("introductoryPricePeriod");
  }
  
  public String getOriginalJson()
  {
    return mOriginalJson;
  }
  
  public String getOriginalPrice()
  {
    if (mParsedJson.has("original_price")) {
      return mParsedJson.optString("original_price");
    }
    return getPrice();
  }
  
  public long getOriginalPriceAmountMicros()
  {
    if (mParsedJson.has("original_price_micros")) {
      return mParsedJson.optLong("original_price_micros");
    }
    return getPriceAmountMicros();
  }
  
  public String getPrice()
  {
    return mParsedJson.optString("price");
  }
  
  public long getPriceAmountMicros()
  {
    return mParsedJson.optLong("price_amount_micros");
  }
  
  public String getPriceCurrencyCode()
  {
    return mParsedJson.optString("price_currency_code");
  }
  
  public String getSku()
  {
    return mParsedJson.optString("productId");
  }
  
  String getSkuDetailsToken()
  {
    return mParsedJson.optString("skuDetailsToken");
  }
  
  public String getSubscriptionPeriod()
  {
    return mParsedJson.optString("subscriptionPeriod");
  }
  
  public String getTitle()
  {
    return mParsedJson.optString("title");
  }
  
  public String getType()
  {
    return mParsedJson.optString("type");
  }
  
  public int hashCode()
  {
    return mOriginalJson.hashCode();
  }
  
  public boolean isRewarded()
  {
    return mParsedJson.has("rewardToken");
  }
  
  String rewardToken()
  {
    return mParsedJson.optString("rewardToken");
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SkuDetails: ");
    localStringBuilder.append(mOriginalJson);
    return localStringBuilder.toString();
  }
  
  public class SkuDetailsResult
  {
    private String mDebugMessage;
    private int mResponseCode;
    private List<com.android.billingclient.api.SkuDetails> mSkuDetailsList;
    
    public SkuDetailsResult(String paramString, List paramList)
    {
      mResponseCode = this$1;
      mDebugMessage = paramString;
      mSkuDetailsList = paramList;
    }
    
    public String getDebugMessage()
    {
      return mDebugMessage;
    }
    
    public int getResponseCode()
    {
      return mResponseCode;
    }
    
    public List getSkuDetailsList()
    {
      return mSkuDetailsList;
    }
  }
}
