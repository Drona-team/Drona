package com.google.android.gms.common;

import android.content.Intent;

public class GooglePlayServicesRepairableException
  extends UserRecoverableException
{
  private final int zzag;
  
  public GooglePlayServicesRepairableException(int paramInt, String paramString, Intent paramIntent)
  {
    super(paramString, paramIntent);
    zzag = paramInt;
  }
  
  public int getConnectionStatusCode()
  {
    return zzag;
  }
}
