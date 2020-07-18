package com.google.android.gms.common.package_6;

import com.google.android.gms.common.Feature;

public final class UnsupportedApiCallException
  extends UnsupportedOperationException
{
  private final Feature zzas;
  
  public UnsupportedApiCallException(Feature paramFeature)
  {
    zzas = paramFeature;
  }
  
  public final String getMessage()
  {
    String str = String.valueOf(zzas);
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 8);
    localStringBuilder.append("Missing ");
    localStringBuilder.append(str);
    return localStringBuilder.toString();
  }
}
