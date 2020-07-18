package com.google.android.gms.common;

import android.util.Log;
import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.android.gms.common.util.IpAddress;
import java.security.MessageDigest;
import java.util.concurrent.Callable;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
class Location
{
  private static final Location zzac = new Location(true, null, null);
  private final Throwable cause;
  final boolean zzad;
  private final String zzae;
  
  Location(boolean paramBoolean, String paramString, Throwable paramThrowable)
  {
    zzad = paramBoolean;
    zzae = paramString;
    cause = paramThrowable;
  }
  
  static Location create(String paramString)
  {
    return new Location(false, paramString, null);
  }
  
  static Location create(String paramString, Throwable paramThrowable)
  {
    return new Location(false, paramString, paramThrowable);
  }
  
  static Location load()
  {
    return zzac;
  }
  
  static Location parse(Callable paramCallable)
  {
    return new Result(paramCallable, null);
  }
  
  static String toString(String paramString, Type paramType, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str;
    if (paramBoolean2) {
      str = "debug cert rejected";
    } else {
      str = "not whitelisted";
    }
    return String.format("%s: pkg=%s, sha1=%s, atk=%s, ver=%s", new Object[] { str, paramString, IpAddress.bytesToStringLowercase(AndroidUtilsLight.getInstance("SHA-1").digest(paramType.getBytes())), Boolean.valueOf(paramBoolean1), "12451009.false" });
  }
  
  String getErrorMessage()
  {
    return zzae;
  }
  
  final void save()
  {
    if ((!zzad) && (Log.isLoggable("GoogleCertificatesRslt", 3)))
    {
      if (cause != null)
      {
        Log.d("GoogleCertificatesRslt", getErrorMessage(), cause);
        return;
      }
      Log.d("GoogleCertificatesRslt", getErrorMessage());
    }
  }
}
