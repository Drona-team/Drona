package com.google.android.gms.common.wrappers;

import android.content.Context;
import com.google.android.gms.common.annotation.KeepForSdk;

@KeepForSdk
public class Wrappers
{
  private static Wrappers zzhz = new Wrappers();
  private PackageManagerWrapper zzhy = null;
  
  public Wrappers() {}
  
  private final PackageManagerWrapper getAppVersion(Context paramContext)
  {
    try
    {
      if (zzhy == null)
      {
        if (paramContext.getApplicationContext() != null) {
          paramContext = paramContext.getApplicationContext();
        }
        zzhy = new PackageManagerWrapper(paramContext);
      }
      paramContext = zzhy;
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static PackageManagerWrapper packageManager(Context paramContext)
  {
    return zzhz.getAppVersion(paramContext);
  }
}
