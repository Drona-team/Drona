package com.google.android.gms.common.wrappers;

import android.content.Context;
import android.content.pm.PackageManager;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.util.PlatformVersion;

@KeepForSdk
public class InstantApps
{
  private static Context zzhv;
  private static Boolean zzhw;
  
  public InstantApps() {}
  
  public static boolean isInstantApp(Context paramContext)
  {
    for (;;)
    {
      try
      {
        localContext = paramContext.getApplicationContext();
        if ((zzhv != null) && (zzhw != null) && (zzhv == localContext))
        {
          bool = zzhw.booleanValue();
          return bool;
        }
        zzhw = null;
        if (PlatformVersion.isAtLeastO()) {
          zzhw = Boolean.valueOf(localContext.getPackageManager().isInstantApp());
        }
      }
      catch (Throwable paramContext)
      {
        Context localContext;
        boolean bool;
        throw paramContext;
      }
      try
      {
        paramContext.getClassLoader().loadClass("com.google.android.instantapps.supervisor.InstantAppsRuntime");
        zzhw = Boolean.valueOf(true);
      }
      catch (ClassNotFoundException paramContext) {}
    }
    zzhw = Boolean.valueOf(false);
    zzhv = localContext;
    bool = zzhw.booleanValue();
    return bool;
  }
}
