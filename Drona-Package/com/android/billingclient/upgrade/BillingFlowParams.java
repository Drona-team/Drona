package com.android.billingclient.upgrade;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;

public class BillingFlowParams
{
  public static final String EXTRA_PARAM_CHILD_DIRECTED = "childDirected";
  public static final String EXTRA_PARAM_KEY_ACCOUNT_ID = "accountId";
  public static final String EXTRA_PARAM_KEY_DEVELOPER_ID = "developerId";
  public static final String EXTRA_PARAM_KEY_OLD_SKUS = "skusToReplace";
  public static final String EXTRA_PARAM_KEY_REPLACE_SKUS_PRORATION_MODE = "prorationMode";
  public static final String EXTRA_PARAM_KEY_RSKU = "rewardToken";
  public static final String EXTRA_PARAM_KEY_VR = "vr";
  public static final String EXTRA_PARAM_UNDER_AGE_OF_CONSENT = "underAgeOfConsent";
  private String mAccountId;
  private String mDeveloperId;
  private String mOldSku;
  private int mReplaceSkusProrationMode = 0;
  private SkuDetails mSkuDetails;
  private boolean mVrPurchaseFlow;
  
  public BillingFlowParams() {}
  
  public static Builder newBuilder()
  {
    return new Builder(null);
  }
  
  public String getAccountId()
  {
    return mAccountId;
  }
  
  public String getDeveloperId()
  {
    return mDeveloperId;
  }
  
  public String getOldSku()
  {
    return mOldSku;
  }
  
  public ArrayList getOldSkus()
  {
    return new ArrayList(Arrays.asList(new String[] { mOldSku }));
  }
  
  public int getReplaceSkusProrationMode()
  {
    return mReplaceSkusProrationMode;
  }
  
  public String getSku()
  {
    if (mSkuDetails == null) {
      return null;
    }
    return mSkuDetails.getSku();
  }
  
  public SkuDetails getSkuDetails()
  {
    return mSkuDetails;
  }
  
  public String getSkuType()
  {
    if (mSkuDetails == null) {
      return null;
    }
    return mSkuDetails.getType();
  }
  
  public boolean getVrPurchaseFlow()
  {
    return mVrPurchaseFlow;
  }
  
  boolean hasExtraParams()
  {
    return (mVrPurchaseFlow) || (mAccountId != null) || (mDeveloperId != null) || (mReplaceSkusProrationMode != 0);
  }
  
  public class Builder
  {
    private String mAccountId;
    private String mDeveloperId;
    private String mOldSku;
    private int mReplaceSkusProrationMode = 0;
    private SkuDetails mSkuDetails;
    private boolean mVrPurchaseFlow;
    
    private Builder() {}
    
    private Builder setSkuDetails(String paramString)
    {
      try
      {
        paramString = new SkuDetails(paramString);
        mSkuDetails = paramString;
        return this;
      }
      catch (JSONException paramString)
      {
        for (;;) {}
      }
      throw new RuntimeException("Incorrect skuDetails JSON object!");
    }
    
    public Builder addOldSku(String paramString)
    {
      mOldSku = paramString;
      return this;
    }
    
    public BillingFlowParams build()
    {
      BillingFlowParams localBillingFlowParams = new BillingFlowParams();
      BillingFlowParams.access$102(localBillingFlowParams, mSkuDetails);
      BillingFlowParams.access$202(localBillingFlowParams, mOldSku);
      BillingFlowParams.access$302(localBillingFlowParams, mAccountId);
      BillingFlowParams.access$402(localBillingFlowParams, mVrPurchaseFlow);
      BillingFlowParams.access$502(localBillingFlowParams, mReplaceSkusProrationMode);
      BillingFlowParams.access$602(localBillingFlowParams, mDeveloperId);
      return localBillingFlowParams;
    }
    
    public Builder setAccountId(String paramString)
    {
      mAccountId = paramString;
      return this;
    }
    
    public Builder setDeveloperId(String paramString)
    {
      mDeveloperId = paramString;
      return this;
    }
    
    public Builder setOldSku(String paramString)
    {
      mOldSku = paramString;
      return this;
    }
    
    public Builder setOldSkus(ArrayList paramArrayList)
    {
      if ((paramArrayList != null) && (paramArrayList.size() > 0)) {
        mOldSku = ((String)paramArrayList.get(0));
      }
      return this;
    }
    
    public Builder setReplaceSkusProrationMode(int paramInt)
    {
      mReplaceSkusProrationMode = paramInt;
      return this;
    }
    
    public Builder setSkuDetails(SkuDetails paramSkuDetails)
    {
      mSkuDetails = paramSkuDetails;
      return this;
    }
    
    public Builder setVrPurchaseFlow(boolean paramBoolean)
    {
      mVrPurchaseFlow = paramBoolean;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface ProrationMode
  {
    public static final int DEFERRED = 4;
    public static final int IMMEDIATE_AND_CHARGE_PRORATED_PRICE = 2;
    public static final int IMMEDIATE_WITHOUT_PRORATION = 3;
    public static final int IMMEDIATE_WITH_TIME_PRORATION = 1;
    public static final int UNKNOWN_SUBSCRIPTION_UPGRADE_DOWNGRADE_POLICY = 0;
  }
}
