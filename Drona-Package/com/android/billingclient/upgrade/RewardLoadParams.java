package com.android.billingclient.upgrade;

import org.json.JSONException;

public class RewardLoadParams
{
  private SkuDetails skuDetails;
  
  public RewardLoadParams() {}
  
  public static Builder newBuilder()
  {
    return new Builder();
  }
  
  public SkuDetails getSkuDetails()
  {
    return skuDetails;
  }
  
  public class Builder
  {
    private SkuDetails skuDetails;
    
    public Builder() {}
    
    private Builder setSkuDetails(String paramString)
    {
      try
      {
        paramString = new SkuDetails(paramString);
        skuDetails = paramString;
        return this;
      }
      catch (JSONException paramString)
      {
        for (;;) {}
      }
      throw new RuntimeException("Incorrect skuDetails JSON object!");
    }
    
    public RewardLoadParams build()
    {
      RewardLoadParams localRewardLoadParams = new RewardLoadParams();
      if (skuDetails != null)
      {
        RewardLoadParams.access$002(localRewardLoadParams, skuDetails);
        return localRewardLoadParams;
      }
      throw new RuntimeException("SkuDetails must be set");
    }
    
    public Builder setSkuDetails(SkuDetails paramSkuDetails)
    {
      skuDetails = paramSkuDetails;
      return this;
    }
  }
}
