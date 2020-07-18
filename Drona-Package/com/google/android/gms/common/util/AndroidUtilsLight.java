package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import com.google.android.gms.internal.common.zzg;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@KeepForSdk
public class AndroidUtilsLight
{
  private static volatile int zzgf;
  
  public AndroidUtilsLight() {}
  
  public static Context getDeviceProtectedStorageContext(Context paramContext)
  {
    Context localContext = paramContext;
    if (zzg.zzam()) {
      localContext = zzg.getDeviceProtectedStorageContext(paramContext);
    }
    return localContext;
  }
  
  public static MessageDigest getInstance(String paramString)
  {
    int i = 0;
    while (i < 2)
    {
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
        if (localMessageDigest != null) {
          return localMessageDigest;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        for (;;) {}
      }
      i += 1;
    }
    return null;
  }
  
  public static byte[] getPackageCertificateHashBytes(Context paramContext, String paramString)
    throws PackageManager.NameNotFoundException
  {
    paramContext = Wrappers.packageManager(paramContext).getPackageInfo(paramString, 64);
    if ((signatures != null) && (signatures.length == 1))
    {
      paramString = getInstance("SHA1");
      if (paramString != null) {
        return paramString.digest(signatures[0].toByteArray());
      }
    }
    return null;
  }
}
