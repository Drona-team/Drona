package com.bumptech.glide.signature;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.bumptech.glide.load.Key;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ApplicationVersionSignature
{
  private static final String EVENTLOG_URL = "AppVersionSignature";
  private static final ConcurrentMap<String, Key> PACKAGE_NAME_TO_KEY = new ConcurrentHashMap();
  
  private ApplicationVersionSignature() {}
  
  private static PackageInfo getPackageInfo(Context paramContext)
  {
    try
    {
      PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      return localPackageInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot resolve info for");
      localStringBuilder.append(paramContext.getPackageName());
      Log.e("AppVersionSignature", localStringBuilder.toString(), localNameNotFoundException);
    }
    return null;
  }
  
  private static String getVersionCode(PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo != null) {
      return String.valueOf(versionCode);
    }
    return UUID.randomUUID().toString();
  }
  
  public static Key obtain(Context paramContext)
  {
    Object localObject = paramContext.getPackageName();
    Key localKey = (Key)PACKAGE_NAME_TO_KEY.get(localObject);
    if (localKey == null)
    {
      paramContext = obtainVersionSignature(paramContext);
      localObject = (Key)PACKAGE_NAME_TO_KEY.putIfAbsent(localObject, paramContext);
      if (localObject == null) {
        return paramContext;
      }
      return localObject;
    }
    return localKey;
  }
  
  private static Key obtainVersionSignature(Context paramContext)
  {
    return new ObjectKey(getVersionCode(getPackageInfo(paramContext)));
  }
  
  static void reset()
  {
    PACKAGE_NAME_TO_KEY.clear();
  }
}
