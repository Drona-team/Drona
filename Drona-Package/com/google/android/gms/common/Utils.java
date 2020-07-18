package com.google.android.gms.common;

import android.content.Context;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import com.google.android.gms.common.internal.ICMStatusBarManager;
import com.google.android.gms.common.internal.ICMStatusBarManager.Stub;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.LoadingException;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
final class Utils
{
  private static final Object EMPTY = new Object();
  private static Context context;
  private static volatile ICMStatusBarManager mService;
  
  static Location get(String paramString, Type paramType, boolean paramBoolean1, boolean paramBoolean2)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskReads();
    try
    {
      paramString = parse(paramString, paramType, paramBoolean1, paramBoolean2);
      StrictMode.setThreadPolicy(localThreadPolicy);
      return paramString;
    }
    catch (Throwable paramString)
    {
      StrictMode.setThreadPolicy(localThreadPolicy);
      throw paramString;
    }
  }
  
  static void init(Context paramContext)
  {
    try
    {
      if (context == null)
      {
        if (paramContext != null) {
          context = paramContext.getApplicationContext();
        }
      }
      else {
        Log.w("GoogleCertificates", "GoogleCertificates has been initialized already");
      }
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  private static Location parse(String paramString, Type paramType, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject;
    if (mService == null) {
      localObject = context;
    }
    try
    {
      Preconditions.checkNotNull(localObject);
      localObject = EMPTY;
      try
      {
        if (mService == null) {
          mService = ICMStatusBarManager.Stub.asInterface(DynamiteModule.load(context, DynamiteModule.PREFER_HIGHEST_OR_LOCAL_VERSION_NO_FORCE_STAGING, "com.google.android.gms.googlecertificates").instantiate("com.google.android.gms.common.GoogleCertificatesImpl"));
        }
      }
      catch (Throwable paramString)
      {
        throw paramString;
      }
      Preconditions.checkNotNull(context);
      localObject = new Command(paramString, paramType, paramBoolean1, paramBoolean2);
      ICMStatusBarManager localICMStatusBarManager = mService;
      Context localContext = context;
      try
      {
        paramBoolean2 = localICMStatusBarManager.registerListener((Command)localObject, ObjectWrapper.wrap(localContext.getPackageManager()));
        if (paramBoolean2) {
          return Location.load();
        }
        return Location.parse(new IntegerPolynomial.CombineTask(paramBoolean1, paramString, paramType));
      }
      catch (RemoteException paramString)
      {
        Log.e("GoogleCertificates", "Failed to get Google certificates from remote", paramString);
        return Location.create("module call", paramString);
      }
      return Location.create(paramString, paramType);
    }
    catch (DynamiteModule.LoadingException paramType)
    {
      Log.e("GoogleCertificates", "Failed to get Google certificates from remote", paramType);
      paramString = String.valueOf(paramType.getMessage());
      if (paramString.length() != 0) {
        paramString = "module init: ".concat(paramString);
      } else {
        paramString = new String("module init: ");
      }
    }
  }
}
