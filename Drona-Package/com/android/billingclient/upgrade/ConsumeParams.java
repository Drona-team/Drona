package com.android.billingclient.upgrade;

public final class ConsumeParams
{
  private String developerPayload;
  private String purchaseToken;
  
  private ConsumeParams() {}
  
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
    
    public ConsumeParams build()
    {
      ConsumeParams localConsumeParams = new ConsumeParams(null);
      ConsumeParams.access$202(localConsumeParams, purchaseToken);
      ConsumeParams.access$302(localConsumeParams, developerPayload);
      return localConsumeParams;
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
