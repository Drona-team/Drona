package com.amplitude.upgrade;

import org.json.JSONException;
import org.json.JSONObject;

public class Revenue
{
  public static final String PAGE_KEY = "com.amplitude.api.Revenue";
  private static AmplitudeLog logger = ;
  protected Double price = null;
  protected String productId = null;
  protected JSONObject properties = null;
  protected int quantity = 1;
  protected String receipt = null;
  protected String receiptSig = null;
  protected String revenueType = null;
  
  public Revenue() {}
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (Revenue)paramObject;
      if (quantity != quantity) {
        return false;
      }
      if (productId != null)
      {
        if (!productId.equals(productId)) {
          return false;
        }
      }
      else if (productId != null) {
        return false;
      }
      if (price != null)
      {
        if (!price.equals(price)) {
          return false;
        }
      }
      else if (price != null) {
        return false;
      }
      if (revenueType != null)
      {
        if (!revenueType.equals(revenueType)) {
          return false;
        }
      }
      else if (revenueType != null) {
        return false;
      }
      if (receipt != null)
      {
        if (!receipt.equals(receipt)) {
          return false;
        }
      }
      else if (receipt != null) {
        return false;
      }
      if (receiptSig != null)
      {
        if (!receiptSig.equals(receiptSig)) {
          return false;
        }
      }
      else if (receiptSig != null) {
        return false;
      }
      if (properties != null) {
        if (Utils.compareJSONObjects(properties, properties)) {
          break label239;
        }
      } else if (properties == null) {
        return true;
      }
      return false;
    }
    else
    {
      return false;
    }
    label239:
    return true;
  }
  
  public int hashCode()
  {
    String str = productId;
    int i1 = 0;
    int i;
    if (str != null) {
      i = productId.hashCode();
    } else {
      i = 0;
    }
    int i2 = quantity;
    int j;
    if (price != null) {
      j = price.hashCode();
    } else {
      j = 0;
    }
    int k;
    if (revenueType != null) {
      k = revenueType.hashCode();
    } else {
      k = 0;
    }
    int m;
    if (receipt != null) {
      m = receipt.hashCode();
    } else {
      m = 0;
    }
    int n;
    if (receiptSig != null) {
      n = receiptSig.hashCode();
    } else {
      n = 0;
    }
    if (properties != null) {
      i1 = properties.hashCode();
    }
    return (((((i * 31 + i2) * 31 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i1;
  }
  
  protected boolean isValidRevenue()
  {
    if (price == null)
    {
      logger.w("com.amplitude.api.Revenue", "Invalid revenue, need to set price");
      return false;
    }
    return true;
  }
  
  public Revenue setEventProperties(JSONObject paramJSONObject)
  {
    properties = Utils.cloneJSONObject(paramJSONObject);
    return this;
  }
  
  public Revenue setPrice(double paramDouble)
  {
    price = Double.valueOf(paramDouble);
    return this;
  }
  
  public Revenue setProductId(String paramString)
  {
    if (Utils.isEmptyString(paramString))
    {
      logger.w("com.amplitude.api.Revenue", "Invalid empty productId");
      return this;
    }
    productId = paramString;
    return this;
  }
  
  public Revenue setQuantity(int paramInt)
  {
    quantity = paramInt;
    return this;
  }
  
  public Revenue setReceipt(String paramString1, String paramString2)
  {
    receipt = paramString1;
    receiptSig = paramString2;
    return this;
  }
  
  public Revenue setRevenueProperties(JSONObject paramJSONObject)
  {
    logger.w("com.amplitude.api.Revenue", "setRevenueProperties is deprecated, please use setEventProperties instead");
    return setEventProperties(paramJSONObject);
  }
  
  public Revenue setRevenueType(String paramString)
  {
    revenueType = paramString;
    return this;
  }
  
  protected JSONObject toJSONObject()
  {
    JSONObject localJSONObject;
    if (properties == null) {
      localJSONObject = new JSONObject();
    } else {
      localJSONObject = properties;
    }
    Object localObject = productId;
    try
    {
      localJSONObject.put("$productId", localObject);
      int i = quantity;
      localJSONObject.put("$quantity", i);
      localObject = price;
      localJSONObject.put("$price", localObject);
      localObject = revenueType;
      localJSONObject.put("$revenueType", localObject);
      localObject = receipt;
      localJSONObject.put("$receipt", localObject);
      localObject = receiptSig;
      localJSONObject.put("$receiptSig", localObject);
      return localJSONObject;
    }
    catch (JSONException localJSONException)
    {
      logger.e("com.amplitude.api.Revenue", String.format("Failed to convert revenue object to JSON: %s", new Object[] { localJSONException.toString() }));
    }
    return localJSONObject;
  }
}
