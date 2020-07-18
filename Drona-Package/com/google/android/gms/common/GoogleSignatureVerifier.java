package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ShowFirstParty;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@KeepForSdk
@ShowFirstParty
public class GoogleSignatureVerifier
{
  private static GoogleSignatureVerifier zzam;
  private final Context mContext;
  private volatile String zzan;
  
  private GoogleSignatureVerifier(Context paramContext)
  {
    mContext = paramContext.getApplicationContext();
  }
  
  public static boolean backup(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    if ((paramPackageInfo != null) && (signatures != null))
    {
      if (paramBoolean) {
        paramPackageInfo = create(paramPackageInfo, DataProvider.TAG);
      } else {
        paramPackageInfo = create(paramPackageInfo, new Type[] { DataProvider.TAG[0] });
      }
      if (paramPackageInfo != null) {
        return true;
      }
    }
    return false;
  }
  
  private final Location create(String paramString)
  {
    if (paramString == null) {
      return Location.create("null pkg");
    }
    if (paramString.equals(zzan)) {
      return Location.load();
    }
    Object localObject = mContext;
    try
    {
      PackageInfo localPackageInfo = Wrappers.packageManager((Context)localObject).getPackageInfo(paramString, 64);
      boolean bool = GooglePlayServicesUtilLight.honorsDebugCertificates(mContext);
      if (localPackageInfo == null)
      {
        localObject = Location.create("null pkg");
      }
      else if (signatures.length != 1)
      {
        localObject = Location.create("single cert required");
      }
      else
      {
        Sha256Hash localSha256Hash = new Sha256Hash(signatures[0].toByteArray());
        String str = packageName;
        Location localLocation = Utils.get(str, localSha256Hash, bool, false);
        localObject = localLocation;
        if (zzad)
        {
          localObject = localLocation;
          if (applicationInfo != null)
          {
            localObject = localLocation;
            if ((applicationInfo.flags & 0x2) != 0)
            {
              localObject = localLocation;
              if (getzzad) {
                localObject = Location.create("debuggable release cert app rejected");
              }
            }
          }
        }
      }
      if (!zzad) {
        return localObject;
      }
      zzan = paramString;
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
      return localNameNotFoundException;
    }
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {
      paramString = "no pkg ".concat(paramString);
    } else {
      paramString = new String("no pkg ");
    }
    return Location.create(paramString);
  }
  
  private final Location create(String paramString, int paramInt)
  {
    Object localObject = mContext;
    Location localLocation;
    try
    {
      PackageInfo localPackageInfo = Wrappers.packageManager((Context)localObject).getPackageInfo(paramString, 64, paramInt);
      localObject = mContext;
      boolean bool = GooglePlayServicesUtilLight.honorsDebugCertificates((Context)localObject);
      if (localPackageInfo == null)
      {
        localObject = Location.create("null pkg");
        return localObject;
      }
      if (signatures.length != 1)
      {
        localObject = Location.create("single cert required");
        return localObject;
      }
      localObject = signatures[0];
      Sha256Hash localSha256Hash = new Sha256Hash(((Signature)localObject).toByteArray());
      String str = packageName;
      localLocation = Utils.get(str, localSha256Hash, bool, false);
      localObject = localLocation;
      if (zzad)
      {
        if ((applicationInfo == null) || ((applicationInfo.flags & 0x2) == 0)) {
          break label212;
        }
        localObject = Utils.get(str, localSha256Hash, false, true);
        if (!zzad) {
          break label212;
        }
        localObject = Location.create("debuggable release cert app rejected");
      }
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
    }
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {
      paramString = "no pkg ".concat(paramString);
    } else {
      paramString = new String("no pkg ");
    }
    return Location.create(paramString);
    label212:
    return localLocation;
  }
  
  private static Type create(PackageInfo paramPackageInfo, Type... paramVarArgs)
  {
    if (signatures == null) {
      return null;
    }
    if (signatures.length != 1)
    {
      Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
      return null;
    }
    paramPackageInfo = signatures;
    int i = 0;
    paramPackageInfo = new Sha256Hash(paramPackageInfo[0].toByteArray());
    while (i < paramVarArgs.length)
    {
      if (paramVarArgs[i].equals(paramPackageInfo)) {
        return paramVarArgs[i];
      }
      i += 1;
    }
    return null;
  }
  
  public static GoogleSignatureVerifier getInstance(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext);
    try
    {
      if (zzam == null)
      {
        Utils.init(paramContext);
        zzam = new GoogleSignatureVerifier(paramContext);
      }
      return zzam;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public boolean isGooglePublicSignedPackage(PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo == null) {
      return false;
    }
    if (backup(paramPackageInfo, false)) {
      return true;
    }
    if (backup(paramPackageInfo, true))
    {
      if (GooglePlayServicesUtilLight.honorsDebugCertificates(mContext)) {
        return true;
      }
      Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
    }
    return false;
  }
  
  public boolean isPackageGoogleSigned(String paramString)
  {
    paramString = create(paramString);
    paramString.save();
    return zzad;
  }
  
  public boolean isUidGoogleSigned(int paramInt)
  {
    String[] arrayOfString = Wrappers.packageManager(mContext).getPackagesForUid(paramInt);
    if ((arrayOfString != null) && (arrayOfString.length != 0))
    {
      Object localObject1 = null;
      int j = arrayOfString.length;
      int i = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (i >= j) {
          break;
        }
        Location localLocation = create(arrayOfString[i], paramInt);
        localObject1 = localLocation;
        localObject2 = localObject1;
        if (zzad) {
          break;
        }
        i += 1;
      }
    }
    Object localObject2 = Location.create("no pkgs");
    ((Location)localObject2).save();
    return zzad;
  }
}
