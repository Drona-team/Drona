package com.android.billingclient.upgrade;

import org.json.JSONException;

public class PriceChangeFlowParams
{
  private SkuDetails skuDetails;
  
  public PriceChangeFlowParams() {}
  
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
    
    public PriceChangeFlowParams build()
    {
      PriceChangeFlowParams localPriceChangeFlowParams = new PriceChangeFlowParams();
      if (skuDetails != null)
      {
        PriceChangeFlowParams.access$002(localPriceChangeFlowParams, skuDetails);
        return localPriceChangeFlowParams;
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
