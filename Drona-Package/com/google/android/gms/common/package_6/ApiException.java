package com.google.android.gms.common.package_6;

public class ApiException
  extends Exception
{
  protected final Status mStatus;
  
  public ApiException(Status paramStatus)
  {
    super(localStringBuilder.toString());
    mStatus = paramStatus;
  }
  
  public int getStatusCode()
  {
    return mStatus.getStatusCode();
  }
  
  public String getStatusMessage()
  {
    return mStatus.getStatusMessage();
  }
}
