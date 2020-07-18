package com.google.android.gms.package_7;

import javax.annotation.concurrent.GuardedBy;

public abstract class zzai
{
  @GuardedBy("SdkFlagFactory.class")
  private static zzai zzdd;
  
  public zzai() {}
  
  public static zzai getCryptoKeys()
  {
    try
    {
      if (zzdd == null) {
        zzdd = new zzac();
      }
      zzai localZzai = zzdd;
      return localZzai;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public abstract zzaj getString(String paramString, boolean paramBoolean);
}
