package com.android.billingclient.upgrade;

public final class AcknowledgePurchaseParams
{
  private String developerPayload;
  private String purchaseToken;
  
  private AcknowledgePurchaseParams() {}
  
  public static Builder newBuilder()
  {
    return new Builder(null);
  }
  
  public String getDeveloperPayload()
  {
    return developerPayload;
  }
  
  public String getPurchaseToken()
  {
    return purchaseToken;
  }
  
  public final class Builder
  {
    private String developerPayload;
    private String purchaseToken;
    
    private Builder() {}
    
    public AcknowledgePurchaseParams build()
    {
      AcknowledgePurchaseParams localAcknowledgePurchaseParams = new AcknowledgePurchaseParams(null);
      AcknowledgePurchaseParams.access$202(localAcknowledgePurchaseParams, developerPayload);
      AcknowledgePurchaseParams.access$302(localAcknowledgePurchaseParams, purchaseToken);
      return localAcknowledgePurchaseParams;
    }
    
    public Builder setDeveloperPayload(String paramString)
    {
      developerPayload = paramString;
      return this;
    }
    
    public Builder setPurchaseToken(String paramString)
    {
      purchaseToken = paramString;
      return this;
    }
  }
}
