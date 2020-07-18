package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.flags.zze;

public final class ImageCache
{
  private static SharedPreferences mInstance;
  
  public static SharedPreferences getInstance(Context paramContext)
    throws Exception
  {
    try
    {
      if (mInstance == null) {
        mInstance = (SharedPreferences)zze.zza(new Preferences.1(paramContext));
      }
      paramContext = mInstance;
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
}
