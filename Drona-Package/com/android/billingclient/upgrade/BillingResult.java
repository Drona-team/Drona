package com.android.billingclient.upgrade;

public final class BillingResult
{
  private String mDebugMessage;
  private int mResponseCode;
  
  public BillingResult() {}
  
  public static Builder newBuilder()
  {
    return new Builder(null);
  }
  
  public String getDebugMessage()
  {
    return mDebugMessage;
  }
  
  public int getResponseCode()
  {
    return mResponseCode;
  }
  
  public class Builder
  {
    private String mDebugMessage;
    private int mResponseCode;
    
    private Builder() {}
    
    public BillingResult build()
    {
      BillingResult localBillingResult = new BillingResult();
      BillingResult.access$102(localBillingResult, mResponseCode);
      BillingResult.access$202(localBillingResult, mDebugMessage);
      return localBillingResult;
    }
    
    public Builder setDebugMessage(String paramString)
    {
      mDebugMessage = paramString;
      return this;
    }
    
    public Builder setResponseCode(int paramInt)
    {
      mResponseCode = paramInt;
      return this;
    }
  }
}