package com.android.billingclient.upgrade;

import java.util.ArrayList;
import java.util.List;

public class SkuDetailsParams
{
  private String mSkuType;
  private List<String> mSkusList;
  
  public SkuDetailsParams() {}
  
  public static Builder newBuilder()
  {
    return new Builder(null);
  }
  
  public String getSkuType()
  {
    return mSkuType;
  }
  
  public List getSkusList()
  {
    return mSkusList;
  }
  
  public class Builder
  {
    private String mSkuType;
    private List<String> mSkusList;
    
    private Builder() {}
    
    public SkuDetailsParams build()
    {
      SkuDetailsParams localSkuDetailsParams = new SkuDetailsParams();
      SkuDetailsParams.access$102(localSkuDetailsParams, mSkuType);
      SkuDetailsParams.access$202(localSkuDetailsParams, mSkusList);
      return localSkuDetailsParams;
    }
    
    public Builder setSkusList(List paramList)
    {
      mSkusList = new ArrayList(paramList);
      return this;
    }
    
    public Builder setType(String paramString)
    {
      mSkuType = paramString;
      return this;
    }
  }
}
